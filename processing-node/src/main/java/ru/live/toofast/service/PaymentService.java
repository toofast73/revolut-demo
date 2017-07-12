package ru.live.toofast.service;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ignite.Ignition;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.account.AccountStatus;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.exception.AccountValidationException;
import ru.live.toofast.exception.EntityNotFoundException;
import ru.live.toofast.exception.NotEnoughFundsException;
import ru.live.toofast.repository.AccountRepository;
import ru.live.toofast.repository.PaymentRepository;
import ru.live.toofast.repository.BalanceEntryRepository;

import java.math.BigDecimal;

public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final BalanceEntryRepository balanceEntryRepository;
    private final FeeService feeService;

    public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository, BalanceEntryRepository balanceEntryRepository, FeeService feeService) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.balanceEntryRepository = balanceEntryRepository;
        this.feeService = feeService;
    }

    /**
     * Execute the payment -- transfer money between two accounts.
     * It's executed in a transaction and protected from concurrent access via pessimistic locking.
     */
    public Payment processPayment(Payment payment) {
        try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
            paymentRepository.store(payment);

            Pair<Account, Account> sourceAndDestination = accountRepository.getAccounts(payment);
            Account source = sourceAndDestination.getLeft();
            Account destination = sourceAndDestination.getRight();
            BigDecimal amount = payment.getAmount();

            validateAccounts(source, destination, amount);

            source.decreaseBalance(amount);
            destination.increaseBalance(amount);

            feeService.collectFee(payment, source, destination);

            accountRepository.store(source);
            accountRepository.store(destination);

            payment.completed();
            paymentRepository.store(payment);

            balanceEntryRepository.registerPayment(payment);
            tx.commit();
            return payment;
        }
    }

    /**
     * Check that both accounts exist, are ACTIVE and source account have enough money to fund the transfer.
     */
    private void validateAccounts(Account source, Account destination, BigDecimal amount) {
        validate(source);
        validate(destination);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughFundsException(String.format("Account %s has not enough funds", source.getId()));
        }
    }

    private void validate(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountValidationException(String.format("Account %s is not ACTIVE", account.getId()));
        }
    }

    public Payment getPaymentStatus(long paymentId) {
        if (!paymentRepository.contains(paymentId)) {
            throw new EntityNotFoundException(String.format("Payment with id %s was not found", paymentId));
        }
        return paymentRepository.get(paymentId);
    }
}

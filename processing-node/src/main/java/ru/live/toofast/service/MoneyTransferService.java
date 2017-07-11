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
import ru.live.toofast.exception.NotEnoughFundsException;
import ru.live.toofast.repository.AccountRepository;
import ru.live.toofast.repository.PaymentRepository;
import ru.live.toofast.repository.TransactionRepository;

import java.math.BigDecimal;

public class MoneyTransferService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final FeeService feeService;

    public MoneyTransferService(AccountRepository accountRepository, PaymentRepository paymentRepository, TransactionRepository transactionRepository, FeeService feeService) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.feeService = feeService;
    }

    public Payment processPayment(Payment payment){
        try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
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

            transactionRepository.registerPayment(payment);

            payment.completed();
            paymentRepository.store(payment);

            tx.commit();
            return payment;
        }
    }

    private void validateAccounts(Account source, Account destination, BigDecimal amount) {
        validate(source);
        validate(destination);

        if(source.getBalance().compareTo(amount) < 0){
            throw new NotEnoughFundsException(String.format("Account %s has not enough funds", source.getId()));
        }
    }

    private void validate(Account account) {
        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new AccountValidationException(String.format("Account %s is not ACTIVE", account.getId()));
        }
    }
}

package ru.live.toofast.service

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import ru.live.toofast.PaymentProcessingApplication
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.entity.payment.PaymentDirection
import ru.live.toofast.entity.payment.BalanceEntry
import ru.live.toofast.exception.AccountValidationException
import ru.live.toofast.exception.NotEnoughFundsException
import ru.live.toofast.repository.AccountRepository
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Callable

import static java.util.concurrent.Executors.newFixedThreadPool

class PaymentServiceTest extends Specification {

    @Shared
    Ignite ignite = Ignition.getOrStart(new IgniteConfiguration())

    @Shared
    AccountRepository accountRepository = PaymentProcessingApplication.accountRepository()
    @Shared
    TransactionRepository = PaymentProcessingApplication.transactionRepository();

    PaymentService moneyTransferService = PaymentProcessingApplication.paymentService()

    IgniteAtomicSequence accountSequence = PaymentProcessingApplication.accountSequence();
    IgniteAtomicSequence paymentSequence = PaymentProcessingApplication.paymentSequence();

    ListeningExecutorService decorator = MoreExecutors.listeningDecorator(newFixedThreadPool(30));

    def "Transfer USD150 from one account to another"(){

        setup:
        List<Account> accountList = generateAccounts(2, accountRepository)

        when:
        Payment payment = createPayment(accountList.first().id, accountList.last().id, 100.0)

        moneyTransferService.processPayment(payment)


        then:
        accountRepository.get(accountList.first().id).balance.doubleValue() == 9899.0;
        accountRepository.get(accountList.last().id).balance.doubleValue() == 10100.0;
        payment.getFee().doubleValue() == 1.0

        List<BalanceEntry> paymentTransactions = transactionRepository.getByPaymentId(payment.id)
        paymentTransactions.size() == 2

        List<BalanceEntry> sourceAccountTransactions = transactionRepository.getByAccountId(accountList.first().id)
        sourceAccountTransactions.size() == 1
        BalanceEntry sourceEntry = sourceAccountTransactions.first()
        sourceEntry.accountId == accountList.first().id
        sourceEntry.paymentId == payment.id
        sourceEntry.direction == PaymentDirection.PAY
        sourceEntry.amount == payment.amount

        List<BalanceEntry> destinationAccountTransactions = transactionRepository.getByAccountId(accountList.last().id)
        BalanceEntry first = destinationAccountTransactions.first()
        first.accountId == accountList.last().id
        first.paymentId == payment.id
        first.direction == PaymentDirection.RECEIVE
        first.amount == payment.amount
    }



    def "If the balance is lower, than the transaction amount the exception is thrown"(){

        setup:
        List<Account> accountList = generateAccounts(2, accountRepository)

        when:
        Payment payment = createPayment(accountList.first().id, accountList.last().id, 999999999.0)

        moneyTransferService.processPayment(payment)

        then:
        thrown(NotEnoughFundsException)
    }


    def "If the source account is not ACTIVE the exception is thrown"(){

        setup:
        List<Account> accountList = generateAccounts(2, accountRepository)
        Account source = accountList.first();
        source.setStatus(AccountStatus.BLOCKED)
        accountRepository.store(source);

        when:
        Payment payment = createPayment(accountList.first().id, accountList.last().id,100.0)

        moneyTransferService.processPayment(payment)


        then:
        thrown(AccountValidationException)
    }

    def "If the destination account is not ACTIVE the exception is thrown"(){

        setup:
        List<Account> accountList = generateAccounts(2, accountRepository)
        Account destination = accountList.last();
        destination.setStatus(AccountStatus.BLOCKED)
        accountRepository.store(destination);

        when:
        Payment payment = createPayment(accountList.first().id, accountList.last().id,100.0)

        moneyTransferService.processPayment(payment)


        then:
        thrown(AccountValidationException)
    }

    private Payment createPayment(Long sourceId, Long destinationId, double amount) {
        new Payment(paymentSequence.incrementAndGet(), sourceId, destinationId, new BigDecimal(amount))
    }

    def "A series of transfers"(){

        setup:
        Account first = new Account(4444, 1, BigDecimal.ZERO, AccountStatus.ACTIVE);
        first.increaseBalance(new BigDecimal(250.0))
        Account second = new Account(5555, 1, BigDecimal.ZERO, AccountStatus.ACTIVE);
        first.increaseBalance(new BigDecimal(250.0))
        accountRepository.store(first)
        accountRepository.store(second)

        when:
        def payments =
                [new Payment(1, 4444, 5555, new BigDecimal(100.0)),
                 new Payment(2, 4444, 5555, new BigDecimal(200.0)),
                 new Payment(3, 5555, 4444, new BigDecimal(50.0))
        ]
        payments.each {
            moneyTransferService.processPayment(it)
        }

        then:
        accountRepository.get(4444L).balance.doubleValue() == 248.0;
        accountRepository.get(5555L).balance.doubleValue() == 249.0;

    }

    def "Concurrent payments: multiple MT between two accounts"(){
        setup:
        List<Account> accountsList = generateAccounts(2, accountRepository)
        List<Callable<Payment>> tasks = generateMoneyTransferTasks(accountsList, 100)

        when:
        executeTasks(tasks)

        then:

        accountRepository.get(accountsList[0].id).balance.doubleValue() == 9900.0;
        accountRepository.get(accountsList[1].id).balance.doubleValue() == 9900.0;
    }



    def "Concurrent payments: dining philosophers strike back"(){
        setup:
        List<Account> accountsList = generateAccounts(10, accountRepository)
        List<Callable<Payment>> tasks = generateMoneyTransferTasks(accountsList, 100)

        when:
        executeTasks(tasks)

        then:
        accountRepository.get(accountsList[0].id).balance.doubleValue() == 9900.0;
        accountRepository.get(accountsList[1].id).balance.doubleValue() == 9900.0;
    }

    private void executeTasks(List<Callable<Payment>> tasks){
        List<ListenableFuture<Payment>> futures = decorator.invokeAll(tasks);
        ListenableFuture<List<Payment>> list = Futures.allAsList(futures)
        list.get();
    }

    private List<Account> generateAccounts(long quantity, AccountRepository repository) {
        List<Account> result = []
        List<Long> ids = []
        quantity.times {
            ids << accountSequence.incrementAndGet();
        }

        ids.each {
            long index = it;
            Account account = new Account(index, 1, BigDecimal.ZERO, AccountStatus.ACTIVE)
            account.increaseBalance(new BigDecimal(10000.0))
            result.add(account)
            repository.store(account)
        }
        return result
    }

    private List<Callable<Payment>> generateMoneyTransferTasks(List<Account> accountList, long quantity) {
        List<Callable<Payment>> tasks = []
        quantity.times {
            accountList.eachWithIndex { account, index ->
                long sourceAccount = account.getId();
                long destinationAccount = account == accountList.last() ? accountList.first().id : accountList[index + 1].id
                tasks.add(new Callable<Payment>() {
                    @Override
                    Payment call() throws Exception {
                        return moneyTransferService.processPayment(new Payment(1L, sourceAccount, destinationAccount, new BigDecimal(100.0)));
                    }
                })
            }
        }
        return tasks;
    }




}

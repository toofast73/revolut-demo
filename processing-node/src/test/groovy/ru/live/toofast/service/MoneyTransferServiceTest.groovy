package ru.live.toofast.service

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.Ignition
import ru.live.toofast.cache.CacheConfigurations
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.entity.payment.PaymentDirection
import ru.live.toofast.entity.payment.TransactionEntry
import ru.live.toofast.exception.AccountValidationException
import ru.live.toofast.exception.NotEnoughFundsException
import ru.live.toofast.repository.AccountRepository
import ru.live.toofast.repository.PaymentRepository
import ru.live.toofast.repository.TransactionRepository
import ru.live.toofast.service.FeeService
import ru.live.toofast.service.MoneyTransferService
import spock.lang.Shared
import spock.lang.Specification

import javax.cache.Cache
import java.util.concurrent.Callable

import static java.util.concurrent.Executors.newFixedThreadPool

class MoneyTransferServiceTest extends Specification {

    @Shared
    Ignite ignite = Ignition.start();

    @Shared
    Cache<Long, Account> accounts = ignite.getOrCreateCache(CacheConfigurations.accountCacheConfiguration());
    @Shared
    Cache<Long, Payment> payments = ignite.getOrCreateCache(CacheConfigurations.paymentCacheConfiguration());
    @Shared
    Cache<Long, TransactionEntry> transactions = ignite.getOrCreateCache(CacheConfigurations.transactionCacheConfiguration());
    AccountRepository accountRepository = new AccountRepository(accounts, sequence);

    IgniteAtomicSequence paymentSequence = ignite.atomicSequence(
            "paymentSequence",
            0,
            true
    );

    IgniteAtomicSequence transactionSequence = ignite.atomicSequence(
            "transactionSequence",
            0,
            true
    );

    PaymentRepository paymentRepository = new PaymentRepository(payments, paymentSequence);
    TransactionRepository transactionRepository = new TransactionRepository(transactions, transactionSequence)

    MoneyTransferService moneyTransferService = new MoneyTransferService(accountRepository, paymentRepository, transactionRepository, new FeeService());

    ListeningExecutorService decorator = MoreExecutors.listeningDecorator(newFixedThreadPool(30));

    def "Transfer USD150 from one account to another"(){

        setup:
        List<Account> accountList = generateAccounts(2, accounts)

        when:
        Payment payment = createPayment(100.0)

        moneyTransferService.processPayment(payment)


        then:
        accounts.get(0L).balance.doubleValue() == 9899.0;
        accounts.get(1L).balance.doubleValue() == 10100.0;
        payment.getFee().doubleValue() == 1.0

        List<TransactionEntry> paymentTransactions = transactionRepository.getByPaymentId(payment.id)
        paymentTransactions.size() == 2

        List<TransactionEntry> sourceAccountTransactions = transactionRepository.getByAccountId(accountList.first().id)
        sourceAccountTransactions.size() == 1
        TransactionEntry sourceEntry = sourceAccountTransactions.first()
        sourceEntry.id == 1;
        sourceEntry.accountId == accountList.first().id
        sourceEntry.paymentId == payment.id
        sourceEntry.type == PaymentDirection.PAY
        sourceEntry.amount == payment.amount

        List<TransactionEntry> destinationAccountTransactions = transactionRepository.getByAccountId(accountList.last().id)
        TransactionEntry first = destinationAccountTransactions.first()
        first.id == 2;
        first.accountId == accountList.last().id
        first.paymentId == payment.id
        first.type == PaymentDirection.RECEIVE
        first.amount == payment.amount
    }



    def "If the balance is lower, than the transaction amount the exception is thrown"(){

        setup:
        List<Account> accounts = generateAccounts(2, accounts)

        when:
        Payment payment = createPayment(999999999.0)

        moneyTransferService.processPayment(payment)

        then:
        thrown(NotEnoughFundsException)
    }


    def "If the source account is not ACTIVE the exception is thrown"(){

        setup:
        List<Account> accountList = generateAccounts(2, accounts)
        Account source = accountList.first();
        source.setStatus(AccountStatus.BLOCKED)
        accounts.put(source.id, source);

        when:
        Payment payment = createPayment(100.0)

        moneyTransferService.processPayment(payment)


        then:
        thrown(AccountValidationException)
    }

    def "If the destination account is not ACTIVE the exception is thrown"(){

        setup:
        List<Account> accountList = generateAccounts(2, accounts)
        Account destination = accountList.last();
        destination.setStatus(AccountStatus.BLOCKED)
        accounts.put(destination.id, destination);

        when:
        Payment payment = createPayment(100.0)

        moneyTransferService.processPayment(payment)


        then:
        thrown(AccountValidationException)
    }

    private Payment createPayment(double amount) {
        new Payment(1, 0L, 1L, new BigDecimal(amount))
    }

    def "A series of transfers"(){

        setup:
        Account first = new Account(4444, 1, AccountStatus.ACTIVE);
        first.increaseBalance(new BigDecimal(250.0))
        Account second = new Account(5555, 1, AccountStatus.ACTIVE);
        first.increaseBalance(new BigDecimal(250.0))
        accounts.put(first.getId(), first)
        accounts.put(second.getId(), second)

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
        accounts.get(4444L).balance.doubleValue() == 248.0;
        accounts.get(5555L).balance.doubleValue() == 249.0;

    }

    def "Concurrent payments: multiple MT between two accounts"(){
        setup:
        List<Account> accountsList = generateAccounts(2, accounts)
        List<Callable<Payment>> tasks = generateMoneyTransferTasks(accountsList, 100)

        when:
        executeTasks(tasks)

        then:

        accounts.get(0L).balance.doubleValue() == 9900.0;
        accounts.get(1L).balance.doubleValue() == 9900.0;
    }



    def "Concurrent payments: dining philosophers strike back"(){
        setup:
        List<Account> accountsList = generateAccounts(10, accounts)
        List<Callable<Payment>> tasks = generateMoneyTransferTasks(accountsList, 100)

        when:
        executeTasks(tasks)

        then:
        accounts.get(0L).balance.doubleValue() == 9900.0;
        accounts.get(1L).balance.doubleValue() == 9900.0;
    }

    private void executeTasks(List<Callable<Payment>> tasks){
        List<ListenableFuture<Payment>> futures = decorator.invokeAll(tasks);
        ListenableFuture<List<Payment>> list = Futures.allAsList(futures)
        list.get();
    }

    private List<Account> generateAccounts(long quantity, Cache<Long, Account> accounts) {
        List<Account> result = []
        quantity.times {
            long index = it;
            Account account = new Account(index, 1, AccountStatus.ACTIVE)
            account.increaseBalance(new BigDecimal(10000.0))
            result.add(account)
            accounts.put(index, account)
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

package ru.live.toofast.repository;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ignite.IgniteAtomicSequence;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.exception.EntityNotFoundException;

import javax.cache.Cache;

/**
 * DAO for Accounts.
 * Uses IgniteCache as a backing implementation.
 */
public class AccountRepository {

    private final Cache<Long, Account> accounts;
    private final IgniteAtomicSequence sequence;

    public AccountRepository(Cache<Long, Account> accounts, IgniteAtomicSequence sequence) {
        this.accounts = accounts;
        this.sequence = sequence;
    }


    /**
     * When executed in a transaction .get() locks the cache entry.
     * Locks are acquired in the same order as .get() or .put() invoked.
     * So, we need to keep the same account access order to avoid deadlocks in a concurrent environment.
     */
    public Pair<Account, Account> getAccounts(Payment payment) {
        long sourceAccountId = payment.getSourceAccountId();
        long destinationAccountId = payment.getDestinationAccountId();

        verifyExistence(sourceAccountId);
        verifyExistence(destinationAccountId);

        Account source;
        Account destination;


        if(sourceAccountId > destinationAccountId){
            source = accounts.get(sourceAccountId);
            destination = accounts.get(destinationAccountId);
        } else {
            destination = accounts.get(destinationAccountId);
            source = accounts.get(sourceAccountId);
        }

        return new ImmutablePair<>(source, destination);
    }

    private void verifyExistence(long accountId) {
        if (!contains(accountId)) {
            throw new EntityNotFoundException(String.format("Account with id %s is not found", accountId));
        }
    }

    public Account store(Account account) {
        if(account.getId() == null){
            account.setId(sequence.incrementAndGet());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    public Account get(long accountId) {
        return accounts.get(accountId);
    }

    public boolean contains(Long accountId) {
        return accounts.containsKey(accountId);
    }
}

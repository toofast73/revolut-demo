package ru.live.toofast.repository;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.processors.cache.CacheEntryImpl;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.entity.payment.TransactionEntry;
import ru.live.toofast.exception.EntityNotFoundException;

import javax.annotation.Nullable;
import javax.cache.Cache;
import java.util.Collection;
import java.util.List;

/**
 * DAO for Accounts.
 * Uses IgniteCache as a backing implementation.
 */
public class AccountRepository {

    private final IgniteCache<Long, Account> accounts;
    private final IgniteCache<String, Long> accountsByCard;
    private final IgniteCache<String, Long> accountsByPhone;

    private final IgniteAtomicSequence sequence;

    public AccountRepository(IgniteCache<Long, Account> accounts, IgniteCache<String, Long> accountsByCard, IgniteCache<String, Long> accountsByPhone, IgniteAtomicSequence sequence) {
        this.accounts = accounts;
        this.accountsByCard = accountsByCard;
        this.accountsByPhone = accountsByPhone;
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

    public Collection<Account> getByClient(long clientId) {
        SqlQuery sql = new SqlQuery(Account.class, "clientId = ?");

        return toList(accounts.query(sql.setArgs(clientId)).getAll());
    }

    public Account getByPhone(String phone) {
        if(accountsByPhone.containsKey(phone)){
            long accountId = accountsByPhone.get(phone);
            return accounts.get(accountId);
        }
        throw new EntityNotFoundException(String.format("Account linked to phone number %s not found", phone));
    }

    public Account getByCard(String card) {
        if(accountsByCard.containsKey(card)){
            long accountId = accountsByCard.get(card);
            return accounts.get(accountId);
        }
        throw new EntityNotFoundException(String.format("Account linked to card number %s not found", card));
    }


    public void linkCard(long accountId, String card){
        accountsByCard.put(card, accountId);
    }

    public void linkPhone(long accountId, String phone){
        accountsByPhone.put(phone, accountId);
    }


    private List<Account> toList(List<CacheEntryImpl> input) {
        return FluentIterable.from(input).transform(new Function<CacheEntryImpl, Account>() {
            @Nullable
            @Override
            public Account apply(@Nullable CacheEntryImpl input) {
                return (Account) input.getValue();
            }
        }).toList();
    }
}

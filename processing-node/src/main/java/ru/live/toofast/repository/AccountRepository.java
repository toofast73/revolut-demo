package ru.live.toofast.repository;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ignite.IgniteAtomicSequence;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;

import javax.cache.Cache;

public class AccountRepository {

    private final Cache<Long, Account> accounts;
    private final IgniteAtomicSequence sequence;

    public AccountRepository(Cache<Long, Account> accounts, IgniteAtomicSequence sequence) {
        this.accounts = accounts;
        this.sequence = sequence;
    }


    public Pair<Account, Account> getAccounts(Payment payment) {
        long sourceAccountId = payment.getSourceAccountId();
        long destinationAccountId = payment.getDestinationAccountId();

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

}

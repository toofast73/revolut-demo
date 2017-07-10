package ru.live.toofast.repository;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;

import javax.cache.Cache;

public class AccountRepository {

    private final Cache<Long, Account> accounts;

    public AccountRepository(Cache<Long, Account> accounts) {
        this.accounts = accounts;
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

    public void store(Account account) {
        accounts.put(account.getId(), account);
    }
}

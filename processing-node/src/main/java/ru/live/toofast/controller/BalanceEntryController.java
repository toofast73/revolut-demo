package ru.live.toofast.controller;

import ru.live.toofast.api.AccountBalanceApi;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.BalanceEntry;
import ru.live.toofast.repository.BalanceEntryRepository;

import java.util.Collection;

/**
 * The stub for operations history.
 *
 */
public class BalanceEntryController implements AccountBalanceApi{

    private final BalanceEntryRepository repository;

    public BalanceEntryController(BalanceEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Collection<BalanceEntry> getByAccount(long id) {
        return repository.getByAccountId(id);
    }

    @Override
    public Collection<BalanceEntry> getByPayment(long id) {
        return repository.getByPaymentId(id);
    }
}

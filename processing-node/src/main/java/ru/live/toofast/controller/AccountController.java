package ru.live.toofast.controller;

import ru.live.toofast.api.AccountApi;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.exception.AlreadyExistsException;
import ru.live.toofast.exception.EntityNotFoundException;
import ru.live.toofast.repository.AccountRepository;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;


/**
 * Controller for operations with Accounts.
 *
 * PUT/DELETE/GET_ALL operations are not implemented, because at the moment there is no need for them.
 */
public class AccountController implements AccountApi {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account get(long id) {
        Account account = accountRepository.get(id);
        if (account != null) {
            return account;
        } else {
            throw new EntityNotFoundException(String.format("Account with id %s is not found", id));
        }
    }

    /**
     * Create if absent.
     * Throw AlreadyExistsException, if already exists
     */
    @Override
    public Response create(Account account) {
        Long accountId = account.getId();
        if (accountId != null && accountRepository.contains(accountId)) {
            throw new AlreadyExistsException(String.format("Account with id %s already exists", accountId));
        }
        Account stored = accountRepository.store(account);

        return Response.status(CREATED).entity(stored).build();
    }

}

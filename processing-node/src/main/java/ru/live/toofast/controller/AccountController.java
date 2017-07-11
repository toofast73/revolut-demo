package ru.live.toofast.controller;

import org.eclipse.jetty.http.HttpStatus;
import ru.live.toofast.api.AccountApi;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.repository.AccountRepository;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;


public class AccountController implements AccountApi{

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account get(long id) {
        return accountRepository.get(id);
    }

    @Override
    public Account store(Account account,  HttpServletResponse response) {
        Account stored = accountRepository.store(account);
        response.setStatus(HttpServletResponse.SC_CREATED);

        return stored;
    }

}

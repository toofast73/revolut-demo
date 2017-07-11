package ru.live.toofast.controller

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import ru.live.toofast.entity.account.Account
import ru.live.toofast.exception.AlreadyExistsException
import ru.live.toofast.repository.AccountRepository

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.mockito.Mockito.when

class AccountControllerTest extends JerseyTest {

    private AccountRepository accountRepository

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();

        accountRepository = Mockito.mock(AccountRepository)
        config.register(new AccountController(accountRepository));
        return config;
    }



    @Test
    public void 'Return 404_NOT_FOUND if account doesnt exist'() {
        Response get = target("/account/222").request().get()
        Assert.assertEquals(get.status, Response.Status.NOT_FOUND.statusCode);
    }

    @Test
    public void "Return account if it exists"() {
        Account account = new Account();
        account.setId(123);
        when(accountRepository.get(123)).thenReturn(account)

        Response response = target("/account/123").request().get()
        Account responseAccount = response.readEntity(Account);
        Assert.assertEquals(Response.Status.OK.statusCode, response.status );
        Assert.assertEquals(123, responseAccount.id );
    }


    @Test
    public void "Store account and return it with the id"() {
        Account account = new Account();
        account.setClientId(22);
        when(accountRepository.store(ArgumentMatchers.any(Account))).thenThrow(new AlreadyExistsException())
        Entity<Account> entity = new Entity<>(account, MediaType.APPLICATION_JSON_TYPE);
        Response exception = target("/account").request().post(entity)

        println "sss"
    }
}

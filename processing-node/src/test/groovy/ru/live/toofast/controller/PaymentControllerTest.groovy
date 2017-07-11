package ru.live.toofast.controller

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.repository.PaymentRepository
import ru.live.toofast.service.PaymentService

import javax.ws.rs.core.Application
import javax.ws.rs.core.Response

import static org.mockito.Mockito.when

public class PaymentControllerTest extends JerseyTest{

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        paymentRepository = Mockito.mock(PaymentRepository);
        paymentService = new PaymentService(null, paymentRepository, null, null);
        config.register(new PaymentController(paymentService));
        return config;
    }



    @Test
    public void 'Return 404_NOT_FOUND if account doesnt exist'() {
        Response get = target("/payment/222").request().get()
        Assert.assertEquals(Response.Status.NOT_FOUND.statusCode, get.status);
    }

    @Test
    public void "Return payment if it exists"() {
        Payment payment = new Payment(0, 0, 0, BigDecimal.ZERO);

        when(paymentRepository.contains(0)).thenReturn(true)
        when(paymentRepository.get(0)).thenReturn(payment)

        Response response = target("/payment/0").request().get()
        Payment responsePayment = response.readEntity(Payment);
        Assert.assertEquals(Response.Status.OK.statusCode, response.status);
        Assert.assertEquals(0, responsePayment.id );
    }

/*
    @Test
    public void "Store account: exception is thrown if it already exists"() {
        Account account = new Account();
        account.setId(1L);
        when(accountRepository.contains(1L)).thenThrow(new AlreadyExistsException("Account already exists"))
        Entity<Account> entity = new Entity<>(account, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/account").request().post(entity)
        ApplicationException applicationException = response.readEntity(ApplicationException)
        Assert.assertEquals(409, response.status )
        Assert.assertEquals("Account already exists", applicationException.message )
        Assert.assertEquals(AlreadyExistsException.name, applicationException.type)
    }


    @Test
    public void "Store account: account can be stored"() {
        Account account = new Account();
        account.setId(1L);
        when(accountRepository.store(ArgumentMatchers.eq(account))).thenReturn(account)
        Entity<Account> entity = new Entity<>(account, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/account").request().post(entity)
        Account responseAccount = response.readEntity(Account)
        Assert.assertEquals(201, response.status )
        Assert.assertEquals(1L, responseAccount.id)
    }*/
}

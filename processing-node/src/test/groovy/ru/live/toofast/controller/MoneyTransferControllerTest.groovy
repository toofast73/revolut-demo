package ru.live.toofast.controller

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import ru.live.toofast.entity.ApplicationException
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.exception.AccountValidationException
import ru.live.toofast.service.PaymentService

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when

public class MoneyTransferControllerTest extends JerseyTest{

    private PaymentService paymentService;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        paymentService = Mockito.mock(PaymentService);
        config.register(new PaymentController(paymentService));
        return config;
    }

    @Test
    public void "Payments: if account is not valid (blocked, deleted) the BAD_REQUEST_400 is returned"() {
        Payment payment = new Payment(id: 1, sourceAccountId: 2, destinationAccountId: 3, amount: BigDecimal.ZERO);
        when(paymentService.processPayment(eq(payment))).thenThrow(new AccountValidationException("Not Valid"))

        Entity<Account> entity = new Entity<>(payment, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/payment").request().post(entity)
        ApplicationException applicationException = response.readEntity(ApplicationException)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status )
        Assert.assertEquals("Not Valid", applicationException.message )
        Assert.assertEquals(AccountValidationException.name, applicationException.type)
    }

    @Test
    public void "Payments: if payment can't pass validation check BAD_REQUEST_400 returned"() {
        Payment payment = new Payment();

        Entity<Account> entity = new Entity<>(payment, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/payment").request().post(entity)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status )
    }
}

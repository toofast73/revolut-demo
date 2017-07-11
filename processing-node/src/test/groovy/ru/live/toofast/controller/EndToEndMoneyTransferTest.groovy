package ru.live.toofast.controller

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Assert
import org.junit.Test
import ru.live.toofast.PaymentProcessingApplication
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.entity.payment.PaymentStatus

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class EndToEndMoneyTransferTest extends JerseyTest {

    @Override
    protected Application configure() {
        Ignition.getOrStart(new IgniteConfiguration())
        return PaymentProcessingApplication.getResourceConfig();
    }

    @Test
    public void "Payments: if account is not valid (blocked, deleted) the BAD_REQUEST_400 is returned"() {
        //given: two account
        Account source = new Account(null, 1, BigDecimal.TEN, AccountStatus.ACTIVE)
        Account destination = new Account(null, 2, BigDecimal.TEN, AccountStatus.ACTIVE)

        //when:
        //POST /account: store source account
        Entity<Account> sourceEntity = new Entity<>(source, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/account").request().post(sourceEntity)
        Account sourceResponseAccount = response.readEntity(Account)

        //POST /account: store destination account
        Entity<Account> destinationEntity = new Entity<>(destination, MediaType.APPLICATION_JSON_TYPE);
        Response destinationResponse = target("/account").request().post(destinationEntity)
        Account destinationResponseAccount = destinationResponse.readEntity(Account)

        Long sourceResponseAccountId = sourceResponseAccount.id;
        Long destinationResponseAccountId = destinationResponseAccount.id;

        //Pay $5 from source to destination account
        Payment payment = new Payment(null, sourceResponseAccountId, destinationResponseAccountId, new BigDecimal(5.0))
        //POST /payment:
        Entity<Payment> paymentEntity = new Entity<>(payment, MediaType.APPLICATION_JSON_TYPE);
        Response paymentResponse = target("/payment").request().post(paymentEntity)
        Payment paymentResponseResult = paymentResponse.readEntity(Payment)

        //GET /account/0: Fetch source account state, after the transaction
        Account sourceAccountAfterOperation = target("/account/" + sourceResponseAccountId).request().get(Account)
        //GET /account/0: Fetch destination account state, after the transaction
        Account destinationAccountAfterOperation = target("/account/" + destinationResponseAccountId).request().get(Account)

        //then:
        //First account was charged by $5 (payment) +$1 (fee)
        Assert.assertEquals(4.0, sourceAccountAfterOperation.balance.doubleValue(), 0.01 )
        //Second account received +$5 (payment)
        Assert.assertEquals(15.0, destinationAccountAfterOperation.balance.doubleValue(), 0.01)
        //The fee is registered in the payment details 1$
        Assert.assertEquals(1.0, paymentResponseResult.fee.doubleValue(), 0.1 )
        //The payment is marked as COMPLETED
        Assert.assertEquals(PaymentStatus.COMPLETED, paymentResponseResult.status)
    }


}

package ru.live.toofast.end.to.end

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Assert
import org.junit.Test
import ru.live.toofast.JerseySpec
import ru.live.toofast.PaymentProcessingApplication
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.entity.payment.PaymentStatus

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class MoneyTransferSpockTest extends JerseySpec {

    @Override
    ResourceConfig config() {
        Ignition.start(new IgniteConfiguration())
        return PaymentProcessingApplication.getResourceConfig();
    }

    def "End-to-end money transfer test with the real cache and services"() {
        given: "two accounts"
        Account source = new Account(clientId: 1, balance: BigDecimal.TEN, status: AccountStatus.ACTIVE)
        Account destination = new Account(clientId: 2, balance: BigDecimal.TEN, status: AccountStatus.ACTIVE)

        when: "POST /account: create source account"
        Entity<Account> sourceEntity = new Entity<>(source, MediaType.APPLICATION_JSON_TYPE);
        Response response = jersey.target("/account").request().post(sourceEntity)
        Account sourceResponseAccount = response.readEntity(Account)
        then: "account has been created and the ID is assigned"
        sourceResponseAccount.id

        when: "POST /account: create destination account"
        Entity<Account> destinationEntity = new Entity<>(destination, MediaType.APPLICATION_JSON_TYPE);
        Response destinationResponse = jersey.target("/account").request().post(destinationEntity)
        Account destinationResponseAccount = destinationResponse.readEntity(Account)
        then:
        destinationResponseAccount.id

        when: "POST /payment: Transfer 5 USD from source to destination account"
        Long sourceResponseAccountId = sourceResponseAccount.id;
        Long destinationResponseAccountId = destinationResponseAccount.id;
        Payment payment = new Payment(sourceAccountId: sourceResponseAccountId, destinationAccountId: destinationResponseAccountId, amount: new BigDecimal(5.0))

        Entity<Payment> paymentEntity = new Entity<>(payment, MediaType.APPLICATION_JSON_TYPE);
        Response paymentResponse = jersey.target("/payment").request().post(paymentEntity)
        Payment paymentResponseResult = paymentResponse.readEntity(Payment)
        then:
        paymentResponseResult.status == PaymentStatus.COMPLETED
        and: "The fee is registered in the payment details 1 USD"
        paymentResponseResult.fee.doubleValue() == 1.0

        when: "GET /account/0: Fetch source account state, after the transaction"
        Account sourceAccountAfterOperation = jersey.target("/account/" + sourceResponseAccountId).request().get(Account)
        then: "First account was charged by \$5 (payment) +\$1 (fee)"
        sourceAccountAfterOperation.balance.doubleValue() == 4.0

        when: "GET /account/1: Fetch destination account state, after the transaction"
        Account destinationAccountAfterOperation = jersey.target("/account/" + destinationResponseAccountId).request().get(Account)
        then: "//Second account received +5 USD (payment)"
        destinationAccountAfterOperation.balance.doubleValue() == 15.0
    }
}
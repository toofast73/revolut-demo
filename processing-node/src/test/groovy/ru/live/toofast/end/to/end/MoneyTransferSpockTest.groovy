package ru.live.toofast.end.to.end

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.glassfish.jersey.server.ResourceConfig
import ru.live.toofast.JerseySpec
import ru.live.toofast.PaymentProcessingApplication
import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.entity.payment.PaymentStatus

import javax.ws.rs.client.Entity
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
        Account sourceResponseAccount = createAccount(source)
        then: "account has been created and the ID is assigned"
        sourceResponseAccount.id

        when: "POST /account: create destination account"
        Account destinationResponseAccount = createAccount(source)
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

    private Account createAccount(Account source) {
        Entity<Account> sourceEntity = new Entity<>(source, MediaType.APPLICATION_JSON_TYPE);
        Response response = jersey.target("/account").request().post(sourceEntity)
        Account sourceResponseAccount = response.readEntity(Account)
        sourceResponseAccount
    }


    def """As a client I don't know my exact account number, but I know my card number
           and want to transfer money to my friend's account.
           I know his phone number"""() {
        given: "two accounts"
        String sourceCardNumber = "4277400040002000"
        String destinationPhoneNumber = "+79034448011"

        Account source = new Account(clientId: 1, balance: BigDecimal.TEN, status: AccountStatus.ACTIVE)
        Account destination = new Account(clientId: 2, balance: BigDecimal.TEN, status: AccountStatus.ACTIVE)

        Account sourceResponseAccount = createAccount(source)
        Account destinationResponseAccount = createAccount(destination)

        when: "Link a credit card number to the source account"
        Entity<String> creditCardEntity = Entity.json(sourceCardNumber)
        Response creditCardRegistrationResponse = jersey.target(String.format("/account/%s/linkTo/card/%s", sourceResponseAccount.id, sourceCardNumber)).request().post(creditCardEntity);
        then:
        creditCardRegistrationResponse.status == Response.Status.OK.statusCode

        when: "Link a phone number to the destination account"
        Entity<String> phoneEntity = Entity.json(destinationPhoneNumber);
        Response phoneRegistrationResponse = jersey.target(String.format("/account/%s/linkTo/phone/%s", destinationResponseAccount.id, destinationPhoneNumber)).request().post(phoneEntity)
        then:
        phoneRegistrationResponse.status == Response.Status.OK.statusCode

        when: "Lookup for source account by card number"
        Account lookupSourceAccount = jersey.target(String.format("/account/by/card/%s", sourceCardNumber)).request().get(Account)
        then:
        lookupSourceAccount.id == sourceResponseAccount.id

        when: "Lookup for destination account by phone number"
        Account lookupDestinationAccount = jersey.target(String.format("/account/by/phone/%s", destinationPhoneNumber)).request().get(Account)
        then:
        lookupDestinationAccount.id == destinationResponseAccount.id

        when: "POST /payment: Transfer 5 USD from source to destination account"
        Long sourceResponseAccountId = lookupSourceAccount.id;
        Long destinationResponseAccountId = lookupDestinationAccount.id;
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
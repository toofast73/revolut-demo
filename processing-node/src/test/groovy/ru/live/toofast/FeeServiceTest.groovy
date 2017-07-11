package ru.live.toofast

import ru.live.toofast.entity.account.Account
import ru.live.toofast.entity.account.AccountStatus
import ru.live.toofast.entity.payment.Payment
import ru.live.toofast.service.FeeService
import spock.lang.Specification


class FeeServiceTest extends Specification {

    def "Fee is constant for all amounts and accounts"(){

        given:
        FeeService feeService = new FeeService()
        Account source = new Account(1, 2, AccountStatus.ACTIVE);
        source.increaseBalance(BigDecimal.TEN);
        Account destination = new Account(2, 3, AccountStatus.ACTIVE);
        Payment payment = new Payment(1, 1, 2, BigDecimal.TEN)

        when:
        feeService.collectFee(payment, source, destination)

        then:
        payment.fee == 1
        source.balance.doubleValue() == 9.0
    }


}
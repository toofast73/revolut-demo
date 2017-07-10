package ru.live.toofast.service;

import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.exception.FeeException;

import java.math.BigDecimal;

public class FeeService {

    public void collectFee(Payment payment, Account source, Account destination) {
        BigDecimal fee = calculateFee(payment);
        if(source.getBalance().compareTo(fee) < 0){
            throw new FeeException();
        }
        source.decreaseBalance(fee);
        payment.setFee(fee);
    }

    private BigDecimal calculateFee(Payment payment) {
        return BigDecimal.ONE;
    }

}

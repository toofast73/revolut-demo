package ru.live.toofast.service;

import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.exception.FeeException;

import java.math.BigDecimal;

/**
 * The most simple fee calculation strategy.
 * It charges $1 per each payment.
 *
 * In this simple example a fee is just charged. It is not transferred to a specific charge-off account.
 */
public class FeeService {

    public void collectFee(Payment payment, Account source, Account destination) {
        BigDecimal fee = calculateFee(payment);
        if(source.getBalance().compareTo(fee) < 0){
            throw new FeeException(String.format("Account %s has not enough funds to pay the fee", source.getId() ));
        }
        source.decreaseBalance(fee);
        payment.setFee(fee);
    }

    private BigDecimal calculateFee(Payment payment) {
        return BigDecimal.ONE;
    }

}

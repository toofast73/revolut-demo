package ru.live.toofast.entity.payment;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.math.BigDecimal;

/**
 * Atomic representation of the account-related transaction.
 *
 * For example, if client add an amount of cash to the account via ATM it is one BalanceEntry with direction RECEIVE.
 *
 * If we transfer money between two accounts we have two legs:
 * Source-account      has BalanceEntry with direction PAY
 * Destination-account has BalanceEntry with direction RECEIVE
 *
 * May be used to compose Account balance history.
 * AccountBalance == Sum(RECEIVE) - Sum(PAY)
 */
public class BalanceEntry {

    long id;

    @QuerySqlField(index = true)
    long paymentId;

    @QuerySqlField(index = true)
    long accountId;

    BigDecimal amount;

    PaymentDirection direction;

    public BalanceEntry(long id, long paymentId, long accountId, BigDecimal amount, PaymentDirection direction) {
        this.id = id;
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.amount = amount;
        this.direction = direction;
    }

    public long getId() {
        return id;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentDirection getDirection() {
        return direction;
    }
}

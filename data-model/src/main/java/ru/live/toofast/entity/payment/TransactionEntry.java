package ru.live.toofast.entity.payment;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.math.BigDecimal;

public class TransactionEntry {

    long id;

    @QuerySqlField(index = true)
    long paymentId;

    @QuerySqlField(index = true)
    long accountId;

    BigDecimal amount;

    PaymentDirection type;

    public TransactionEntry(long id, long paymentId, long accountId, BigDecimal amount, PaymentDirection type) {
        this.id = id;
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
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

    public PaymentDirection getType() {
        return type;
    }
}

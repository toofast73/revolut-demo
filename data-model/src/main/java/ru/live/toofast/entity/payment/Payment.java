package ru.live.toofast.entity.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private final long id;

    private final long sourceAccountId;

    private final long destinationAccountId;

    private final BigDecimal amount;

    private BigDecimal fee;

    private PaymentStatus status;

    private final LocalDateTime receivedDate;

    private LocalDateTime completedDate;

    public Payment(long id, long sourceAccountId, long destinationAccountId, BigDecimal amount) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.receivedDate = LocalDateTime.now();
    }


    public long getId() {
        return id;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void completed(){
        completedDate = LocalDateTime.now();
        status = PaymentStatus.COMPLETED;
    }
}

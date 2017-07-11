package ru.live.toofast.entity.payment;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class Payment {

    private Long id;

    @NotNull
    private Long sourceAccountId;
    @NotNull
    private Long destinationAccountId;
    @NotNull
    private BigDecimal amount;

    private BigDecimal fee;

    private PaymentStatus status;

    private Date receivedDate;

    private Date completedDate;

    public Payment(Long id, long sourceAccountId, long destinationAccountId, BigDecimal amount) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.receivedDate = new Date();
    }

    public Payment() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Long getId() {
        return id;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void completed(){
        completedDate = new Date();
        status = PaymentStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id == payment.id &&
                sourceAccountId == payment.sourceAccountId &&
                destinationAccountId == payment.destinationAccountId &&
                Objects.equals(amount, payment.amount) &&
                Objects.equals(fee, payment.fee) &&
                status == payment.status &&
                Objects.equals(receivedDate, payment.receivedDate) &&
                Objects.equals(completedDate, payment.completedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceAccountId, destinationAccountId, amount, fee, status, receivedDate, completedDate);
    }
}

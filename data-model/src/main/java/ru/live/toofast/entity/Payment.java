package ru.live.toofast.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private final long id;

    private final long sourceAccountId;

    private final long destinationAccountId;

    private final BigDecimal amount;

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

}

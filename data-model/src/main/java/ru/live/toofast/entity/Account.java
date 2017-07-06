package ru.live.toofast.entity;

import java.math.BigDecimal;

public class Account {

    private final long id;
    private final long clientId;
    private BigDecimal balance;
    private AccountStatus status;

    public Account(Long id, Long clientId, AccountStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.balance = BigDecimal.ZERO;
        this.status = status;
    }
}

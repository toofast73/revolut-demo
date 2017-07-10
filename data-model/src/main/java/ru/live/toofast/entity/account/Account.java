package ru.live.toofast.entity.account;

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

    public long getId() {
        return id;
    }

    public long getClientId() {
        return clientId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void decreaseBalance(BigDecimal amount){
        balance = balance.add(amount.negate());
    }

    public void increaseBalance(BigDecimal amount){
        balance = balance.add(amount);
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }


}

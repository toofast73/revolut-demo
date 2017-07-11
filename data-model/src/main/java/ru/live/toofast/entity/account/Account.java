package ru.live.toofast.entity.account;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private long clientId;
    private BigDecimal balance;
    private AccountStatus status;

    public Long getId() {
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

    public void setId(long id) {
        this.id = id;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

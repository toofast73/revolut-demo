package ru.live.toofast.entity.account;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * User account
 */
public class Account {
    private Long id;

    @QuerySqlField(index = true)
    private long clientId;

    //Account balance. The amount of money, available for transfers.
    private BigDecimal balance;
    private AccountStatus status;

    public Account(Long id, long clientId, BigDecimal balance, AccountStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.balance = balance;
        this.status = status;
    }

    public Account() {
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return clientId == account.clientId &&
                Objects.equals(id, account.id) &&
                Objects.equals(balance, account.balance) &&
                status == account.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, balance, status);
    }
}

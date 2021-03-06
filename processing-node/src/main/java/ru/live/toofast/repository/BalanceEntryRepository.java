package ru.live.toofast.repository;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.processors.cache.CacheEntryImpl;
import org.apache.ignite.cache.query.SqlQuery;
import ru.live.toofast.entity.payment.BalanceEntry;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.entity.payment.PaymentDirection;

import javax.annotation.Nullable;
import java.util.List;

/**
 * DAO for Payments.
 * Uses IgniteCache as a backing implementation.
 */
public class BalanceEntryRepository {

    private final IgniteCache<Long, BalanceEntry> transactions;
    private final IgniteAtomicSequence sequence;

    public BalanceEntryRepository(IgniteCache<Long, BalanceEntry> transactions, IgniteAtomicSequence sequence) {
        this.transactions = transactions;
        this.sequence = sequence;
    }

    public void registerPayment(Payment payment) {
        BalanceEntry pay = new BalanceEntry(sequence.incrementAndGet(), payment.getId(), payment.getSourceAccountId(), payment.getAmount(), PaymentDirection.PAY);
        BalanceEntry receive = new BalanceEntry(sequence.incrementAndGet(), payment.getId(), payment.getDestinationAccountId(), payment.getAmount(), PaymentDirection.RECEIVE);
        transactions.put(pay.getId(), pay);
        transactions.put(receive.getId(), receive);
    }

    /**
     * Ignite allows you to make SQL-like queries over the distributed in-memory cache.
     * Here we het all transaction-entries related to an account.
     */
    public List<BalanceEntry> getByAccountId(long accountId) {
        SqlQuery sql = new SqlQuery(BalanceEntry.class, "accountId = ?");

        return toList(transactions.query(sql.setArgs(accountId)).getAll());
    }


    /**
     * Get all transaction-entries produced by specific payment.
     */
    public List<BalanceEntry> getByPaymentId(long paymentId) {
        SqlQuery sql = new SqlQuery(BalanceEntry.class, "paymentId = ?");

        return toList(transactions.query(sql.setArgs(paymentId)).getAll());
    }

    private List<BalanceEntry> toList(List<CacheEntryImpl> input) {
        return FluentIterable.from(input).transform(new Function<CacheEntryImpl, BalanceEntry>() {
            @Nullable
            @Override
            public BalanceEntry apply(@Nullable CacheEntryImpl input) {
                return (BalanceEntry) input.getValue();
            }
        }).toList();
    }
}

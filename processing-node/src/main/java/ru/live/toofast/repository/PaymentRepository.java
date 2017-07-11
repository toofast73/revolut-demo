package ru.live.toofast.repository;

import org.apache.ignite.IgniteAtomicSequence;
import ru.live.toofast.entity.payment.Payment;

import javax.cache.Cache;
/**
 * DAO for Payments.
 * Uses IgniteCache as a backing implementation.
 */
public class PaymentRepository {

    private final Cache<Long, Payment> payments;
    private final IgniteAtomicSequence sequence;

    public PaymentRepository(Cache<Long, Payment> payments, IgniteAtomicSequence sequence) {
        this.payments = payments;
        this.sequence = sequence;
    }

    public void store(Payment payment) {
        if(payment.getId() == null){
            payment.setId(sequence.incrementAndGet());
        }
        payments.put(payment.getId(), payment);
    }


    public boolean contains(long paymentId){
        return payments.containsKey(paymentId);
    }

    public Payment get(long paymentId) {
        return payments.get(paymentId);
    }
}

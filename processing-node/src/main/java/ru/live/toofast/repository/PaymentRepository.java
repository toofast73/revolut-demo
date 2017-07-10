package ru.live.toofast.repository;

import org.apache.ignite.IgniteAtomicSequence;
import ru.live.toofast.entity.payment.Payment;

import javax.cache.Cache;

public class PaymentRepository {

    private final Cache<Long, Payment> payments;
    private final IgniteAtomicSequence sequence;

    public PaymentRepository(Cache<Long, Payment> payments, IgniteAtomicSequence sequence) {
        this.payments = payments;
        this.sequence = sequence;
    }


    public void store(Payment payment) {
        payments.put(payment.getId(), payment);
    }
}

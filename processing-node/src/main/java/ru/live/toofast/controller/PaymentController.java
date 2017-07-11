package ru.live.toofast.controller;

import ru.live.toofast.api.PaymentApi;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.service.PaymentService;

import javax.validation.Valid;

/**
 * PUT/DELETE/GET_ALL operations are not implemented, because at the moment there is no need for them.
 */
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Payment get(long paymentId) {
         return paymentService.getPaymentStatus(paymentId);
    }

    @Override
    public Payment transferMoney(@Valid Payment payment) {
        return paymentService.processPayment(payment);
    }

}

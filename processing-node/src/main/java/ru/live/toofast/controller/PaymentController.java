package ru.live.toofast.controller;

import ru.live.toofast.api.PaymentApi;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.service.PaymentService;

import javax.validation.Valid;


public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Payment getPaymentStatus(long paymentId) {
         return paymentService.getPaymentStatus(paymentId);
    }

    @Override
    public Payment transferMoney(@Valid Payment payment) {
        return paymentService.processPayment(payment);
    }

}

package ru.live.toofast.controller;

import ru.live.toofast.api.PaymentApi;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.repository.PaymentRepository;
import ru.live.toofast.service.MoneyTransferService;

import javax.ws.rs.POST;

public class PaymentController implements PaymentApi {

    MoneyTransferService moneyTransferService;
    PaymentRepository paymentRepository;


    public Payment getPaymentStatus(){
        long paymentId = 0L;
        Payment payment = moneyTransferService.getPayment(paymentId);

        return payment;
    }


    @POST
    public Payment transferMoney(Payment request){
        return moneyTransferService.processPayment(request);
    }



}

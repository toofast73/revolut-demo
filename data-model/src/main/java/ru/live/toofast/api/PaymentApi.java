package ru.live.toofast.api;

import ru.live.toofast.entity.payment.Payment;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/payment")
public interface PaymentApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{paymentId}")
    Payment getPaymentStatus(@PathParam("paymentId") long paymentId);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Payment transferMoney(@Valid Payment request);

}

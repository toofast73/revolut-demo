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

/**
 * Controller for money transfers.
 *
 *
 *
 * PUT/DELETE/GET_ALL operations are not implemented, because at the moment there is no need for them.
 */
@Path("/payment")
public interface PaymentApi {

    /**
     * Get payment by id. Used to check current status of the payment.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{paymentId}")
    Payment get(@PathParam("paymentId") long paymentId);


    /**
     * Money transfer request. Between two accounts.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Payment transferMoney(@Valid Payment request);

}

package ru.live.toofast.api;

import ru.live.toofast.entity.payment.BalanceEntry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/balance")
public interface AccountBalanceApi {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by/account/{accountId}")
    Collection<BalanceEntry> getByAccount(@PathParam("accountId") long id);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by/payment/{paymentId}")
    Collection<BalanceEntry> getByPayment(@PathParam("paymentId") long id);


}

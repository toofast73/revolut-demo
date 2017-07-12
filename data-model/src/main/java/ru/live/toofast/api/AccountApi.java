package ru.live.toofast.api;

import ru.live.toofast.entity.account.Account;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Controller for operations with Accounts.
 *
 * PUT/DELETE/GET_ALL operations are not implemented, because at the moment there is no need for them.
 */
@Path("/account")
public interface AccountApi  {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}")
    Account get(@PathParam("accountId") long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by/client/{clientId}")
    Collection<Account> getByClient(@PathParam("clientId") long id);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by/phone/{phone}")
    Account getByPhone(@PathParam("phone") String phone);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by/card/{card}")
    Account getByCard(@PathParam("card") String id);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}/linkTo/card/{card}")
    Response linkCard(@PathParam("accountId") long accountId, @PathParam("card") String card);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}/linkTo/phone/{phone}")
    Response linkPhone(@PathParam("accountId") long accountId, @PathParam("phone") String phone);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response create(@Valid Account account);

}

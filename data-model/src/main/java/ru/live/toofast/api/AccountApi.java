package ru.live.toofast.api;

import ru.live.toofast.entity.account.Account;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Yuri on 11.07.2017.
 */
@Path("/account")
public interface AccountApi  {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}")
    Account get(@PathParam("accountId") long id);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response store(@Valid Account account);

}

package ru.live.toofast.exception;

import ru.live.toofast.entity.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *  Account doesn't have enough funds to pay the specified amount.
 */
public class NotEnoughFundsException extends WebApplicationException {
    private static final Response.Status STATUS = Response.Status.BAD_REQUEST;

    public NotEnoughFundsException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        new ApplicationException(message, NotEnoughFundsException.class.getName(), STATUS.name(), STATUS.getStatusCode()))
                .type(MediaType.APPLICATION_JSON).build());
    }

}

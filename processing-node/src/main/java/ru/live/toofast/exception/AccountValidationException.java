package ru.live.toofast.exception;

import ru.live.toofast.entity.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Account failed to pass validation constraints and can't participate in money transfers.
 */
public class AccountValidationException extends WebApplicationException {
    private static final Response.Status STATUS = Response.Status.BAD_REQUEST;

    public AccountValidationException(String message) {
        super(Response.status(STATUS)
                .entity(
                        new ApplicationException(message, AccountValidationException.class.getName(), STATUS.name(), STATUS.getStatusCode()))
                .type(MediaType.APPLICATION_JSON).build());
    }
}

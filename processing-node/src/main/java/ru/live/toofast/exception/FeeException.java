package ru.live.toofast.exception;

import ru.live.toofast.entity.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class FeeException extends WebApplicationException {
    private static final Response.Status STATUS = Response.Status.BAD_REQUEST;

    public FeeException(String message) {
        super(Response.status(STATUS)
                .entity(
                        new ApplicationException(message, FeeException.class.getName(), STATUS.name(), STATUS.getStatusCode()))
                .type(MediaType.APPLICATION_JSON).build());
    }

}

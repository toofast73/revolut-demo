package ru.live.toofast.exception;

import ru.live.toofast.entity.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AlreadyExistsException extends WebApplicationException {
    private static final Response.Status STATUS = Response.Status.CONFLICT;

    public AlreadyExistsException(String message) {
        super(Response.status(Response.Status.CONFLICT)
                .entity(
                        new ApplicationException(message, AlreadyExistsException.class.getName(), STATUS.name(), STATUS.getStatusCode()))
                .type(MediaType.APPLICATION_JSON).build());
    }
}

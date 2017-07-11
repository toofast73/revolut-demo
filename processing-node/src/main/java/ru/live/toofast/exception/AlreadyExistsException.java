package ru.live.toofast.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AlreadyExistsException extends WebApplicationException {

    public AlreadyExistsException(String message) {
        super(Response.status(Response.Status.CONFLICT)
                .entity(new ApplicationExceptionHolder(message, AlreadyExistsException.class.getName())).type(MediaType.APPLICATION_JSON).build());
    }
}

package ru.live.toofast.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class FeeException extends WebApplicationException {

    public FeeException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApplicationExceptionHolder(message, FeeException.class.getName())).type(MediaType.APPLICATION_JSON).build());
    }

}

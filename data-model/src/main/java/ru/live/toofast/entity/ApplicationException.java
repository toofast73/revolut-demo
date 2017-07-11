package ru.live.toofast.entity;

/**
 * The wrapper for WebApplicationExceptions.
 * Used to return exception-response-body, when things go nasty.
 */
public class ApplicationException {

    /**
     * User-friendly error message
     */
    private String message;
    /**
     * Exception class
     */
    private String type;
    /**
     * Verbal representation of the HTTP code
     */
    private String status;
    /**
     * HTTP code
     */
    private long statusCode;

    public ApplicationException(String message, String type, String status, long statusCode) {
        this.message = message;
        this.type = type;
        this.status = status;
        this.statusCode = statusCode;
    }

    public ApplicationException() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }
}

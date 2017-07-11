package ru.live.toofast.entity;

public class ApplicationException {

    private String message;
    private String type;
    private String status;
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

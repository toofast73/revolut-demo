package ru.live.toofast.entity;

public class ApplicationException {

    private String message;
    private String type;

    public ApplicationException(String message, String type) {
        this.message = message;
        this.type = type;
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
}

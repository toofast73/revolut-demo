package ru.live.toofast.exception;

public class AccountValidationException extends RuntimeException{

    private final String msg;

    public AccountValidationException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

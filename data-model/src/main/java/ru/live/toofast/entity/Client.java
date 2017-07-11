package ru.live.toofast.entity;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.LocalDateTime.now;

public class Client {

    private final long id;
    private String name;
    private final Date creationDate;

    public Client(long id, String name) {
        this.id = id;
        this.name = name;
        this.creationDate = new Date();
    }
}

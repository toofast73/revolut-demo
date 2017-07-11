package ru.live.toofast.entity;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.LocalDateTime.now;

/**
 * The entity stub for Client.
 * Is not used in business logic at the moment.
 */
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

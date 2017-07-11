package ru.live.toofast

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.core.Application

abstract class JerseySpec extends Specification {

    abstract ResourceConfig config()

    @Shared JerseyTest jersey

    void setupSpec(){
        jersey = new JerseyTest(){
            @Override
            protected Application configure() {
                return config()
            }
        }
        jersey.setUp()
    }

    void cleanupSpec() {
        jersey.tearDown()
    }
}

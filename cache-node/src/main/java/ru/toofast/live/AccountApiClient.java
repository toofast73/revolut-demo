package ru.toofast.live;

import feign.Feign;
import feign.Response;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import ru.live.toofast.api.AccountApi;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Yuri on 11.07.2017.
 */
public class AccountApiClient {

    public static AccountApi accountApi(String path){
        return Feign.builder()
                .contract(new JAXRSContract())
                .decoder(new ResponseDecoder(new JacksonDecoder()))
                .encoder(new JacksonEncoder())
                .target(AccountApi.class, path);
    }

    final static class ResponseDecoder implements Decoder {
        private final Decoder delegate;

        ResponseDecoder(Decoder delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object decode(Response response, Type type) throws IOException {
            if (javax.ws.rs.core.Response.class.equals(type)) {
                System.out.println();

                // construct and return.
            }
            return delegate.decode(response, type);
        }
    }

}

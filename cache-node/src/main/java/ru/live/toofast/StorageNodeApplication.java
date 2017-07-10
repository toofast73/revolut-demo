package ru.live.toofast;


import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

public class StorageNodeApplication {

    public static void main(String[] args) throws IgniteException {
        Ignition.start(storageNodeConfig());
    }

    private static IgniteConfiguration storageNodeConfig(){
        IgniteConfiguration config = new IgniteConfiguration();
        config.setClientMode(false);
        return config;
    }

}

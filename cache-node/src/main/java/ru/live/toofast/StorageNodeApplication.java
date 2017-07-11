package ru.live.toofast;


import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * This is an instance of the distributed cache cluster.
 * Its function is to create data (in memory) and provide it via javax.cache.Cache interface and SQL-like queries.
 *
 * Local instances automagically join into the cluster.
 *
 * To connect remote nodes see: https://apacheignite.readme.io/v2.0/docs/cluster-config
 */
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

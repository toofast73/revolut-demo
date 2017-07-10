package ru.live.toofast;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class PaymentProcessingApplication {

    public static void main(String[] args) {
        Ignition.start(clientNodeConfig());

        ResourceConfig config = new ResourceConfig();
        config.packages("ru.live.toofast.controller");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(2222);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }


    private static IgniteConfiguration clientNodeConfig(){
        IgniteConfiguration config = new IgniteConfiguration();
        config.setClientMode(true);
        return config;
    }


}

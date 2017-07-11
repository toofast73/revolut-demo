package ru.live.toofast;

import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.live.toofast.cache.CacheConfigurations;
import ru.live.toofast.controller.AccountController;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.repository.AccountRepository;

import javax.cache.Cache;

public class PaymentProcessingApplication {

    public static void main(String[] args) {
        Ignition.start(clientNodeConfig());

        ResourceConfig config = new ResourceConfig();
        config.register(new AccountController(accountRepository()));
        config.register(org.glassfish.jersey.jackson.JacksonFeature.class);
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
        config.setClientMode(false);
        return config;
    }

    private static AccountRepository accountRepository(){
        return new AccountRepository(accountIgniteCache(), accountSequence());
    }

    private static IgniteAtomicSequence accountSequence() {
        return Ignition.ignite().atomicSequence(
                "accountSequence",
                0,
                true
        );
    }

    private static Cache<Long, Account> accountIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.accountCacheConfiguration());
    }




}

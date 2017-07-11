package ru.live.toofast;

import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jetbrains.annotations.NotNull;
import ru.live.toofast.cache.CacheConfigurations;
import ru.live.toofast.controller.AccountController;
import ru.live.toofast.controller.PaymentController;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.entity.payment.TransactionEntry;
import ru.live.toofast.repository.AccountRepository;
import ru.live.toofast.repository.PaymentRepository;
import ru.live.toofast.repository.TransactionRepository;
import ru.live.toofast.service.FeeService;
import ru.live.toofast.service.PaymentService;

import javax.cache.Cache;

public class PaymentProcessingApplication {

    public static void main(String[] args) {
        Ignition.getOrStart(clientNodeConfig());

        ResourceConfig config = getResourceConfig();
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

    @NotNull
    private static ResourceConfig getResourceConfig() {
        ResourceConfig config = new ResourceConfig();
        config.register(new AccountController(accountRepository()));
        config.register(new PaymentController(paymentService()));
        config.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        return config;
    }

    private static PaymentService paymentService() {

        return new PaymentService(accountRepository(), paymentRepository(), transactionRepository(), new FeeService());
    }

    private static TransactionRepository transactionRepository() {
        return new TransactionRepository(transactionIgniteCache(), transactionSequence());
    }

    private static PaymentRepository paymentRepository() {
         return new PaymentRepository(paymentIgniteCache(), paymentSequence());
    }


    private static IgniteConfiguration clientNodeConfig(){
        IgniteConfiguration config = new IgniteConfiguration();
        config.setClientMode(true);
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

    private static IgniteAtomicSequence paymentSequence() {
        return Ignition.ignite().atomicSequence(
                "paymentSequence",
                0,
                true
        );
    }

    private static IgniteAtomicSequence transactionSequence() {
        return Ignition.ignite().atomicSequence(
                "transactionSequence",
                0,
                true
        );
    }

    private static Cache<Long, Account> accountIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.accountCacheConfiguration());
    }

    private static Cache<Long, Payment> paymentIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.paymentCacheConfiguration());
    }

    private static IgniteCache<Long, TransactionEntry> transactionIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.transactionCacheConfiguration());
    }

}

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
import ru.live.toofast.controller.TransactionEntryController;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.Payment;
import ru.live.toofast.entity.payment.TransactionEntry;
import ru.live.toofast.repository.AccountRepository;
import ru.live.toofast.repository.PaymentRepository;
import ru.live.toofast.repository.TransactionRepository;
import ru.live.toofast.service.FeeService;
import ru.live.toofast.service.PaymentService;

public class PaymentProcessingApplication {

    private static final int DEFAULT_PORT = 2222;

    public static void main(String[] args) {
        startCacheClient();

        startHttpServer(args);
    }

    private static void startHttpServer(String[] args) {
        int port = determinePort(args);

        ResourceConfig config = getResourceConfig();
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(port);
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

    private static void startCacheClient() {
        Ignition.getOrStart(clientNodeConfig());
    }

    /**
     * HTTP port is taken from application arguments. 2222 is used by default.
     */
    private static int determinePort(String[] args) {
        Integer port = DEFAULT_PORT;

        if(args.length > 0 && args[0] != null){
            port = Integer.valueOf(args[0]);
        }

        return port;
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


    /**
     * setClientMode(true) means, that processing-node won't store any data.
     * It's made to separate the responsibilities between storage and processing nodes.
     */
    private static IgniteConfiguration clientNodeConfig(){
        IgniteConfiguration config = new IgniteConfiguration();
        config.setClientMode(true);
        return config;
    }

    private static AccountRepository accountRepository(){
        return new AccountRepository(accountIgniteCache(), accountByCardCache(), accountByPhoneCache(), accountSequence());
    }

    /**
     * ID-generator for Accounts. The sequence is shared in cluster and ID uniqueness is guaranteed.
     * Unlike UUID it is protected from fluctuations and divine intervention.
     */
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

    private static IgniteCache<Long, Account> accountIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.accountCacheConfiguration());
    }

    private static IgniteCache<Long, Payment> paymentIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.paymentCacheConfiguration());
    }

    private static IgniteCache<Long, TransactionEntry> transactionIgniteCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.transactionCacheConfiguration());
    }

    /**
     * Key: credit card number
     * Val: associated accountId
     */
    private static IgniteCache<String, Long> accountByCardCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.accountsByCardConfiguration());
    }

    /**
     * Key: phone number
     * Val: associated accountId
     */
    private static IgniteCache<String, Long> accountByPhoneCache() {
        return Ignition.ignite().getOrCreateCache(CacheConfigurations.accountsByPhoneCacheConfiguration());
    }

}

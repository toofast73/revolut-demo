package ru.live.toofast.cache;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import ru.live.toofast.entity.account.Account;
import ru.live.toofast.entity.payment.BalanceEntry;
import ru.live.toofast.entity.payment.Payment;

/**
 * Configuration for entity tables.
 * AtomicityMode set to TRANSACTIONAL to activate transactions support.
 */
public class CacheConfigurations {

    public static CacheConfiguration accountCacheConfiguration() {
        CacheConfiguration<Long, Account> cc = new CacheConfiguration<>();
        cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cc.setIndexedTypes(Long.class, Account.class);
        cc.setName("accounts");
        return cc;
    }

    public static CacheConfiguration paymentCacheConfiguration() {
        CacheConfiguration<Long, Payment> cc = new CacheConfiguration<>();
        cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cc.setName("payments");
        return cc;
    }


    public static CacheConfiguration balanceEntryCacheConfiguration() {
        CacheConfiguration<Long, BalanceEntry> cc = new CacheConfiguration<>();
        cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cc.setName("balanceEntries");
        cc.setIndexedTypes(Long.class, BalanceEntry.class);
        return cc;
    }

    public static CacheConfiguration accountsByPhoneCacheConfiguration() {
        CacheConfiguration<String, Long> cc = new CacheConfiguration<>();
        cc.setName("accountsByPhone");
        return cc;
    }

    public static CacheConfiguration accountsByCardConfiguration() {
        CacheConfiguration<String, Long> cc = new CacheConfiguration<>();
        cc.setName("accountsByCard");
        return cc;
    }
}

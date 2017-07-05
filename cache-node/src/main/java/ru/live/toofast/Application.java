package ru.live.toofast;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;

import java.io.Serializable;

public class Application {
        /** Cache name. */
        private static final String CACHE_NAME = Application.class.getSimpleName();

        /**
         * Executes example.
         *
         * @param args Command line arguments, none required.
         * @throws IgniteException If example execution failed.
         */
        public static void main(String[] args) throws IgniteException {
            try (Ignite ignite = Ignition.start()) {
                System.out.println();
                System.out.println(">>> Cache transaction example started.");

                CacheConfiguration<Integer, Account> cfg = new CacheConfiguration<>(CACHE_NAME);

                cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

                // Auto-close cache at the end of the example.
                try (IgniteCache<Integer, Account> cache = ignite.getOrCreateCache(cfg)) {
                    // Initialize.
                    cache.put(1, new Account(1, 100));
                    cache.put(2, new Account(1, 200));

                    System.out.println();
                    System.out.println(">>> Accounts before deposit: ");
                    System.out.println(">>> " + cache.get(1));
                    System.out.println(">>> " + cache.get(2));

                    // Make transactional deposits.
                    deposit(cache, 1, 100);
                    deposit(cache, 2, 200);

                    System.out.println();
                    System.out.println(">>> Accounts after transfer: ");
                    System.out.println(">>> " + cache.get(1));
                    System.out.println(">>> " + cache.get(2));

                    System.out.println(">>> Cache transaction example finished.");
                }
                finally {
                    // Distributed cache could be removed from cluster only by #destroyCache() call.
                    ignite.destroyCache(CACHE_NAME);
                }
            }
        }

        /**
         * Make deposit into specified account.
         *
         * @param acctId Account ID.
         * @param amount Amount to deposit.
         * @throws IgniteException If failed.
         */
        private static void deposit(IgniteCache<Integer, Account> cache, int acctId, double amount) throws IgniteException {
            try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
                Account acct = cache.get(acctId);

                assert acct != null;

                // Deposit into account.
                acct.update(amount);

                // Store updated account in cache.
                cache.put(acctId, acct);

                tx.commit();
            }

            System.out.println();
            System.out.println(">>> Transferred amount: $" + amount);
        }

        /**
         * Account.
         */
        private static class Account implements Serializable {
            /** Account ID. */
            private int id;

            /** Account balance. */
            private double balance;

            /**
             * @param id Account ID.
             * @param balance Balance.
             */
            Account(int id, double balance) {
                this.id = id;
                this.balance = balance;
            }

            /**
             * Change balance by specified amount.
             *
             * @param amount Amount to add to balance (may be negative).
             */
            void update(double amount) {
                balance += amount;
            }

            /** {@inheritDoc} */
            @Override public String toString() {
                return "Account [id=" + id + ", balance=$" + balance + ']';
            }
        }
    }

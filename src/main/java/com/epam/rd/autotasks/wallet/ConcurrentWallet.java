package com.epam.rd.autotasks.wallet;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class ConcurrentWallet implements Wallet {
    private final PaymentLog log;
    private final List<Account> accounts;
    public ConcurrentWallet(List<Account> accounts, PaymentLog log) {
        this.accounts = accounts;
        this.log = log;
    }
    @Override
    public void pay(String recipient, long amount) throws Exception {
        for (Account account : accounts) {
            Lock accountLock = account.lock();
            accountLock.lock();

            try {
                if (account.balance() >= amount) {
                    account.pay(amount);
                    log.add(account, recipient, amount);
                    return;
                }
            }
            finally {
                accountLock.unlock();
            }
        }
        throw new ShortageOfMoneyException(recipient, amount);
    }
}

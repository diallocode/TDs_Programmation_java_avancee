package td4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public final class Ex03 {
    static final class BankAccount {
        private final int accountId;
        private int balance;
        private final ReentrantLock lock = new ReentrantLock();
        BankAccount(int id, int initial) {
            if (initial < 0) throw new IllegalArgumentException();
            accountId = id; balance = initial;
        }
        static void positive(int n) { if (n <= 0) throw new IllegalArgumentException(); }
        public void deposit(int amount) {
            positive(amount); lock.lock();
            try { balance = Math.addExact(balance, amount); } finally { lock.unlock(); }
        }
        public void withdraw(int amount) {
            positive(amount); lock.lock();
            try {
                if (balance < amount) throw new IllegalStateException("Fonds insuffisants");
                balance -= amount;
            } finally { lock.unlock(); }
        }
        public int getBalance() {
            lock.lock(); try { return balance; } finally { lock.unlock(); }
        }
        public static void transfer(BankAccount from, BankAccount to, int amount) {
            positive(amount);
            if (from == to) return;
            if (from.accountId == to.accountId) throw new IllegalArgumentException("Identifiants dupliqués");
            BankAccount first = from.accountId < to.accountId ? from : to;
            BankAccount second = first == from ? to : from;
            first.lock.lock();
            try {
                second.lock.lock();
                try {
                    if (from.balance < amount) throw new IllegalStateException("Fonds insuffisants");
                    int newTarget = Math.addExact(to.balance, amount);
                    from.balance -= amount; to.balance = newTarget;
                } finally { second.lock.unlock(); }
            } finally { first.lock.unlock(); }
        }
    }
    public static void main(String[] args) throws Exception {
        BankAccount a = new BankAccount(1, 500), b = new BankAccount(2, 800), c = new BankAccount(3, 1000);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Callable<Void> t1 = () -> { a.deposit(100); BankAccount.transfer(a, b, 50); return null; };
        Callable<Void> t2 = () -> { b.withdraw(80); BankAccount.transfer(b, c, 120); return null; };
        Callable<Void> t3 = () -> { c.deposit(30); BankAccount.transfer(c, a, 70); return null; };
        try {
            for (Future<Void> f : pool.invokeAll(List.of(t1, t2, t3))) f.get();
        } finally { pool.shutdown(); }
        int total = a.getBalance() + b.getBalance() + c.getBalance();
        if (a.getBalance() != 620 || b.getBalance() != 650 || c.getBalance() != 1080 || total != 2350)
            throw new AssertionError();
        System.out.printf("Soldes : %d / %d / %d ; total=%d (2300+130-80)%n",
                a.getBalance(), b.getBalance(), c.getBalance(), total);
        BankAccount x = new BankAccount(4, Integer.MAX_VALUE), y = new BankAccount(5, 10);
        try { BankAccount.transfer(y, x, 1); throw new AssertionError(); }
        catch (ArithmeticException expected) {
            if (y.getBalance() != 10 || x.getBalance() != Integer.MAX_VALUE) throw new AssertionError();
        }
        System.out.println("Débordement refusé sans débit partiel : OK");
    }
}

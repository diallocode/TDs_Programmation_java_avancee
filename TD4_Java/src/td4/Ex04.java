package td4;

import java.lang.management.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public final class Ex04 {
    static final class Resource {
        final String name;
        final ReentrantLock lock = new ReentrantLock();
        Resource(String name) { this.name = name; }
        public boolean tryUse(Resource other) throws InterruptedException {
            if (!lock.tryLock(100, TimeUnit.MILLISECONDS)) return false;
            try {
                if (!other.lock.tryLock(100, TimeUnit.MILLISECONDS)) return false;
                try {
                    System.out.printf("%s utilise %s + %s%n", Thread.currentThread().getName(), name, other.name);
                    return true;
                } finally { other.lock.unlock(); }
            } finally { lock.unlock(); }
        }
    }
    static void worker(Resource a, Resource b) {
        try {
            while (!a.tryUse(b)) Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
    public static void main(String[] args) throws Exception {
        Resource a = new Resource("R1"), b = new Resource("R2");
        if (args.length > 0 && args[0].equals("--deadlock")) {
            CountDownLatch acquired = new CountDownLatch(2);
            Runnable ab = () -> deadlock(a, b, acquired);
            Runnable ba = () -> deadlock(b, a, acquired);
            Thread t1 = new Thread(ab, "Thread-1"), t2 = new Thread(ba, "Thread-2");
            t1.setDaemon(true); t2.setDaemon(true); t1.start(); t2.start();
            acquired.await();
            long[] ids = null;
            for (int i = 0; i < 100 && ids == null; i++) {
                Thread.sleep(10); ids = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
            }
            if (ids == null) throw new AssertionError("Deadlock non détecté");
            System.out.println("Deadlock détecté : " + ids.length + " threads (verrous ReentrantLock).");
            return;
        }
        Thread t1 = new Thread(() -> worker(a, b), "Thread-1");
        Thread t2 = new Thread(() -> worker(b, a), "Thread-2");
        t1.start(); t2.start(); t1.join(5000); t2.join(5000);
        if (t1.isAlive() || t2.isAlive()) throw new AssertionError("Délai dépassé");
        System.out.println("Deux threads terminés ; aucun verrou conservé.");
    }
    static void deadlock(Resource a, Resource b, CountDownLatch gate) {
        a.lock.lock();
        try {
            gate.countDown();
            try { gate.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            b.lock.lock(); try { } finally { b.lock.unlock(); }
        } finally { a.lock.unlock(); }
    }
}

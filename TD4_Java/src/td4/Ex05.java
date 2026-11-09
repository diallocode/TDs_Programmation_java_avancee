package td4;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

public final class Ex05 {
    static final class Railway {
        private final Object voie1 = new Object(), voie2 = new Object();
        private final CountDownLatch gate = new CountDownLatch(2);
        public void passageAvecDeadlock(String name, boolean reverse) {
            Object first = reverse ? voie2 : voie1, second = reverse ? voie1 : voie2;
            synchronized (first) {
                System.out.println(name + " a réservé la voie " + (reverse ? 2 : 1));
                gate.countDown();
                try { gate.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                synchronized (second) { System.out.println(name + " traverse"); }
            }
        }
        public void passageSansDeadlock(String name) {
            synchronized (voie1) {
                synchronized (voie2) { System.out.println(name + " traverse (voie1 + voie2)"); }
            }
        }
    }
    static final class Train extends Thread {
        final Railway railway; final boolean safeMode, reverse;
        Train(String name, int priority, Railway r, boolean safe, boolean reverse) {
            super(name); setPriority(priority); railway = r; safeMode = safe; this.reverse = reverse;
        }
        public void run() {
            if (safeMode) railway.passageSansDeadlock(getName());
            else railway.passageAvecDeadlock(getName(), reverse);
        }
    }
    public static void main(String[] args) throws Exception {
        boolean safe = args.length == 0 || !args[0].equals("--deadlock");
        Railway r = new Railway();
        Train a = new Train("Train Rapide", Thread.MAX_PRIORITY, r, safe, false);
        Train b = new Train("Train Lent", Thread.MIN_PRIORITY, r, safe, true);
        if (!safe) { a.setDaemon(true); b.setDaemon(true); }
        a.start(); b.start();
        if (safe) { a.join(); b.join(); System.out.println("Deux passages terminés."); }
        else {
            r.gate.await(); long[] ids = null;
            for (int i = 0; i < 100 && ids == null; i++) {
                Thread.sleep(10); ids = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
            }
            if (ids == null) throw new AssertionError();
            System.out.println("Deadlock détecté : deux trains, deux voies.");
        }
    }
}

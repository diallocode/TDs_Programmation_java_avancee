package td4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public final class Ex07 {
    static final class Imprimante {
        private final ReentrantLock lock = new ReentrantLock();
        private final long start = System.nanoTime();
        void journal(String text) {
            System.out.printf(Locale.ROOT, "%.3f s | %s%n", (System.nanoTime()-start)/1e9, text);
        }
        private void impression(String employe, String doc) throws InterruptedException {
            journal(employe + " imprime " + doc);
            Thread.sleep(2000);
            journal(employe + " a fini");
        }
        public void imprimer(String employe, String doc) {
            lock.lock();
            try { impression(employe, doc); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); journal("Impression annulée"); }
            finally { lock.unlock(); }
        }
        public boolean essayerImprimer(String employe, String doc) {
            try {
                if (!lock.tryLock(3, TimeUnit.SECONDS)) {
                    journal(employe + " : Imprimante occupée, réessayez plus tard"); return false;
                }
                try { impression(employe, doc); return true; }
                finally { lock.unlock(); }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); journal(employe + " : impression annulée"); return false;
            }
        }
    }
    static final class FileDocuments {
        private final List<String> docs = new ArrayList<>();
        private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        public List<String> consulter() {
            rw.readLock().lock();
            try { return List.copyOf(docs); } finally { rw.readLock().unlock(); }
        }
        public void ajouter(String doc) {
            rw.writeLock().lock();
            try { docs.add(doc); } finally { rw.writeLock().unlock(); }
        }
    }
    public static void main(String[] args) throws Exception {
        boolean timeout = args.length > 0 && args[0].equals("timeout");
        Imprimante imp = new Imprimante(); CountDownLatch gate = new CountDownLatch(1);
        List<Thread> employees = new ArrayList<>();
        for (String name : List.of("Alice", "Bob", "Chloé", "David")) {
            Thread t = new Thread(() -> {
                try { gate.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                if (timeout) imp.essayerImprimer(name, "rapport.pdf"); else imp.imprimer(name, "rapport.pdf");
            }, name);
            employees.add(t); t.start();
        }
        gate.countDown(); for (Thread t : employees) t.join();
        FileDocuments docs = new FileDocuments(); ExecutorService pool = Executors.newFixedThreadPool(11);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            tasks.add(() -> { for (int i=0;i<100;i++) docs.ajouter("doc-"+i); return null; });
            for (int j=0;j<10;j++) tasks.add(() -> {
                for (int i=0;i<1000;i++) docs.consulter(); return null;
            });
            for (Future<Void> f : pool.invokeAll(tasks)) f.get();
        } finally { pool.shutdown(); }
        if (docs.consulter().size()!=100) throw new AssertionError();
        System.out.println("File protégée : 10 lecteurs, 1 rédacteur, 100 documents, OK.");
    }
}

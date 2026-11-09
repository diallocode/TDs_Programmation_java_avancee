package td4;

import java.lang.management.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public final class Ex08 {
    static final class Entrepot {
        private final int id;
        private int stock;
        private final ReentrantLock lock = new ReentrantLock();
        Entrepot(int id, int stock) { this.id=id; this.stock=stock; }
        public int getId() { return id; }
        public synchronized int getStock() { return stock; }
        public synchronized void retirer(int n) { stock -= n; }
        public synchronized void ajouter(int n) { stock += n; }
    }
    static final class Logistique {
        static void arguments(Entrepot a, Entrepot b, int n) {
            if (n <= 0 || (a != b && a.id == b.id)) throw new IllegalArgumentException();
        }
        public static void transferer(Entrepot from, Entrepot to, int n) {
            arguments(from,to,n); if (from==to) return;
            Entrepot first=from.id<to.id?from:to, second=first==from?to:from;
            synchronized(first) {
                synchronized(second) { from.retirer(n); to.ajouter(n); }
            }
        }
    }
    // Modèle indépendant : toutes les lectures/écritures utilisent le même ReentrantLock.
    static final class EntrepotLock {
        final int id; private int stock; final ReentrantLock lock = new ReentrantLock();
        EntrepotLock(int id, int stock) { this.id=id; this.stock=stock; }
        int getStock() { lock.lock(); try { return stock; } finally { lock.unlock(); } }
    }
    static void transfererTryLock(EntrepotLock a, EntrepotLock b, int n) throws InterruptedException {
        if (n<=0) throw new IllegalArgumentException();
        if (a==b) return;
        for (;;) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            if (a.lock.tryLock()) {
                try {
                    if (b.lock.tryLock()) {
                        try { a.stock -= n; b.stock += n; return; }
                        finally { b.lock.unlock(); }
                    }
                } finally { a.lock.unlock(); }
            }
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 6));
        }
    }
    static void unsafe(Entrepot a, Entrepot b, CountDownLatch gate) {
        synchronized(a) {
            gate.countDown();
            try { gate.await(); } catch(InterruptedException e) { Thread.currentThread().interrupt(); return; }
            synchronized(b) { a.retirer(1); b.ajouter(1); }
        }
    }
    static void original(Entrepot de, Entrepot vers, int n) {
        synchronized(de) {
            synchronized(vers) { de.retirer(n); vers.ajouter(n); }
        }
    }
    public static void main(String[] args) throws Exception {
        if(args.length>0 && args[0].equals("--deadlock")) {
            Entrepot a=new Entrepot(1,1000), b=new Entrepot(2,1000); CountDownLatch gate=new CountDownLatch(2);
            Thread t1=new Thread(()->unsafe(a,b,gate),"T1"), t2=new Thread(()->unsafe(b,a,gate),"T2");
            t1.start(); t2.start(); gate.await();
            System.out.println("PID="+ProcessHandle.current().pid()+" ; attente circulaire forcée pour jstack.");
            t1.join(); t2.join(); return;
        }
        boolean initial=args.length>0 && args[0].equals("--original");
        Entrepot a=new Entrepot(1,1000), b=new Entrepot(2,1000);
        Thread t1=new Thread(()->{for(int i=0;i<10000;i++) {
            if(initial)original(a,b,1);else Logistique.transferer(a,b,1);
        }});
        Thread t2=new Thread(()->{for(int i=0;i<10000;i++) {
            if(initial)original(b,a,1);else Logistique.transferer(b,a,1);
        }});
        t1.start(); t2.start(); t1.join(); t2.join();
        if(a.getStock()+b.getStock()!=2000) throw new AssertionError();
        if(initial){System.out.println("Version initiale terminée ; total=2000");return;}
        System.out.printf("Ordre fixe : Paris=%d, Lyon=%d, total=%d%n",a.getStock(),b.getStock(),a.getStock()+b.getStock());
        EntrepotLock x=new EntrepotLock(1,1000), y=new EntrepotLock(2,1000);
        ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            Future<?> f1=pool.submit(()->{try{for(int i=0;i<10000;i++)transfererTryLock(x,y,1);}
                catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}});
            Future<?> f2=pool.submit(()->{try{for(int i=0;i<10000;i++)transfererTryLock(y,x,1);}
                catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}});
            f1.get(10,TimeUnit.SECONDS); f2.get(10,TimeUnit.SECONDS);
        } finally { pool.shutdownNow(); }
        if(x.getStock()+y.getStock()!=2000)throw new AssertionError();
        System.out.printf("tryLock : Paris=%d, Lyon=%d, total=%d%n",x.getStock(),y.getStock(),x.getStock()+y.getStock());
    }
}

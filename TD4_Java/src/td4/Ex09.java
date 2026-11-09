package td4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public final class Ex09 {
    static final class Statistiques {
        final AtomicLong visites=new AtomicLong();
        final AtomicInteger record=new AtomicInteger();
        void nouvelleVisite(){visites.incrementAndGet();}
        void connectes(int n){record.accumulateAndGet(n,Math::max);}
        void connectesCAS(int n){
            while(true){
                int actuel=record.get();
                if(n<=actuel)return;
                if(record.compareAndSet(actuel,n))return;
            }
        }
    }
    record Intervalle(int min,int max){
        Intervalle {if(min>max)throw new IllegalArgumentException("min > max");}
    }
    static final class Plage {
        final AtomicReference<Intervalle> intervalle=new AtomicReference<>(new Intervalle(0,10));
        void changer(int min,int max){intervalle.set(new Intervalle(min,max));}
        Intervalle lire(){return intervalle.get();}
    }
    static long measure(ExecutorService pool,int threads,int repetitions,Runnable increment) throws Exception {
        CountDownLatch ready=new CountDownLatch(threads),start=new CountDownLatch(1);
        List<Future<?>> tasks=new ArrayList<>();
        for(int i=0;i<threads;i++)tasks.add(pool.submit(()->{
            ready.countDown();
            try{start.await();}catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}
            for(int j=0;j<repetitions;j++)increment.run();
        }));
        ready.await();long t=System.nanoTime();start.countDown();
        for(Future<?> f:tasks)f.get();
        return System.nanoTime()-t;
    }
    static void benchmark() throws Exception {
        int n=16,repetitions=100000;long expected=(long)n*repetitions;
        ExecutorService pool=Executors.newFixedThreadPool(n);
        List<Long> atomicTimes=new ArrayList<>(),adderTimes=new ArrayList<>();
        try {
            for(int round=-2;round<5;round++) {
                AtomicLong atomic=new AtomicLong();LongAdder adder=new LongAdder();long ta,tl;
                if((round&1)==0){
                    ta=measure(pool,n,repetitions,atomic::incrementAndGet);
                    tl=measure(pool,n,repetitions,adder::increment);
                } else {
                    tl=measure(pool,n,repetitions,adder::increment);
                    ta=measure(pool,n,repetitions,atomic::incrementAndGet);
                }
                if(atomic.get()!=expected||adder.sum()!=expected)throw new AssertionError();
                if(round>=0){atomicTimes.add(ta);adderTimes.add(tl);}
            }
        } finally {pool.shutdown();}
        Collections.sort(atomicTimes);Collections.sort(adderTimes);
        System.out.printf(Locale.ROOT,"16 threads, 100000 incréments/thread : AtomicLong=%.3f ms ; LongAdder=%.3f ms (médianes, 5 mesures)%n",
            atomicTimes.get(2)/1e6,adderTimes.get(2)/1e6);
        System.out.println("Deux compteurs vérifiés : 1600000 ; 2 tours de chauffe, ordre alterné.");
    }
    public static void main(String[] args) throws Exception {
        Statistiques s=new Statistiques();ExecutorService pool=Executors.newFixedThreadPool(8);
        try {measure(pool,8,100000,s::nouvelleVisite);}finally{pool.shutdown();}
        if(s.visites.get()!=800000)throw new AssertionError();
        s.connectes(20);s.connectes(12);s.connectesCAS(30);s.connectesCAS(15);
        if(s.record.get()!=30)throw new AssertionError();
        Plage p=new Plage();p.changer(20,30);Intervalle snapshot=p.lire();
        if(snapshot.min()!=20||snapshot.max()!=30)throw new AssertionError();
        System.out.printf("8 threads : visites=%d ; record CAS=%d ; intervalle=%s%n",s.visites.get(),s.record.get(),snapshot);
        benchmark();
    }
}

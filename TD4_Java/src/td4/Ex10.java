package td4;

import java.util.*;
import java.util.concurrent.*;

/** Exercice « Analyser des logs à la chaîne », numéroté 6 dans le sujet. */
public final class Ex10 {
    static final String FIN="FIN";
    static final int N=100000;
    record Resultat(Map<String,Integer> comptes,long nanos){}
    static Resultat run(int workers) throws Exception {
        BlockingQueue<String> queue=new ArrayBlockingQueue<>(1000);
        ConcurrentHashMap<String,Integer> comptes=new ConcurrentHashMap<>();
        ExecutorService pool=Executors.newFixedThreadPool(workers+1);
        List<Future<?>> consumers=new ArrayList<>();long start=System.nanoTime();
        try {
            for(int i=0;i<workers;i++)consumers.add(pool.submit(()->{
                try {
                    for(;;){
                        String line=queue.take();
                        if(FIN.equals(line))return;
                        String code=line.substring(line.lastIndexOf(' ')+1);
                        comptes.merge(code,1,Integer::sum);
                    }
                } catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}
            }));
            Future<?> producer=pool.submit(()->{
                SplittableRandom random=new SplittableRandom(42);String[] codes={"200","200","200","404","500"};
                try {
                    for(int i=0;i<N;i++)queue.put("2026-10-05 10:12:31 GET /accueil "+codes[random.nextInt(codes.length)]);
                    for(int i=0;i<workers;i++)queue.put(FIN);
                }catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}
            });
            producer.get(20,TimeUnit.SECONDS);
            for(Future<?> f:consumers)f.get(20,TimeUnit.SECONDS);
            int total=comptes.values().stream().mapToInt(Integer::intValue).sum();
            if(total!=N)throw new AssertionError("Lignes perdues : "+total);
            return new Resultat(new TreeMap<>(comptes),System.nanoTime()-start);
        }finally{pool.shutdownNow();}
    }
    public static void main(String[] args) throws Exception {
        run(2);run(4); // chauffe ; mêmes données dans tous les essais
        List<Long> two=new ArrayList<>(),four=new ArrayList<>();Resultat a=null,b=null;
        for(int i=0;i<5;i++){
            if((i&1)==0){a=run(2);b=run(4);}else{b=run(4);a=run(2);}
            if(!a.comptes.equals(b.comptes))throw new AssertionError();
            two.add(a.nanos);four.add(b.nanos);
        }
        Collections.sort(two);Collections.sort(four);
        System.out.println("Comptes communs (graine 42) : "+a.comptes+" ; somme=100000");
        System.out.printf(Locale.ROOT,"Médiane 5 essais : 2 analyseurs=%.3f ms ; 4 analyseurs=%.3f ms%n",two.get(2)/1e6,four.get(2)/1e6);
    }
}

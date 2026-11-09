package td4;

import java.util.*;
import java.util.concurrent.*;

/** Exercice « Préparer un voyage », numéroté 7 à la fin du sujet. */
public final class Ex11 {
    static final class Services {
        final ExecutorService pool;
        final boolean fixture;
        final String mode;
        Services(ExecutorService pool,boolean fixture){this(pool,fixture,"normal");}
        Services(ExecutorService pool,boolean fixture,String mode){this.pool=pool;this.fixture=fixture;this.mode=mode;}
        static void attendre(long ms){
            try{Thread.sleep(ms);}
            catch(InterruptedException e){Thread.currentThread().interrupt();throw new CancellationException("Service interrompu");}
        }
        double prixVol(String ville){attendre(1000);return 180.0;}
        double prixHotel(String ville){attendre(1500);return 95.0;}
        String meteo(String ville){
            if(mode.equals("erreur")) {attendre(100);throw new IllegalStateException("erreur contrôlée");}
            if(mode.equals("lent")) {attendre(3000);return "Ensoleillé après 3 s";}
            attendre(fixture?700:ThreadLocalRandom.current().nextInt(500,4000));
            if(!fixture&&ThreadLocalRandom.current().nextInt(4)==0)
                throw new IllegalStateException("service météo indisponible");
            return "Ensoleillé";
        }
        CompletableFuture<String> villeAsync(String code){
            return CompletableFuture.supplyAsync(()->{attendre(300);return "Paris";},pool);
        }
        CompletableFuture<String> meteoAsync(String ville){
            return CompletableFuture.supplyAsync(()->meteo(ville),pool);
        }
    }
    record Page(double total,String meteo){}
    static CompletableFuture<Page> page(Services s,String code){
        CompletableFuture<String> ville=s.villeAsync(code);
        CompletableFuture<Double> vol=ville.thenCompose(v->CompletableFuture.supplyAsync(()->s.prixVol(v),s.pool));
        CompletableFuture<Double> hotel=ville.thenCompose(v->CompletableFuture.supplyAsync(()->s.prixHotel(v),s.pool));
        // Le délai porte sur ville + météo, à partir de la création de la page.
        CompletableFuture<String> meteo=ville.thenCompose(s::meteoAsync)
            .completeOnTimeout("Météo inconnue",1900,TimeUnit.MILLISECONDS)
            .exceptionally(e->"Météo inconnue");
        return vol.thenCombine(hotel,Double::sum).thenCombine(meteo,Page::new);
    }
    public static void main(String[] args){
        boolean fixture=Arrays.asList(args).contains("--fixture");
        ExecutorService pool=Executors.newFixedThreadPool(8);
        try{
            Services s=new Services(pool,fixture);
            long start=System.nanoTime();double vol=s.prixVol("Paris"),hotel=s.prixHotel("Paris");
            String weather;
            try{weather=s.meteo("Paris");}catch(IllegalStateException e){weather="Météo inconnue";}
            System.out.printf(Locale.ROOT,"Séquentiel : %.1f EUR, %s, %.3f s%n",vol+hotel,weather,(System.nanoTime()-start)/1e9);
            start=System.nanoTime();
            CompletableFuture<Double> f=CompletableFuture.supplyAsync(()->s.prixVol("Paris"),pool);
            CompletableFuture<Double> h=CompletableFuture.supplyAsync(()->s.prixHotel("Paris"),pool);
            double total=f.thenCombine(h,Double::sum).join();
            if(total!=275.0)throw new AssertionError();
            System.out.printf(Locale.ROOT,"Vol + hôtel parallèles : %.1f EUR, %.3f s%n",total,(System.nanoTime()-start)/1e9);
            System.out.println("CDG -> météo : "+s.villeAsync("CDG").thenCompose(s::meteoAsync)
                .completeOnTimeout("Météo inconnue",2,TimeUnit.SECONDS).exceptionally(e->"Météo inconnue").join());
            start=System.nanoTime();Page page=page(s,"CDG").join();
            if(page.total()!=275.0)throw new AssertionError();
            System.out.printf(Locale.ROOT,"Page complète (ville incluse) : %s, %.3f s%n",page,(System.nanoTime()-start)/1e9);
            for(String mode:List.of("erreur","lent")) {
                start=System.nanoTime();Page fallbackPage=page(new Services(pool,true,mode),"CDG").join();
                if(!fallbackPage.meteo().equals("Météo inconnue") || fallbackPage.total()!=275.0)throw new AssertionError();
                System.out.printf(Locale.ROOT,"Page, météo %s : %s, %.3f s%n",mode,fallbackPage,(System.nanoTime()-start)/1e9);
            }
            // Deux cas contrôlés : erreur et dépassement, indépendants du tirage aléatoire.
            String fallback=CompletableFuture.<String>failedFuture(new IllegalStateException("test"))
                .exceptionally(e->"Météo inconnue").join();
            CompletableFuture<String> slow=CompletableFuture.supplyAsync(()->{Services.attendre(3000);return "Trop tard";},pool);
            start=System.nanoTime();String timed=slow.completeOnTimeout("Météo inconnue",2,TimeUnit.SECONDS).join();
            if(!fallback.equals("Météo inconnue")||!timed.equals("Météo inconnue"))throw new AssertionError();
            System.out.printf(Locale.ROOT,"Erreur contrôlée : %s ; timeout contrôlé : %s en %.3f s%n",fallback,timed,(System.nanoTime()-start)/1e9);
            List<String> villes=List.of("Paris","Lyon","Rome","Tokyo","Dakar");
            List<CompletableFuture<String>> results=villes.stream().map(v->s.meteoAsync(v)
                .completeOnTimeout("Météo inconnue",2,TimeUnit.SECONDS).exceptionally(e->"Météo inconnue")
                .thenApply(m->v+" : "+m)).toList();
            CompletableFuture.allOf(results.toArray(CompletableFuture<?>[]::new))
                .thenRun(()->results.forEach(r->System.out.println(r.join()))).join();
        }finally{pool.shutdownNow();}
    }
}

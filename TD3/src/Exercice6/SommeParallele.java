package Exercice6;

import java.util.*;
import java.util.concurrent.*;

public class SommeParallele {
    public static void main(String[] args)
            throws Exception {

        ExecutorService pool =
                Executors.newFixedThreadPool(4);

        List<Callable<Long>> taches = new ArrayList<>();

        for (int k = 0; k < 4; k++) {

            long debut = k * 1_000_000L + 1;
            long fin = (k + 1) * 1_000_000L;

            taches.add(() -> {
                long s = 0;

                for (long i = debut; i <= fin; i++)
                    s += i;

                return s;
            });
        }

        List<Future<Long>> resultats =
                pool.invokeAll(taches);

        long total = 0;

        for (Future<Long> f : resultats) {
            total += f.get();
        }

        System.out.println("Total : " + total);

        pool.shutdown();
    }
}
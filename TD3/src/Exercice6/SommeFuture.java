package Exercice6;

import java.util.concurrent.*;

public class SommeFuture {
    public static void main(String[] args)
            throws Exception {

        ExecutorService pool =
                Executors.newFixedThreadPool(2);

        Callable<Long> somme = () -> {
            long s = 0;

            for (int i = 1; i <= 1_000_000; i++)
                s += i;

            return s;
        };

        Future<Long> f = pool.submit(somme);

        System.out.println("Résultat : " + f.get());

        pool.shutdown();
    }
}
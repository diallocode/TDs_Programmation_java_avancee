package Exercice6;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoolSimple {
    public static void main(String[] args) {

        ExecutorService pool =
                Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 6; i++) {
            int numero = i;

            pool.execute(() ->
                System.out.println(
                    "Tâche " + numero + " exécutée par "
                    + Thread.currentThread().getName()
                )
            );
        }

        pool.shutdown();
    }
}
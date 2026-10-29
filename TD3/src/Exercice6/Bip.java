package Exercice6;

import java.util.concurrent.*;

public class Bip {
    public static void main(String[] args)
            throws InterruptedException {

        ScheduledExecutorService pool =
                Executors.newScheduledThreadPool(1);

        pool.scheduleAtFixedRate(
                () -> System.out.println("Bip"),
                0,
                1,
                TimeUnit.SECONDS
        );

        Thread.sleep(5000);

        pool.shutdown();
    }
}
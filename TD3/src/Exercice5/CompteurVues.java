package Exercice5;

import java.util.concurrent.atomic.AtomicInteger;

public class CompteurVues {
    private AtomicInteger vues = new AtomicInteger(0);

    public void nouvelleVue() {
        vues.incrementAndGet();
    }

    public int getVues() {
        return vues.get();
    }

    public static void main(String[] args)
            throws InterruptedException {

        CompteurVues v = new CompteurVues();

        Runnable spectateurs = () -> {
            for (int i = 0; i < 100_000; i++)
                v.nouvelleVue();
        };

        Thread t1 = new Thread(spectateurs);
        Thread t2 = new Thread(spectateurs);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(
                "Vues : " + v.getVues()
                + " (attendu : 200000)"
        );
    }
}
package Exercice7;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LigneDeDepart {

    private final int nbCoureurs;

    private final AtomicInteger nbPrets =
            new AtomicInteger(0);

    private final CountDownLatch prets;
    private final CountDownLatch depart;

    public LigneDeDepart(int nbCoureurs) {
        this.nbCoureurs = nbCoureurs;

        prets = new CountDownLatch(nbCoureurs);
        depart = new CountDownLatch(1);
    }

    public void seDeclarerPret(String nom) {

        int numero = nbPrets.incrementAndGet();

        System.out.println(
                nom + " est prêt ("
                + numero + "/" + nbCoureurs + ")"
        );

        prets.countDown();
    }

    public void attendreTousPrets()
            throws InterruptedException {

        prets.await();

        System.out.println(
                "Arbitre : tout le monde est prêt !"
        );
    }

    public void donnerDepart() {

        System.out.println(">>> PAN ! Départ ! <<<");

        depart.countDown();
    }

    public void attendreDepart()
            throws InterruptedException {

        depart.await();
    }
}
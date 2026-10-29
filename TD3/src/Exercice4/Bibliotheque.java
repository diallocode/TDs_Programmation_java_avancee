package Exercice4;

import java.util.ArrayList;
import java.util.List;

public class Bibliotheque {
    private List<String> livres = new ArrayList<>();
    private List<String> lecteurs = new ArrayList<>();

    public synchronized void ajouterLivre(String titre) {
        livres.add(titre);
        pause();
    }

    public synchronized void ajouterLecteur(String nom) {
        lecteurs.add(nom);
        pause();
    }

    private void pause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) { }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Bibliotheque b = new Bibliotheque();

        long debut = System.currentTimeMillis();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                b.ajouterLivre("Livre " + i);
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                b.ajouterLecteur("Lecteur " + i);
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        long duree = System.currentTimeMillis() - debut;

        System.out.println("Durée : " + duree + " ms");
    }
}
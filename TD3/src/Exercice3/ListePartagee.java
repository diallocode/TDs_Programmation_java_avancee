package Exercice3;
import java.util.ArrayList;
import java.util.List;

public class ListePartagee {
    public static void main(String[] args)
            throws InterruptedException {

        List<Integer> liste = new ArrayList<>();

        Runnable tache = () -> {
            for (int i = 0; i < 10_000; i++) {
                liste.add(i);
            }
        };

        Thread t1 = new Thread(tache);
        Thread t2 = new Thread(tache);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Taille attendue : 20000");
        System.out.println("Taille obtenue : " + liste.size());
    }
}
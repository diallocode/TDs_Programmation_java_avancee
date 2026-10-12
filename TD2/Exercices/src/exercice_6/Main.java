package exercice_6;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int MAX_PLACES = 100;
        int NB_THREADS = 50;

        SystemReservation s = new SystemReservation(MAX_PLACES);
        AtomicInteger totalReserve = new AtomicInteger();
        AtomicInteger totalAnnule = new AtomicInteger();

        Thread[] T = new Thread[NB_THREADS];
        for (int i = 0; i < NB_THREADS; i++) {
            T[i] = new Thread(
                new Utilisateur(s, i, 5 + (int) (Math.random() * 10), totalReserve, totalAnnule),
                "Utilisateur_" + i);
            T[i].start();
        }
        for (Thread t : T) t.join();

        int occupees = 0;
        for (int i = 0; i < MAX_PLACES; i++) {
            int owner = s.getOwner(i);
            System.out.println("Place_" + i + " : " + (owner == -1 ? "libre" : "Utilisateur_" + owner));
            if (owner != -1) occupees++;
        }

        System.out.println("Réservations réussies : " + totalReserve.get());
        System.out.println("Annulations réussies  : " + totalAnnule.get());
        System.out.println("Places occupées       : " + occupees);
        System.out.println("Cohérent : " + (totalReserve.get() - totalAnnule.get() == occupees));
    }
}
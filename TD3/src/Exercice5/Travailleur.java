package Exercice5;

public class Travailleur implements Runnable {
    private volatile boolean actif = true;

    public void arreter() {
        actif = false;
    }

    public void run() {
        long tours = 0;

        while (actif) {
            tours++;
        }

        System.out.println("Arrêté après " + tours + " tours");
    }

    public static void main(String[] args)
            throws InterruptedException {

        Travailleur w = new Travailleur();

        Thread t = new Thread(w);

        t.start();

        Thread.sleep(1000);

        w.arreter();

        System.out.println("Demande d'arrêt envoyée");

        t.join(3000);

        System.out.println(
                t.isAlive()
                ? "Le thread tourne encore !"
                : "Le thread est bien arrêté"
        );

        System.exit(0);
    }
}

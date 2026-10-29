package Exercice2;

public class Horloge implements Runnable {

    public void run() {
        while (true) {
            System.out.println("Tic : " + new java.util.Date());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread t = new Thread(new Horloge());

        // Étape b :
        //t.setDaemon(true);

        t.start();
        
        t.setDaemon(true); //: Exception java.lang.IllegalThreadStateException


        Thread.sleep(3500);

        System.out.println("Fin du main");
    }
}

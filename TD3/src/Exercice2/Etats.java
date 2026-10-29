package Exercice2;

public class Etats {
    public static void main(String[] args)
            throws InterruptedException {

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrompu");
            }
        });

        System.out.println("1. " + t.getState());

        t.start();

        System.out.println("2. " + t.getState());

        Thread.sleep(200);

        System.out.println("3. " + t.getState());

        t.join();

        System.out.println("4. " + t.getState());
        
        t.start(); //: Exception in thread "main"
    }
}

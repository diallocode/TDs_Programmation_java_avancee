package Exercice1;

public class NavigateurP {
    public static void main(String[] args)
            throws InterruptedException {

        Telechargement d1 =
                new Telechargement("photo.jpg", 2);
        Telechargement d2 =
                new Telechargement("video.mp4", 3);
        Telechargement d3 =
                new Telechargement("cours.pdf", 1);

        long debut = System.currentTimeMillis();

        Thread t1 = new Thread(d1, "DL-1");
        Thread t2 = new Thread(d2, "DL-2");
        Thread t3 = new Thread(d3, "DL-3");

        t1.start();
        t2.start();
        t3.start();

        //t1.join();
        //t2.join();
        //t3.join();
 
        long duree = System.currentTimeMillis() - debut;

        System.out.println("Durée totale : " + duree + " ms");
    }
}

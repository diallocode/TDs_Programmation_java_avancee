package Exercice1;

public class Navigateur {
    public static void main(String[] args)
            throws InterruptedException {

        Telechargement d1 =
                new Telechargement("photo.jpg", 2);
        Telechargement d2 =
                new Telechargement("video.mp4", 3);
        Telechargement d3 =
                new Telechargement("cours.pdf", 1);

        long debut = System.currentTimeMillis();

        d1.run();
        d2.run();
        d3.run();

        long duree = System.currentTimeMillis() - debut;

        System.out.println("Durée totale : " + duree + " ms");
    }
}
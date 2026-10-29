package Exercice7;

public class Course {
    public static void main(String[] args)
            throws InterruptedException {

        LigneDeDepart ligne = new LigneDeDepart(5);

        Classement classement = new Classement();

        String[] noms = {
                "Amina",
                "Bruno",
                "Chloé",
                "Driss",
                "Emma"
        };

        Coureur[] coureurs = new Coureur[noms.length];

        // TODO 10 : créer et démarrer les coureurs
        for (int i = 0; i < noms.length; i++) {

            coureurs[i] = new Coureur(
                    noms[i],
                    ligne,
                    classement
            );

            coureurs[i].start();
        }

        ligne.attendreTousPrets();

        System.out.println(
                "Arbitre : à vos marques... prêts..."
        );

        Thread.sleep(1000);

        ligne.donnerDepart();

        // TODO 11 : attendre la fin des coureurs
        for (int i = 0; i < coureurs.length; i++) {
            coureurs[i].join();
        }

        System.out.println("Arbitre : course terminée !");
    }
}

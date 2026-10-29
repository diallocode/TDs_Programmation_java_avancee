package Exercice7;

public class Coureur extends Thread {

    private final LigneDeDepart ligne;
    private final Classement classement;

    public Coureur(
            String nom,
            LigneDeDepart ligne,
            Classement classement) {

        super(nom);

        // TODO 5 : initialiser les attributs
        this.ligne = ligne;
        this.classement = classement;
    }

    private int hasard(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public void run() {
        try {

            System.out.println(getName() + " s'échauffe...");

            // TODO 6 : échauffement
            sleep(hasard(500, 2000));

            // TODO 7 : se déclarer prêt et attendre
            ligne.seDeclarerPret(getName());
            ligne.attendreDepart();

            System.out.println(
                    " " + getName() + " s'élance !"
            );

            // TODO 8 : durée de la course
            sleep(hasard(1000, 3000));

            // TODO 9 : franchir l'arrivée
            classement.arriver(getName());

        } catch (InterruptedException e) {
            System.out.println(getName() + " a abandonné");
        }
    }
}

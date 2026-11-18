package td6;

public class Profil extends Identite implements Presentable {
    public static final String CATEGORIE = "STI";
    protected int annee;
    private final String filiere;

    public Profil(String nom, String filiere, int annee) {
        super(nom);
        this.filiere = filiere;
        this.annee = anneeValide(annee);
    }

    @Override
    public String presentation() {
        return getNom() + " — " + filiere + " (" + annee + "A)";
    }

    protected void changerAnnee(int annee) {
        this.annee = anneeValide(annee);
    }

    private static int anneeValide(int annee) {
        if (annee < 1) {
            throw new IllegalArgumentException("L’année doit être positive.");
        }
        return annee;
    }
}

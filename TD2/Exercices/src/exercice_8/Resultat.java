package exercice_8;

public class Resultat {
    private int idRequete;
    private double valeurFinale;
    private String messageErreur;

    public Resultat(int idRequete, double valeurFinale) {
        this.idRequete = idRequete;
        this.valeurFinale = valeurFinale;
    }

    public Resultat(int idRequete, String messageErreur) {
        this.idRequete = idRequete;
        this.messageErreur = messageErreur;
    }

    @Override
    public String toString() {
        return messageErreur != null ? "Erreur: " + messageErreur : "Résultat: " + valeurFinale;
    }
}
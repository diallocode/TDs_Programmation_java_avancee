package td5;

import java.io.Serializable;

public final class Etudiant implements Serializable, Comparable<Etudiant> {
    private static final long serialVersionUID = 1L;
    private final String nom;
    private final double moyenne;
    private transient String motDePasse;

    public Etudiant(String nom, double moyenne) {
        this.nom = nom;
        this.moyenne = moyenne;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public double getMoyenne() {
        return moyenne;
    }

    @Override
    public int compareTo(Etudiant autre) {
        return Double.compare(moyenne, autre.moyenne);
    }

    @Override
    public String toString() {
        return nom + " : " + moyenne;
    }
}

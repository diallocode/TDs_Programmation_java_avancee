package td5;

import java.util.*;

public class TriDecroissant {
    private record Etudiant(String nom, double moyenne) implements Comparable<Etudiant> {
        @Override
        public int compareTo(Etudiant autre) {
            return Double.compare(autre.moyenne, moyenne);
        }
    }

    public static void main(String[] args) {
        List<Etudiant> liste = new ArrayList<>(List.of(
                new Etudiant("Lina", 14.5), new Etudiant("Noé", 12),
                new Etudiant("Maya", 17)));
        Collections.sort(liste);
        System.out.println("Ordre naturel décroissant : "
                + liste.stream().map(Etudiant::nom).toList());
    }
}

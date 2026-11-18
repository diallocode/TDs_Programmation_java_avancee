package td5;

import java.util.*;

public class Variance {
    public static double somme(List<? extends Number> liste) {
        double total = 0;
        for (Number element : liste) {
            total += element.doubleValue();
        }
        return total;
    }

    public static void remplir(List<? super Integer> liste) {
        for (int valeur = 1; valeur <= 5; valeur++) {
            liste.add(valeur);
        }
    }

    public static void main(String[] args) {
        List<Integer> entiers = new ArrayList<>(List.of(1, 2, 3));
        List<Double> reels = List.of(1.5, 2.5);
        List<Integer> destinationEntiers = new ArrayList<>();
        List<Object> destinationObjets = new ArrayList<>();
        remplir(destinationEntiers);
        remplir(destinationObjets);
        System.out.println("Somme Integer : " + somme(entiers));
        System.out.println("Somme Double  : " + somme(reels));
        System.out.println("Remplir Integer : " + destinationEntiers);
        System.out.println("Remplir Object  : " + destinationObjets);

        List<Etudiant> etudiants = new ArrayList<>(List.of(
                new Etudiant("Lina", 14.5), new Etudiant("Noé", 12),
                new Etudiant("Maya", 17)));
        System.out.println("Avant tri   : " + etudiants);
        Collections.sort(etudiants);
        System.out.println("Croissant   : " + etudiants);
        etudiants.sort((a, b) -> Double.compare(b.getMoyenne(), a.getMoyenne()));
        System.out.println("Décroissant : " + etudiants);
    }
}

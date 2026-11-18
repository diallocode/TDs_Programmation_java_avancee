package td5;

import java.io.*;
import java.nio.file.*;

public class Serialisation {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path fichier = Path.of("etudiant.ser");
        Etudiant lina = new Etudiant("Lina", 14.5);
        lina.setMotDePasse("valeur-fictive");
        System.out.println("Mot de passe initial renseigné : " + (lina.getMotDePasse() != null));
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fichier))) {
            out.writeObject(lina);
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(fichier))) {
            Etudiant lu = (Etudiant) in.readObject();
            System.out.println("Objet relu : " + lu);
            System.out.println("Mot de passe relu : " + lu.getMotDePasse());
            if (!lu.toString().equals(lina.toString()) || lu.getMotDePasse() != null) {
                throw new AssertionError("Sérialisation incorrecte");
            }
        }
    }
}

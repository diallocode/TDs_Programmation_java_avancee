package td5;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class GestionFichiers {
    public static void main(String[] args) throws IOException {
        Path source = Paths.get("courses.txt");
        Path cible = Paths.get("sauvegarde.txt");
        if (!Files.exists(source)) {
            System.out.println("courses.txt absent ou inaccessible");
            return;
        }
        System.out.println("Taille : " + Files.size(source) + " octets");
        System.out.println("Nombre de lignes : " + Files.readAllLines(source).size());
        Files.copy(source, cible, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copie identique : " + (Files.mismatch(source, cible) == -1));
        try (Stream<Path> fichiers = Files.list(Paths.get("."))) {
            fichiers.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .filter(nom -> nom.toString().endsWith(".txt"))
                    .sorted()
                    .forEach(System.out::println);
        }
        System.out.println("Sauvegarde supprimée : " + Files.deleteIfExists(cible));
    }
}

package td5;

import java.io.*;
import java.nio.file.*;
import java.util.Locale;

public class Courses {
    public static void main(String[] args) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(Path.of("courses.txt"));
             BufferedWriter bw = Files.newBufferedWriter(Path.of("courses_maj.txt"))) {
            int num = 1;
            String ligne;
            while ((ligne = br.readLine()) != null) {
                System.out.printf("%d - %s%n", num++, ligne);
                bw.write(ligne.toUpperCase(Locale.ROOT));
                bw.newLine();
            }
        }
    }
}

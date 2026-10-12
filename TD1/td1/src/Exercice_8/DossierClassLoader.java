import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DossierClassLoader extends ClassLoader {
    private String cheminDossier;

    public DossierClassLoader(String cheminDossier, ClassLoader parent) {
        super(parent);
        this.cheminDossier = cheminDossier;
    }

    @Override
    protected Class<?> findClass(String nom) throws ClassNotFoundException {
        try {
            // Transformation du nom de la classe en chemin de fichier (ex: plugins.Cible -> plugins/Cible.class)
            String fichier = cheminDossier + File.separator + nom.replace('.', File.separatorChar) + ".class";
            File f = new File(fichier);
            
            if (!f.exists()) {
                throw new ClassNotFoundException("Fichier non trouve : " + fichier);
            }

            // Lecture des octets du fichier .class
            FileInputStream input = new FileInputStream(f);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int donnees;
            while ((donnees = input.read()) != -1) {
                buffer.write(donnees);
            }
            input.close();

            byte[] octets = buffer.toByteArray();
            // Définition de la classe dans la JVM
            return defineClass(nom, octets, 0, octets.length);

        } catch (IOException e) {
            throw new ClassNotFoundException("Erreur de lecture pour la classe " + nom, e);
        }
    }
}
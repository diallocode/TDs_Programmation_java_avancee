package td6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LanceurPlugin {
    private LanceurPlugin() {}

    public static void chargerEtExecuter(Path fichier)
            throws IOException, ReflectiveOperationException {
        String nom = Files.readString(fichier).strip();
        ClassLoader chargeur = LanceurPlugin.class.getClassLoader();
        Class<? extends Plugin> type = Class.forName(nom, false, chargeur)
                .asSubclass(Plugin.class);
        Plugin plugin = type.getConstructor().newInstance();
        Plugin.class.getMethod("executer").invoke(plugin);
    }

    public static void main(String[] args) throws IOException, ReflectiveOperationException {
        chargerEtExecuter(Path.of("plugin.txt"));
    }
}

package td6;

import java.util.Arrays;

public final class InspecteurClasse {
    private InspecteurClasse() {}

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage : InspecteurClasse <nom complet de classe>");
        }
        ClassLoader chargeur = InspecteurClasse.class.getClassLoader();
        Class<?> classe = Class.forName(args[0], false, chargeur);
        System.out.println("Classe : " + classe.getName());
        System.out.println("Champs déclarés");
        Arrays.stream(classe.getDeclaredFields())
                .map(Signatures::champ)
                .sorted()
                .forEach(champ -> System.out.println("  " + champ));
        System.out.println("Méthodes déclarées");
        Arrays.stream(classe.getDeclaredMethods())
                .map(Signatures::methode)
                .sorted()
                .forEach(methode -> System.out.println("  " + methode));
    }
}

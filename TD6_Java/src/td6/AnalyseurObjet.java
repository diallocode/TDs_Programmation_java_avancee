package td6;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnalyseurObjet {
    private AnalyseurObjet() {}

    public static void afficherInfos(Object objet) {
        Class<?> classe = Objects.requireNonNull(objet, "Objet à analyser").getClass();
        Class<?> parent = classe.getSuperclass();
        String interfaces = Arrays.stream(classe.getInterfaces())
                .map(Class::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Classe : " + classe.getName());
        System.out.println("Superclasse : " + (parent == null ? "aucune" : parent.getName()));
        System.out.println("Interfaces directes : " + (interfaces.isEmpty() ? "aucune" : interfaces));

        Set<Class<?>> types = new LinkedHashSet<>();
        releverTypes(classe, types);
        for (Class<?> type : types) {
            afficherDeclarations(type);
        }
    }

    private static void releverTypes(Class<?> type, Set<Class<?>> types) {
        if (type == null || !types.add(type)) {
            return;
        }
        for (Class<?> contrat : type.getInterfaces()) {
            releverTypes(contrat, types);
        }
        releverTypes(type.getSuperclass(), types);
    }

    private static void afficherDeclarations(Class<?> type) {
        System.out.println("Déclarations de " + type.getName());
        Arrays.stream(type.getDeclaredFields())
                .map(Signatures::champ)
                .sorted()
                .forEach(champ -> System.out.println("  Champ : " + champ));
        Arrays.stream(type.getDeclaredConstructors())
                .map(Signatures::constructeur)
                .sorted()
                .forEach(constructeur -> System.out.println("  Constructeur : " + constructeur));
        Arrays.stream(type.getDeclaredMethods())
                .map(Signatures::methode)
                .sorted()
                .forEach(methode -> System.out.println("  Méthode : " + methode));
    }

    public static void main(String[] args) {
        afficherInfos("Bonjour");
        System.out.println();
        afficherInfos(new Date(0));
        System.out.println();
        afficherInfos(new Profil("Lina", "Sécurité informatique", 4));
    }
}

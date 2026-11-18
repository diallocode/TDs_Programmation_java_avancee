package td6;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public final class ConstructeurDynamique {
    private static final Map<Class<?>, Class<?>> TYPES_OBJETS = Map.of(
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            char.class, Character.class);

    private static final Map<Class<?>, Function<String, ?>> CONVERTISSEURS = Map.of(
            Byte.class, texte -> Byte.valueOf(texte.strip()),
            Short.class, texte -> Short.valueOf(texte.strip()),
            Integer.class, texte -> Integer.valueOf(texte.strip()),
            Long.class, texte -> Long.valueOf(texte.strip()),
            Float.class, texte -> Float.valueOf(texte.strip()),
            Double.class, texte -> Double.valueOf(texte.strip()),
            Boolean.class, ConstructeurDynamique::lireBooleen,
            Character.class, ConstructeurDynamique::lireCaractere,
            String.class, Function.identity());

    private record Candidat(Constructor<?> constructeur, Object[] arguments) {}

    private ConstructeurDynamique() {}

    public static Object creerObjet(Class<?> classe, List<String> valeurs)
            throws ReflectiveOperationException {
        List<Candidat> compatibles = new ArrayList<>();
        for (Constructor<?> constructeur : classe.getConstructors()) {
            if (constructeur.getParameterCount() != valeurs.size()) {
                continue;
            }
            try {
                Object[] arguments = convertir(constructeur.getParameterTypes(), valeurs);
                compatibles.add(new Candidat(constructeur, arguments));
            } catch (IllegalArgumentException incompatible) {
                // Cette signature ne permet pas de convertir les valeurs saisies.
            }
        }
        if (compatibles.isEmpty()) {
            throw new IllegalArgumentException("Aucun constructeur public compatible pour "
                    + classe.getName() + " avec " + valeurs.size() + " valeur(s).");
        }
        if (compatibles.size() > 1) {
            List<String> signatures = compatibles.stream()
                    .map(Candidat::constructeur)
                    .map(Signatures::nomConstructeur)
                    .sorted()
                    .toList();
            throw new IllegalArgumentException("Plusieurs constructeurs compatibles : "
                    + String.join(" ou ", signatures));
        }
        Candidat choisi = compatibles.getFirst();
        Object objet = choisi.constructeur().newInstance(choisi.arguments());
        System.out.println("Constructeur retenu : " + Signatures.nomConstructeur(choisi.constructeur()));
        return objet;
    }

    private static Object[] convertir(Class<?>[] types, List<String> valeurs) {
        Object[] arguments = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> typeObjet = TYPES_OBJETS.getOrDefault(types[i], types[i]);
            Function<String, ?> convertisseur = CONVERTISSEURS.get(typeObjet);
            if (convertisseur == null) {
                throw new IllegalArgumentException("Type non pris en charge : " + types[i]);
            }
            arguments[i] = convertisseur.apply(valeurs.get(i));
        }
        return arguments;
    }

    private static Boolean lireBooleen(String texte) {
        return switch (texte.strip().toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalArgumentException("Booléen attendu : true ou false.");
        };
    }

    private static Character lireCaractere(String texte) {
        if (texte.length() != 1) {
            throw new IllegalArgumentException("Une seule unité UTF-16 est attendue pour char.");
        }
        return texte.charAt(0);
    }

    private static void afficherChamps(Object objet) throws IllegalAccessException {
        for (Class<?> type = objet.getClass(); type != null; type = type.getSuperclass()) {
            for (Field champ : ChampsInstance.declaresPar(type)) {
                System.out.printf("%s.%s = %s%n", type.getSimpleName(), champ.getName(),
                        ChampsInstance.lire(champ, objet));
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner entree = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Nom complet de la classe :");
            String nom = entree.nextLine().strip();
            System.out.println("Nombre de valeurs :");
            int nombre = Integer.parseInt(entree.nextLine().strip());
            if (nombre < 0) {
                throw new IllegalArgumentException("Le nombre de valeurs doit être positif ou nul.");
            }
            List<String> valeurs = new ArrayList<>();
            for (int i = 0; i < nombre; i++) {
                System.out.println("Valeur " + (i + 1) + " :");
                valeurs.add(entree.nextLine());
            }
            Class<?> classe = Class.forName(nom, false, ConstructeurDynamique.class.getClassLoader());
            afficherChamps(creerObjet(classe, valeurs));
        } catch (InvocationTargetException erreur) {
            System.err.println("Le constructeur a levé : " + erreur.getCause());
            System.exit(1);
        } catch (ReflectiveOperationException | IllegalArgumentException | NoSuchElementException erreur) {
            System.err.println("Construction impossible : " + erreur.getMessage());
            System.exit(1);
        }
    }
}

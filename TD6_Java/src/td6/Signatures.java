package td6;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

/** Formate les métadonnées communes aux exercices 1, 4 et 5. */
final class Signatures {
    private Signatures() {}

    static String champ(Field champ) {
        return modificateurs(champ.getModifiers()) + " "
                + champ.getType().getTypeName() + " " + champ.getName();
    }

    static String methode(Method methode) {
        int bits = methode.getModifiers() & Modifier.methodModifiers();
        String modificateurs = modificateurs(bits);
        if (methode.isDefault()) {
            modificateurs += " default";
        }
        return modificateurs + " " + methode.getReturnType().getTypeName()
                + " " + methode.getName() + "(" + parametres(methode) + ")";
    }

    static String constructeur(Constructor<?> constructeur) {
        return modificateurs(constructeur.getModifiers() & Modifier.constructorModifiers())
                + " " + nomConstructeur(constructeur);
    }

    static String nomConstructeur(Constructor<?> constructeur) {
        return constructeur.getDeclaringClass().getName()
                + "(" + parametres(constructeur) + ")";
    }

    private static String parametres(Executable membre) {
        return Arrays.stream(membre.getParameterTypes())
                .map(Class::getTypeName)
                .collect(Collectors.joining(", "));
    }

    private static String modificateurs(int bits) {
        String mots = Modifier.toString(bits);
        if (Modifier.isPublic(bits) || Modifier.isProtected(bits) || Modifier.isPrivate(bits)) {
            return mots;
        }
        return mots.isEmpty() ? "(package)" : "(package) " + mots;
    }
}

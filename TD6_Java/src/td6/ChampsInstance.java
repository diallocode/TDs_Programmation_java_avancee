package td6;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

final class ChampsInstance {
    private ChampsInstance() {}

    static List<Field> declaresPar(Class<?> classe) {
        return Arrays.stream(classe.getDeclaredFields())
                .filter(champ -> !Modifier.isStatic(champ.getModifiers()) && !champ.isSynthetic())
                .sorted(Comparator.comparing(Field::getName))
                .toList();
    }

    static Object lire(Field champ, Object objet) throws IllegalAccessException {
        if (!champ.canAccess(objet) && !champ.trySetAccessible()) {
            throw new IllegalAccessException("Champ inaccessible : " + champ);
        }
        return champ.get(objet);
    }
}

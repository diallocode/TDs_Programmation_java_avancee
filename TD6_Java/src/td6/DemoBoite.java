package td6;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class DemoBoite {
    private static final Boite<Integer> boite = new Boite<>();

    private DemoBoite() {}

    public static void main(String[] args) throws ReflectiveOperationException {
        Field champ = DemoBoite.class.getDeclaredField("boite");
        ParameterizedType declaration = (ParameterizedType) champ.getGenericType();
        Type argument = declaration.getActualTypeArguments()[0];
        Object instance = champ.get(null);
        Class<?> classe = instance.getClass();

        Method setter = classe.getMethod("setValeur", Object.class);
        Method getter = classe.getMethod("getValeur");
        setter.invoke(instance, 100);
        Object valeur = getter.invoke(instance);

        System.out.println("Type déclaré du champ : " + declaration.getTypeName());
        System.out.println("Argument déclaré : " + argument.getTypeName());
        System.out.println("Classe de l’instance : " + classe.getName());
        System.out.println("Paramètre de setValeur : " + setter.getParameterTypes()[0].getTypeName());
        System.out.println("Type de retour de getValeur : " + getter.getReturnType().getTypeName());
        System.out.println("Valeur relue : " + valeur + " (" + valeur.getClass().getName() + ")");
    }
}

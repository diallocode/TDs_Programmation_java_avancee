package td6;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

/** Sérialisation des champs d'instance déclarés, limitée aux valeurs scalaires. */
public final class JsonReflexif {
    private JsonReflexif() {}

    public static String toJSON(Object objet) throws IllegalAccessException {
        Class<?> classe = Objects.requireNonNull(objet, "Objet à sérialiser").getClass();
        StringJoiner membres = new StringJoiner(",", "{", "}");
        for (Field champ : ChampsInstance.declaresPar(classe)) {
            Object valeur = ChampsInstance.lire(champ, objet);
            membres.add(citer(champ.getName()) + ":" + valeurJson(valeur));
        }
        return membres.toString();
    }

    private static String valeurJson(Object valeur) {
        return switch (valeur) {
            case null -> "null";
            case String texte -> citer(texte);
            case Character caractere -> citer(caractere.toString());
            case Boolean bool -> bool.toString();
            case Byte entier -> entier.toString();
            case Short entier -> entier.toString();
            case Integer entier -> entier.toString();
            case Long entier -> entier.toString();
            case Float reel when Float.isFinite(reel) -> reel.toString();
            case Double reel when Double.isFinite(reel) -> reel.toString();
            default -> throw new IllegalArgumentException("Valeur JSON non prise en charge : "
                    + valeur.getClass().getTypeName());
        };
    }

    private static String citer(String texte) {
        StringBuilder sortie = new StringBuilder("\"");
        for (int i = 0; i < texte.length(); i++) {
            char caractere = texte.charAt(i);
            switch (caractere) {
                case '"' -> sortie.append("\\\"");
                case '\\' -> sortie.append("\\\\");
                case '\b' -> sortie.append("\\b");
                case '\f' -> sortie.append("\\f");
                case '\n' -> sortie.append("\\n");
                case '\r' -> sortie.append("\\r");
                case '\t' -> sortie.append("\\t");
                default -> {
                    if (caractere < 0x20) {
                        sortie.append(String.format(Locale.ROOT, "\\u%04x", (int) caractere));
                    } else {
                        sortie.append(caractere);
                    }
                }
            }
        }
        return sortie.append('"').toString();
    }

    public static void main(String[] args) throws IllegalAccessException {
        Employe[] exemples = {
            new Employe("Dupont", "Ingénieur", 3500),
            new Employe("Ndiaye", "Analyste SOC", 4100),
            new Employe("Maya \"R&D\"", "Réseau\\Lab\nN2", 4200),
            new Employe("Lina", null, 3200)
        };
        for (Employe employe : exemples) {
            System.out.println(toJSON(employe));
        }
    }
}


public class Main {

    // Méthode utilitaire pour afficher la hiérarchie des ClassLoaders
    public static void afficherHierarchy(Class<?> c, String nom) {
        System.out.println("--- Hiérarchie des ClassLoaders pour : " + nom + " ---");
        ClassLoader cl = c.getClassLoader();
        while (cl != null) {
            System.out.println("  -> " + cl);
            cl = cl.getParent();
        }
        System.out.println("  -> null (Bootstrap ClassLoader)\n");
    }

    public static void main(String[] args) {
        try {
            // ==========================================
            // QUESTION 1 : Hiérarchie des ClassLoaders
            // ==========================================
            afficherHierarchy(Main.class, "Main.class");
            afficherHierarchy(String.class, "String.class");
            afficherHierarchy(Connection.class, "java.sql.Connection.class");

            /*
             * Explication des résultats (Q1) :
             * - Main.class est chargée par l'Application ClassLoader (AppClassLoader). Sur les JDK modernes, son parent est le Platform ClassLoader, puis Bootstrap (null).
             * - String.class fait partie du noyau de Java (Core API). Elle est chargée par le Bootstrap ClassLoader, ce qui s'affiche directement par 'null'.
             * - Sur les JDK modernes, java.sql.Connection.class est chargée par le Platform ClassLoader.
             */

            // ==========================================
            // QUESTION 2 & 3 : Deux instances de DossierClassLoader
            // ==========================================
            DossierClassLoader loader1 = new DossierClassLoader("plugins", ClassLoader.getSystemClassLoader());
            DossierClassLoader loader2 = new DossierClassLoader("plugins", ClassLoader.getSystemClassLoader());

            Class<?> classe1 = loader1.loadClass("plugins.Cible");
            Class<?> classe2 = loader2.loadClass("plugins.Cible");

            // Les deux objets Class obtenus sont-ils égaux ?
            boolean classesEgales = (classe1 == classe2);
            System.out.println("Q3 - Les deux objets Class sont-ils égaux ? " + classesEgales);
            // Réponse : Non (false). Bien qu'elles proviennent du même fichier physique, deux instances de ClassLoaders distinctes chargent deux espaces de noms de classes séparés en mémoire.

            // ==========================================
            // QUESTION 4 : Tentative de cast entre chargeurs
            // ==========================================
            Object instance1 = classe1.getDeclaredConstructor().newInstance();
            
            try {
                // Tentative de caster l'objet de loader1 via la classe obtenue par loader2
                Object instanceCaste = classe2.cast(instance1);
                System.out.println("Q4 - Cast réussi : " + instanceCaste);
            } catch (ClassCastException e) {
                System.out.println("Q4 - Échec du cast (ClassCastException) : " + e.getMessage());
            }
            /*
             * Pourquoi ? En Java, deux classes chargées par deux ClassLoaders différents ne sont pas considérées comme compatibles, 
             * même si elles portent le même nom canonique et proviennent du même fichier source. Le type est lié au chargeur.
             */

            // ==========================================
            // QUESTION 5 : Cible.class placée dans le classpath
            // ==========================================
            /*
             * Si Cible.class est copiée dans le classpath principal de l'application :
             * - Avec la délégation parent-d'abord, chaque DossierClassLoader transmet la demande au parent. Si le System ClassLoader
             *   trouve plugins.Cible dans le classpath, il charge la classe et la renvoie aux deux chargeurs.
             * - Les deux références désignent alors la même classe, définie par le System ClassLoader. Le DossierClassLoader est
             *   tout de même sollicité, mais ne définit pas lui-même cette classe.
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
# TD 6 — Réflexivité en Java

Correction des sept exercices avec le JDK 21. Les classes se trouvent dans le
package `td6`. Le projet utilise uniquement la bibliothèque standard Java.

## Compiler et lancer les exercices

Depuis le dossier `TD6_Java`, avec un terminal UTF-8 :

```sh
mkdir -p classes
javac -encoding UTF-8 -Xlint:all -Werror -d classes src/td6/*.java

java -cp classes td6.AnalyseurObjet
java -cp classes td6.JsonReflexif
java -cp classes td6.LanceurPlugin
printf '%s\n' td6.Point 2 7 -3 | java -cp classes td6.ConstructeurDynamique
java -cp classes td6.InspecteurClasse td6.Profil
java -cp classes td6.DemoBoite
printf '%s\n' javax.crypto.Cipher AES 'Réflexivité en Java' | java -cp classes td6.ChiffrementDynamique
printf '%s\n' javax.crypto.Cipher DES 'Réflexivité en Java' | java -cp classes td6.ChiffrementDynamique
```

| Exercice | Entrée / résultat attendu |
|---:|---|
| 1 | Inventaire de `String`, `Date` et `Profil`, regroupé par type de déclaration. |
| 2 | Quatre objets JSON, dont un avec des caractères échappés et un avec `null`. |
| 3 | `plugin.txt` contient par défaut `td6.PluginA`. Écrire `td6.PluginB` à la place pour lancer B avec la même commande. |
| 4 | Saisir le nom complet de la classe, le nombre de paramètres puis une valeur par ligne. `Point(7, -3)` doit afficher `Point.x = 7` et `Point.y = -3`. |
| 5 | Passer le nom complet de la classe en argument. `td6.Profil` montre notamment `private final`, `protected` et `public static final`. |
| 6 | L’argument de type déclaré est `Integer`. L’invocation dynamique relit `100`. |
| 7 | Saisir `javax.crypto.Cipher`, `AES` ou `DES`, puis une ligne de texte. Le déchiffrement restitue la ligne en UTF-8. |

Pour ajouter un plugin au TD, compiler une classe de confiance qui implémente
`td6.Plugin` et fournit un constructeur public sans argument. Mettre cette
classe sur le classpath et inscrire son nom complet dans `plugin.txt`.

La construction interactive sélectionne une signature publique de la bonne
arité dont tous les paramètres acceptent les valeurs saisies. Elle prend en
charge `String`, les primitifs et leurs enveloppes. Deux signatures
compatibles produisent un message d’ambiguïté.

La démo de cryptographie génère une nouvelle clé et un nouvel IV par exécution.
Elle emploie `AES/GCM/NoPadding` ou `DES/CBC/PKCS5Padding`. DES est inclus
pour l’exercice pédagogique.

## Reproduire la validation

```sh
python3 verifier.py
```

Le script utilise un répertoire temporaire et conserve les résultats dans
`resultats/`. Les douze scénarios couvrent les sept exercices, dont trois
rejets attendus. Il vérifie la structure JSON avec le parseur de la
bibliothèque standard Python et compare le texte clair au texte déchiffré.
Les traces de chiffrement changent à chaque exécution.

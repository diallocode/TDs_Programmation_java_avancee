# TD 5 — Entrées/sorties, ORM et variance

Projet pédagogique pour JDK 21. Les exemples sont indépendants, à l'exception de
`Courses` et `GestionFichiers`, qui utilisent tous deux `courses.txt`.

| Exercice | Point d'entrée |
|---|---|
| 1 | `td5.Courses` |
| 2 | `td5.GestionFichiers` |
| 3 | `td5.Serialisation` |
| 4 | `td5.DemoJpa` |
| 5 | `td5.Variance`, `td5.TriDecroissant` et cas de compilation dans `verifier.py` |

Avec Python 3 et Java 21, lancer `python3 verifier.py` à la racine pour vérifier
les exercices 1, 2, 3 et 5 dans un répertoire temporaire. Les résultats sont
réécrits dans `resultats/`. Le script exerce aussi l'échec de lecture lorsque
`courses.txt` est absent.

Pour exécuter la démonstration JPA avec Hibernate et H2 en mémoire :

```sh
mvn compile exec:java -Dexec.mainClass=td5.DemoJpa
```

Maven doit pouvoir récupérer les dépendances de `pom.xml`. L'unité de
persistance `td5` se trouve dans `src/main/resources/META-INF/persistence.xml`.
La création et la suppression automatiques du schéma s'appliquent uniquement
à la base H2 de démonstration en mémoire. L'exercice 4.5 décrit la variante de
mapping avec une entité Auteur, distincte du mapping texte utilisé dans le test.

Les exemples de fichiers s'exécutent dans un répertoire où se trouve
`courses.txt`. Utiliser une copie de `data/courses.txt` pour les essais manuels.
Le programme NIO remplace puis supprime `sauvegarde.txt` dans ce répertoire.
La sérialisation utilise exclusivement le fichier généré par son propre programme.

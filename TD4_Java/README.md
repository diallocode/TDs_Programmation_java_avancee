# TD4 - Programmation concurrente avancée

Java 21, sans dépendance externe. Chaque exercice est indépendant.
Les exercices `Ex10` et `Ex11` correspondent aux deux derniers exercices
numérotés à nouveau 6 et 7 dans le PDF.

## Validation automatique
`python3 verifier.py` compile les 12 sources et exécute les 18 scénarios en
contrôlant leurs sorties. Les journaux sont réécrits dans `resultats/` ; les durées
et l'ordre des employés peuvent changer par rapport au rapport. Cette validation
prend environ 45 secondes. Elle n'exécute pas le parking en temps réel ni le mode
de deadlock persistant. Les captures du rapport correspondent à la validation
conservée lors de sa rédaction.

## Compilation
```sh
mkdir -p classes
javac -encoding UTF-8 -d classes src/td4/*.java
```
Si seul le lanceur `java` est dans le PATH mais que le module compilateur est présent :
```sh
java com.sun.tools.javac.Main -encoding UTF-8 -d classes src/td4/*.java
```

## Exécutions
```sh
java -cp classes td4.Ex01
java -cp classes td4.Ex01Sujet     # algorithme du sémaphore fourni, remis en forme
java -cp classes td4.Ex02          # délais accélérés : unité = 1 ms
java -cp classes td4.Ex02 --reel   # délais du sujet : unité = 1 s ; plusieurs minutes
java -cp classes td4.Ex03
java -cp classes td4.Ex04
java -cp classes td4.Ex04 --deadlock # démonstration isolée, threads daemon
java -cp classes td4.Ex05
java -cp classes td4.Ex05 --deadlock # démonstration isolée, threads daemon
java -cp classes td4.Ex06 original
java -cp classes td4.Ex06
java -cp classes td4.Ex06 calcul
java -cp classes td4.Ex06 executor
java -cp classes td4.Ex07
java -cp classes td4.Ex07 timeout
java -cp classes td4.Ex08
java -cp classes td4.Ex08 --original # risque de blocage ; arrêter avec Ctrl+C au besoin
java -cp classes td4.Ex09
java -cp classes td4.Ex10
java -cp classes td4.Ex11 --fixture # météo reproductible + erreur/timeout contrôlés
java -cp classes td4.Ex11           # météo aléatoire du sujet
```

## Diagnostic jstack (dans un processus séparé)
```sh
java -cp classes td4.Ex08 --deadlock
# autre terminal : jps puis jstack <pid>
# terminer ensuite explicitement le processus de démonstration (Ctrl+C).
```
Le mode `Ex08 --deadlock` bloque volontairement. Ne pas le lancer dans la suite normale.
Dans l'environnement de validation, les lanceurs jps/jstack ne sont pas dans le PATH ;
les mêmes outils du JDK sont invoqués via leurs classes :
`java --module jdk.jcmd/sun.tools.jps.Jps` et
`java --module jdk.jcmd/sun.tools.jstack.JStack <pid>`.
L'attachement à la JVM est indisponible dans cet environnement isolé (socket
`.java_pid` inaccessible). Le diagnostic réel fourni a été obtenu par SIGQUIT :
`kill -QUIT <pid>`. La JVM détecte et imprime la même attente circulaire.

## Choix et limites
- Ex01 répare le sémaphore pédagogique du PDF : compteur de permis non négatif,
  attente avant décrémentation, interruption propagée.
- Ex02 offre les délais du sujet et un mode accéléré explicitement annoncé.
- Ex03 utilise des identifiants uniques ; toute entrée/sortie utilise les mêmes verrous.
  Un montant est un entier (à interpréter comme centimes pour un vrai modèle monétaire).
- Ex08 suit les transferts du sujet : pas de rejet des stocks négatifs transitoires.
  Ajouter cette règle métier changerait le scénario ; les accès d'une variante ne doivent
  jamais être mélangés avec ceux de l'autre.
- Les démonstrations d'interblocage utilisent un rendez-vous après le premier verrou
  pour rendre le diagnostic reproductible, sans dépendre d'un sleep hasardeux.
- Les micro-mesures sont indicatives : médiane, chauffe et données constantes ; pas JMH.
- Les délais asynchrones bornent l'attente logique de la météo, sans tuer sa tâche.

`resultats/` contient les sorties réelles de la validation. Le rapport ne reproduit
que les extraits de code nécessaires ; les sources ci-dessus sont complètes.

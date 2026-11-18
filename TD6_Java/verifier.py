#!/usr/bin/env python3
"""Compile le TD et vérifie ses points d'entrée à partir de leurs vraies sorties."""
import base64
import json
from pathlib import Path
import shlex
import shutil
import subprocess
import tempfile

RACINE = Path(__file__).resolve().parent
RESULTATS = RACINE / "resultats"
SOURCES = sorted((RACINE / "src" / "td6").glob("*.java"))


def verifier(condition, message):
    if not condition:
        raise AssertionError(message)


def scenario(dossier, nom, programme, *, arguments=(), saisie=(), plugin=None, erreur=None):
    if plugin is not None:
        (dossier / "plugin.txt").write_text(plugin + "\n", encoding="utf-8")
    commande = ["java", "-cp", "classes", f"td6.{programme}", *arguments]
    sortie = subprocess.run(
        commande, cwd=dossier, capture_output=True, encoding="utf-8",
        input="".join(ligne + "\n" for ligne in saisie), timeout=15
    )
    trace = [f"$ {shlex.join(commande)}"]
    if plugin is not None:
        trace.append("plugin.txt = " + plugin)
    if saisie:
        trace.append("Entrée standard (une valeur par ligne) : " + " | ".join(saisie))
    trace.append(sortie.stdout.rstrip())
    if sortie.stderr:
        trace.append(sortie.stderr.rstrip())
    (RESULTATS / f"{nom}.txt").write_text("\n".join(trace) + "\n", encoding="utf-8")

    if erreur is None:
        verifier(sortie.returncode == 0 and not sortie.stderr, "\n".join(trace))
    else:
        verifier(sortie.returncode != 0 and erreur in sortie.stderr, "\n".join(trace))
    return sortie.stdout


def verifier_exercices(dossier):
    analyse = scenario(dossier, "01_analyse_objet", "AnalyseurObjet")
    for attendu in (
        "Classe : java.lang.String", "Classe : java.util.Date", "Classe : td6.Profil",
        "Superclasse : td6.Identite", "Interfaces directes : td6.Presentable",
        "Champ : private final java.lang.String filiere", "Champ : protected int annee",
        "Constructeur : public java.util.Date(long)", "Méthode : public long getTime()",
        "Méthode : public java.lang.String substring(int, int)",
        "Déclarations de td6.Identite\n  Champ : private final java.lang.String nom",
    ):
        verifier(attendu in analyse, f"Analyse incomplète : {attendu}")

    sortie = scenario(dossier, "02_json", "JsonReflexif")
    objets = [json.loads(ligne) for ligne in sortie.splitlines()]
    verifier(objets == [
        {"nom": "Dupont", "poste": "Ingénieur", "salaire": 3500},
        {"nom": "Ndiaye", "poste": "Analyste SOC", "salaire": 4100},
        {"nom": 'Maya "R&D"', "poste": "Réseau\\Lab\nN2", "salaire": 4200},
        {"nom": "Lina", "poste": None, "salaire": 3200},
    ], "Le JSON doit restituer les chaînes, les nombres et null.")
    verifier(sortie.splitlines()[0] == '{"nom":"Dupont","poste":"Ingénieur","salaire":3500}',
             "Le premier JSON doit suivre la présentation de l’énoncé.")

    for lettre in ("A", "B"):
        sortie = scenario(dossier, f"03_plugin_{lettre.lower()}", "LanceurPlugin",
                          plugin=f"td6.Plugin{lettre}")
        verifier(sortie.strip() == f"Plugin {lettre} exécuté.", "Mauvais plugin exécuté.")
    scenario(dossier, "03_plugin_invalide", "LanceurPlugin",
             plugin="java.lang.String", erreur="ClassCastException")

    point = scenario(dossier, "04_point", "ConstructeurDynamique", saisie=("td6.Point", "2", "7", "-3"))
    for attendu in ("Constructeur retenu : td6.Point(int, int)", "Point.x = 7", "Point.y = -3"):
        verifier(attendu in point, f"Point incorrect : {attendu}")
    scenario(dossier, "04_point_incompatible", "ConstructeurDynamique",
             saisie=("td6.Point", "2", "abc", "-3"), erreur="Aucun constructeur public compatible")
    scenario(dossier, "04_constructeurs_ambigus", "ConstructeurDynamique",
             saisie=("java.lang.Integer", "1", "12"), erreur="Plusieurs constructeurs compatibles")

    mods = scenario(dossier, "05_modificateurs", "InspecteurClasse", arguments=("td6.Profil",))
    for attendu in ("public static final java.lang.String CATEGORIE", "protected int annee",
                    "private final java.lang.String filiere", "public java.lang.String presentation()",
                    "private static int anneeValide(int)", "protected void changerAnnee(int)"):
        verifier(attendu in mods, f"Modificateurs incomplets : {attendu}")

    boite = scenario(dossier, "06_boite", "DemoBoite")
    for attendu in ("td6.Boite<java.lang.Integer>", "Argument déclaré : java.lang.Integer",
                    "Paramètre de setValeur : java.lang.Object",
                    "Type de retour de getValeur : java.lang.Object",
                    "Valeur relue : 100 (java.lang.Integer)"):
        verifier(attendu in boite, f"Réflexion générique incorrecte : {attendu}")

    original = "Réflexivité en Java"
    taille_texte = len(original.encode("utf-8"))
    for algo, transformation, cle, iv, taille_chiffre in (
        ("AES", "AES/GCM/NoPadding", 16, 12, taille_texte + 16),
        ("DES", "DES/CBC/PKCS5Padding", 8, 8, 8 * (taille_texte // 8 + 1)),
    ):
        sortie = scenario(dossier, f"07_{algo.lower()}", "ChiffrementDynamique",
                          saisie=("javax.crypto.Cipher", algo, original))
        champs = dict(ligne.split(" : ", 1) for ligne in sortie.splitlines() if " : " in ligne)
        verifier(champs["Transformation"] == transformation, f"Transformation {algo} incorrecte.")
        verifier(champs["Taille du matériau de clé"] == f"{cle} octets", "Taille de clé incorrecte.")
        verifier(champs["Texte clair"] == champs["Texte déchiffré"] == original,
                 f"Le texte {algo} n’a pas été restauré.")
        verifier(champs["Identique à l’original"] == "true", "Comparaison du texte incorrecte.")
        verifier(len(base64.b64decode(champs["IV (Base64)"], validate=True)) == iv,
                 "Taille de l’IV incorrecte.")
        verifier(len(base64.b64decode(champs["Texte chiffré (Base64)"], validate=True)) == taille_chiffre,
                 "Taille du texte chiffré incorrecte.")


def main():
    RESULTATS.mkdir(exist_ok=True)
    binaire_javac = shutil.which("javac")
    compilateur = [binaire_javac] if binaire_javac else ["java", "com.sun.tools.javac.Main"]
    version = subprocess.run(["java", "-version"], capture_output=True, encoding="utf-8", check=True)
    with tempfile.TemporaryDirectory(prefix="td6-java-") as temporaire:
        dossier = Path(temporaire)
        (dossier / "classes").mkdir()
        subprocess.run(
            [*compilateur, "-encoding", "UTF-8", "-Xlint:all", "-Werror", "-d", "classes",
             *map(str, SOURCES)],
            cwd=dossier, timeout=30, check=True
        )
        verifier_exercices(dossier)
    bilan = (
        version.stderr
        + f"{len(SOURCES)} fichiers Java compilés avec -Xlint:all -Werror.\n"
        + "12 scénarios vérifiés, dont 3 rejets attendus.\n"
    )
    (RESULTATS / "validation.txt").write_text(bilan, encoding="utf-8")
    print(bilan, end="")


if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""Vérification des exercices hors JPA avec un JDK 21 et Python 3."""
from pathlib import Path
import shutil
import subprocess
import tempfile

root = Path(__file__).resolve().parent
sources = root / "src/main/java/td5"
journaux = root / "resultats"
journaux.mkdir(exist_ok=True)
javac = [shutil.which("javac")] if shutil.which("javac") else ["java", "com.sun.tools.javac.Main"]
with tempfile.TemporaryDirectory() as dossier:
    travail = Path(dossier)
    classes = travail / "classes"
    classes.mkdir()
    textes = ["Courses", "GestionFichiers", "Etudiant", "Serialisation", "Variance", "TriDecroissant"]
    subprocess.run([*javac, "-encoding", "UTF-8", "-Xlint:all", "-d", str(classes),
                    *[str(sources / (nom + ".java")) for nom in textes]], check=True)

    def lancer(nom, trace=None, code=0):
        commande = ["java", "-cp", str(classes), "td5." + nom]
        resultat = subprocess.run(commande, cwd=travail, capture_output=True, text=True)
        assert resultat.returncode == code, resultat.stderr
        (journaux / ((trace or nom) + ".txt")).write_text(
            "$ java -cp classes td5." + nom + "\n" + resultat.stdout + resultat.stderr,
            encoding="UTF-8")
        return resultat.stdout + resultat.stderr

    assert "NoSuchFileException: courses.txt" in lancer("Courses", "CoursesAbsentes", code=1)
    assert not (travail / "courses_maj.txt").exists()
    shutil.copy(root / "data/courses.txt", travail)
    assert lancer("Courses").splitlines() == ["1 - pain", "2 - lait", "3 - oeufs", "4 - pommes"]
    assert (travail / "courses_maj.txt").read_text().splitlines() == ["PAIN", "LAIT", "OEUFS", "POMMES"]
    shutil.copy(travail / "courses_maj.txt", journaux / "CoursesMaj.txt")
    (travail / "sauvegarde.txt").write_text("Ancienne sauvegarde")
    assert "Copie identique : true" in lancer("GestionFichiers")
    assert not (travail / "sauvegarde.txt").exists()
    assert "Mot de passe relu : null" in lancer("Serialisation")
    assert b"valeur-fictive" not in (travail / "etudiant.ser").read_bytes()
    sortie = lancer("Variance")
    assert "Somme Integer : 6.0" in sortie and "Somme Double  : 4.0" in sortie
    assert sortie.count("[1, 2, 3, 4, 5]") == 2
    assert "Croissant   : [Noé : 12.0, Lina : 14.5, Maya : 17.0]" in sortie
    assert "[Maya, Lina, Noé]" in lancer("TriDecroissant")

    lignes = ["List<Number> a = entiers;", "List<? extends Number> b = entiers;",
              "Number n = b.get(0);", "b.add(4);",
              "List<? super Integer> c = new ArrayList<Number>();", "c.add(10);",
              "Integer i = c.get(0);", "Object o = c.get(0);"]
    verdicts = []
    for numero, ligne in enumerate(lignes, 1):
        contexte = lignes[1] if numero in (3, 4) else lignes[4] if numero >= 6 else ""
        fichier = travail / "Cas.java"
        fichier.write_text("import java.util.*; class Cas { void test() {"
                           "List<Integer> entiers = new ArrayList<>(List.of(1, 2, 3));"
                           + contexte + ligne + "} }")
        resultat = subprocess.run([*javac, "-d", str(classes), str(fichier)],
                                  capture_output=True, text=True)
        assert (resultat.returncode == 0) == (numero not in (1, 4, 7)), resultat.stderr
        verdicts.append(f"Ligne {numero} | {'OK' if resultat.returncode == 0 else 'Erreur'} | {ligne}")
    (journaux / "CompilationVariance.txt").write_text("\n".join(verdicts) + "\n", encoding="UTF-8")
print("Entrées/sorties, sérialisation, tri et 8 cas de compilation vérifiés.")

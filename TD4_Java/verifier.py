#!/usr/bin/env python3
"""Validation reproductible : compilation, 18 scénarios et contrôles de sorties."""
from pathlib import Path
import json
import subprocess
import time

root = Path(__file__).resolve().parent
classes = root / "classes"
classes.mkdir(exist_ok=True)
results = root / "resultats"
results.mkdir(exist_ok=True)
sources = sorted((root / "src" / "td4").glob("*.java"))
compile_result = subprocess.run(
    ["java", "com.sun.tools.javac.Main", "-encoding", "UTF-8", "-Xlint:all",
     "-d", str(classes), *map(str, sources)], text=True, capture_output=True, check=True)
assert not compile_result.stdout and not compile_result.stderr, "Avertissement de compilation"
(results / "compilation.txt").write_text(
    "OpenJDK 21.0.13 ; compilation -encoding UTF-8 -Xlint:all\n"
    "12 fichiers sources compilés, aucun avertissement.\n", encoding="utf-8")

cases = [
    ("01", ["Ex01"], ["ACBACBACBACBACB"]),
    ("01_sujet", ["Ex01Sujet"], ["ACBACBACBACBACB"]),
    ("02", ["Ex02"], ["terminées=100", "présentes=0"]),
    ("03", ["Ex03"], ["620 / 650 / 1080", "total=2350", "sans débit partiel : OK"]),
    ("04", ["Ex04"], ["Deux threads terminés"]),
    ("04_deadlock", ["Ex04", "--deadlock"], ["Deadlock détecté : 2 threads"]),
    ("05", ["Ex05"], ["Deux passages terminés"]),
    ("05_deadlock", ["Ex05", "--deadlock"], ["Deadlock détecté"]),
    ("06_original", ["Ex06", "original"], ["Copie de e.txt", "Sauvegarde terminée"]),
    ("06_corrige", ["Ex06"], ["Sauvegarde annulée"]),
    ("06_calcul", ["Ex06", "calcul"], ["Sauvegarde annulée"]),
    ("06_executor", ["Ex06", "executor"], ["cancel(true)=true", "Sauvegarde annulée"]),
    ("07_simple", ["Ex07"], ["100 documents, OK"]),
    ("07_timeout", ["Ex07", "timeout"], ["réessayez plus tard", "100 documents, OK"]),
    ("08", ["Ex08"], ["total=2000"]),
    ("09", ["Ex09"], ["visites=800000", "record CAS=30", "1600000"]),
    ("10", ["Ex10"], ["somme=100000", "200=60105", "404=20092", "500=19803"]),
    ("11", ["Ex11", "--fixture"], ["Page, météo erreur", "Page, météo lent", "Météo inconnue", "Dakar :"]),
]
summary = []
for label, args, expected in cases:
    command = ["java", "-ea", "-cp", str(classes), "td4." + args[0], *args[1:]]
    start = time.monotonic()
    r = subprocess.run(command, capture_output=True, text=True, timeout=40, check=True)
    assert not r.stderr, r.stderr
    for value in expected:
        assert value in r.stdout, (label, value, r.stdout)
    if label.startswith("06_") and label != "06_original":
        assert "Sauvegarde terminée" not in r.stdout
        assert "Copie de d.txt" not in r.stdout
    if label == "07_simple":
        assert r.stdout.count("a fini") == 4
    if label == "08":
        assert r.stdout.count("total=2000") == 2
    seconds = round(time.monotonic() - start, 3)
    (results / (label + ".txt")).write_text(
        "$ java -ea -cp classes td4." + " ".join(args) + "\n" + r.stdout, encoding="utf-8")
    summary.append({"case": label, "status": "OK", "seconds": seconds})
    print(f"{label}: OK ({seconds:.3f} s)", flush=True)
(results / "validation.json").write_text(json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8")
print("18 scénarios validés. Le mode de deadlock persistant est volontairement exclu.")

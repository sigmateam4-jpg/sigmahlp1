# Calculatrice Plus V1.3 — Correctifs et vérifications

## Correctifs
### V1.2
1. **Mémoire** : après m+, m− ou mr, le chiffre ou la virgule suivant démarre un nouveau nombre
   (`5 m+ 3 m+ mr` = 8 ; `2 × 3 m+ 4 =` = 8). ⌫ juste après la mémoire remet l'écran à 0.
2. **Zéros décimaux** : la saisie est affichée telle quelle (`1,0`, `0,0`, `1,50`).
3. **Très grands / très petits résultats** : notation scientifique lisible (`1E+48`, `3,333333333333333E−22`).

### V1.3
4. **= répété** : `2 + 3 = =` donne 8, `= = =` donne 11 (répète la dernière opération). Sans opération préalable, `=` ne fait rien.
5. **%** : un chiffre tapé après % démarre un nouveau nombre (`50 % 5` → 5).
6. **+/−** : la virgule en attente est conservée (`5,` → `−5,`) ; utilisable avant la saisie (`+/− , 5` → −0,5) ;
   après `=`, un chiffre tapé démarre un nouveau nombre (`5+3= +/− 2` → 2).
7. **Affichage** : l'écran principal (1 ligne) et l'historique réduisent automatiquement la taille du texte
   (14–40 sp et 9–15 sp) pour ne plus dépasser (MainActivity.java).

## Tests (moteur de calcul)
- `tests/CalculatorEngineTest.java` : 23 contrôles.
- `tests/CalculatorEngineExtendedTest.java` : 107 contrôles (opérations, décimaux, enchaînements, ±, %, mémoire,
  retour arrière, erreurs, grands nombres, historique, = répété) + 20 000 expressions comparées à une référence
  + 30 000 suites de touches aléatoires sans plantage. Code de sortie 1 si un test échoue.

## Non vérifié
Compilation de l'APK et rendu sur appareil réel (Galaxy S8). La modification d'affichage de l'étape 7
(`setAutoSizeTextTypeUniformWithConfiguration`, API 26 = minSdk) n'a pas pu être compilée ici.

## Exécution des tests
javac -encoding UTF-8 -d test-build app/src/main/java/com/calculatriceplus/app/CalculatorEngine.java tests/*.java
java -Dfile.encoding=UTF-8 -cp test-build CalculatorEngineTest
java -Dfile.encoding=UTF-8 -cp test-build CalculatorEngineExtendedTest

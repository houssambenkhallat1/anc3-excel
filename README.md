# Projet ANC3 2425 - Groupe g04 - Excel

## Notes de version itération 1

### Liste des bugs connus

* (RESOLU) L'expression = B2 + 3 * 5 + C4 déclenche une erreur "SYNTAX ERROR"  au lieu de "VALEUR" si par exemple la valeur de B2 ou de C4 ne sont pas numériques
* (RESOLU) Pour les références circulaires, la source affiche bien #CIRCULAR REF. mais l'autre cellule affiche SYNTAX ERROR.
* (RESOLU) Les cellules références affichent l'expression mais dès que plusieurs modifications y sont faites, elles  affichent la valeur au lieu de l'expression. Donc cela ne fonction qu'en partie.

### Liste des fonctionnalités supplémentaires

### Divers

## Notes de version itération 2

...

## Notes de version itération 3

ajout des fonctions exposant, min, max et avg + footer label
ajout également de la modification de la taille du tableau (taille du tableau adaptable pour les tests)


## Pour lancer le projet

### Option 1 

Dans le menu d'exécution, ne pas choisir "Current File" mais "App"

### Option 2

Dans VM options, ajouter ça : 

```
--add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=org.controlsfx.controls
```

Source : https://github.com/controlsfx/controlsfx/wiki/Using-ControlsFX-with-JDK-9-and-above 

### Option 3

Dans la console de maven, tapper `mvn javafx:run`
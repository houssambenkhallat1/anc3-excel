# Projet ANC3 2425 - Groupe g04 - Excel

## Notes de version itération 1

### Liste des bugs connus

* bug 1
* bug 2
* ...

### Liste des fonctionnalités supplémentaires

### Divers

## Notes de version itération 2

...

## Notes de version itération 3

...


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
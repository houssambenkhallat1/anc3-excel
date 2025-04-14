package excel.model;

import java.io.*;
import java.util.*;

public class SpreadsheetFileHandler {
    /**
     * Sauvegarde la feuille de calcul dans un fichier .e4e
     *
     * @param spreadsheet Le modèle de feuille de calcul à sauvegarder
     * @param filepath Le chemin du fichier de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void saveSpreadsheet(SpreadsheetModel spreadsheet, String filepath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // Écrire la première ligne : nombre de lignes et colonnes
            writer.write(spreadsheet.getRowCount() + "," + spreadsheet.getColumnCount());
            writer.newLine();

            // Parcourir toutes les cellules et écrire uniquement celles qui ne sont pas vides
            for (int row = 0; row < spreadsheet.getRowCount(); row++) {
                for (int col = 0; col < spreadsheet.getColumnCount(); col++) {
                    Cell cell = spreadsheet.getCell(row, col);
                    if (!cell.getContent().isEmpty()){
                        writer.write(row + "," + col + ";" + cell.getContent());
                        writer.newLine();
                    }
                }
            }
        }
    }

    /**
     * Charge une feuille de calcul à partir d'un fichier .e4e
     *
     * @param filepath Le chemin du fichier à charger
     * @return Un nouveau modèle de feuille de calcul
     * @throws IOException En cas d'erreur de lecture
     * @throws IllegalArgumentException En cas de format de fichier invalide
     */
    public static SpreadsheetModel loadSpreadsheet(String filepath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            // Lire la première ligne pour obtenir la taille de la feuille
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IllegalArgumentException("Fichier vide");
            }

            // Parsing de la première ligne
            String[] dimensions = firstLine.split(",");
            if (dimensions.length != 2) {
                throw new IllegalArgumentException("Format de la première ligne invalide");
            }

            int rowCount = Integer.parseInt(dimensions[0].trim());
            int colCount = Integer.parseInt(dimensions[1].trim());

            // Créer un nouveau modèle de feuille de calcul
            SpreadsheetModel spreadsheet = new SpreadsheetModel(rowCount, colCount);

            // Lire les cellules
            String line;
            while ((line = reader.readLine()) != null) {
                // Parsing de chaque ligne de cellule
                String[] cellParts = line.split(";");
                if (cellParts.length != 2) {
                    throw new IllegalArgumentException("Format de ligne de cellule invalide : " + line);
                }

                // Parsing des coordonnées de la cellule
                String[] coords = cellParts[0].split(",");
                if (coords.length != 2) {
                    throw new IllegalArgumentException("Coordonnées de cellule invalides : " + cellParts[0]);
                }

                int row = Integer.parseInt(coords[0].trim());
                int col = Integer.parseInt(coords[1].trim());
                String content = cellParts[1];

                // Vérifier les limites
                if (row < 0 || row >= rowCount || col < 0 || col >= colCount) {
                    throw new IllegalArgumentException("Coordonnées de cellule hors limites : " + row + "," + col);
                }

                // Définir le contenu de la cellule
                spreadsheet.getCell(row, col).setContent(content);
            }

            return spreadsheet;
        }
    }


}
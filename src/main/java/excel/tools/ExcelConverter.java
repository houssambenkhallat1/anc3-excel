package excel.tools;

/**
 * Utilitaire pour convertir entre les références Excel et les indices
 */
public class ExcelConverter {
    /**
     * Convertit les indices de ligne et colonne en référence Excel (ex: A1, B2)
     */
    public static String rowColToExcel(int row, int column) {
        return columnIndexToLetter(column) + (row + 1);
    }

    /**
     * Convertit un indice de colonne en lettre (0->A, 1->B, etc.)
     */
    public static String columnIndexToLetter(int columnIndex) {
        StringBuilder result = new StringBuilder();

        do {
            result.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        } while (columnIndex >= 0);

        return result.toString();
    }

    /**
     * Convertit une référence Excel en indices de ligne et colonne
     */
    public static int[] excelToRowCol(String reference) {
        if (reference == null || reference.isEmpty()) {
            return new int[] {0, 0};
        }

        // Séparer les lettres des chiffres
        String letters = reference.replaceAll("[0-9]", "");
        String numbers = reference.replaceAll("[A-Za-z]", "");

        // Convertir les lettres en indice de colonne
        int column = 0;
        for (int i = 0; i < letters.length(); i++) {
            column = column * 26 + (Character.toUpperCase(letters.charAt(i)) - 'A' + 1);
        }
        column--; // Ajuster pour l'indice 0

        // Convertir les chiffres en indice de ligne
        int row = Integer.parseInt(numbers) - 1; // Ajuster pour l'indice 0

        return new int[] {row, column};
    }
}
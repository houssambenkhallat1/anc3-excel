package excel.model;

/**
 * Interface pour les expressions pouvant être évaluées dans une cellule
 */
public interface Expression {
    /**
     * Évalue l'expression et retourne sa valeur
     */
    CellValue evaluate(SpreadsheetModel spreadsheet) throws CircularReferenceException;
}
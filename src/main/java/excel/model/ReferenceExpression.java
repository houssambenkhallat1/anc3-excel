package excel.model;

import excel.tools.ExcelConverter;

/**
 * Expression représentant une référence à une autre cellule
 */
public class ReferenceExpression implements Expression {
    private final String cellReference;
    private final Cell sourceCell; // La cellule qui contient cette référence

    public ReferenceExpression(String cellReference, SpreadsheetModel spreadsheet, Cell sourceCell) {
        this.cellReference = cellReference;
        this.sourceCell = sourceCell;
    }

    public ReferenceExpression(String cellReference) {
        this(cellReference, null, null);
    }

    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) throws CircularReferenceException {
        if (spreadsheet == null) {
            return CellValue.ofError(CellError.VALUE_ERROR);
        }

        int[] coords = ExcelConverter.excelToRowCol(cellReference);
        int row = coords[0];
        int col = coords[1];

        Cell referencedCell = spreadsheet.getCell(row, col);
        if (referencedCell == null) {
            return CellValue.ofError(CellError.VALUE_ERROR);
        }

        // Vérifier si la cellule référencée est déjà en cours d'évaluation (détection de cycle)
        if (referencedCell.isEvaluating()) {
            throw new CircularReferenceException("Référence circulaire détectée: " + cellReference);
        }

        // Renvoyer la valeur de la cellule référencée
        return referencedCell.getValue();
    }
}
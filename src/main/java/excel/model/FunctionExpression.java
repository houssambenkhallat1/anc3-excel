package excel.model;

import excel.tools.ExcelConverter;

import java.util.ArrayList;
import java.util.List;

public class FunctionExpression implements Expression{
    private  String rangeStart;
    private  String rangeEnd;
    private Cell cellSource;

    public FunctionExpression(String rangeStart, String rangeEnd, Cell source) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.cellSource = source;
    }
    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) throws CircularReferenceException {
        int[] startCoords = ExcelConverter.excelToRowCol(rangeStart);
        int[] endCoords = ExcelConverter.excelToRowCol(rangeEnd);

        // Vérifier la validité du range
        if (startCoords[0] > endCoords[0] || startCoords[1] > endCoords[1]) {
            return CellValue.ofError(CellError.SYNTAX_ERROR);
        }

        double sum = 0;

        // Parcourir toutes les cellules dans le range
        for (int row = startCoords[0]; row <= endCoords[0]; row++) {
            for (int col = startCoords[1]; col <= endCoords[1]; col++) {
                Cell cell = spreadsheet.getCell(row, col);
                spreadsheet.addDependency(cellSource, cell);

                // Vérifier si la cellule est dans le range
                if (cell == null) {
                    return CellValue.ofError(CellError.VALUE_ERROR);
                }
                if (cell.getAddress().compareTo(cellSource.getAddress())==0){
                    throw new CircularReferenceException("Circular reference detected: " + cell);
                }

                CellValue cellValue = cell.getValue();

                // Vérifier s'il y a des erreurs ou des cellules non numériques
                if (cellValue.isError()) {
                    return cellValue;
                }

                if (!cellValue.isNumber()) {
                    return CellValue.ofError(CellError.VALUE_ERROR);
                }

                // Convertir la valeur en nombre pour la somme
                double numValue = cellValue.getNumberValue();
                sum += numValue;
            }
        }

        return CellValue.ofNumber(sum);
    }

}

package excel.model;

import excel.tools.ExcelConverter;

import java.util.ArrayList;
import java.util.List;

public class FunctionExpression implements Expression{
    private  String rangeStart;
    private  String rangeEnd;
    private Cell cellSource;
    private String functionName;

    public FunctionExpression(String functionName, String rangeStart, String rangeEnd, Cell source) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.cellSource = source;
        this.functionName = functionName;
    }

    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) throws CircularReferenceException {
        int[] startCoords = ExcelConverter.excelToRowCol(rangeStart);
        int[] endCoords = ExcelConverter.excelToRowCol(rangeEnd);

        // Check if the range is valid
        if (startCoords[0] > endCoords[0] || startCoords[1] > endCoords[1]) {
            return CellValue.ofError(CellError.SYNTAX_ERROR);
        }

        List<Double> values = new ArrayList<>();

        for (int row = startCoords[0]; row <= endCoords[0]; row++) {
            for (int col = startCoords[1]; col <= endCoords[1]; col++) {
                Cell cell = spreadsheet.getCell(row, col);
                if (cell == null) {
                    return CellValue.ofError(CellError.VALUE_ERROR);
                }
                spreadsheet.addDependency(cellSource, cell);

                if (cell.getAddress().equals(cellSource.getAddress())) {
                    throw new CircularReferenceException("Circular reference detected: " + cell);
                }

                CellValue cellValue = cell.getValue();

                if (cellValue.isError()) {
                    return cellValue;
                }

                if (!cellValue.isNumber()) {
                    return CellValue.ofError(CellError.VALUE_ERROR);
                }

                values.add(cellValue.getNumberValue());
            }
        }

        if (values.isEmpty()) {
            return CellValue.ofError(CellError.VALUE_ERROR);
        }

        switch (functionName) {
            case "sum":
                double sum = 0;
                for (double d : values) sum += d;
                return CellValue.ofNumber(sum);
            case "avg":
                double sumAvg = 0;
                for (double d : values) sumAvg += d;
                return CellValue.ofNumber(sumAvg / values.size());
            case "min":
                double min = Double.POSITIVE_INFINITY;
                for (double d : values) min = Math.min(min, d);
                return CellValue.ofNumber(min);
            case "max":
                double max = Double.NEGATIVE_INFINITY;
                for (double d : values) max = Math.max(max, d);
                return CellValue.ofNumber(max);
            default:
                return CellValue.ofError(CellError.SYNTAX_ERROR);
        }
    }

}

package excel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modèle principal du tableur
 */
public class SpreadsheetModel {
    private final int rowCount;
    private final int columnCount;
    private final Cell[][] cells;
    private final ExpressionBuilder expressionBuilder;
    private final Map<Cell, List<Cell>> dependencies = new HashMap<>();

    public SpreadsheetModel(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.cells = new Cell[rowCount][columnCount];
        this.expressionBuilder = new ExpressionBuilder(this);

        // Initialiser les cellules
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                cells[row][col] = new Cell(row, col, this);
            }
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Cell getCell(int row, int column) {
        if (row >= 0 && row < rowCount && column >= 0 && column < columnCount) {
            return cells[row][column];
        }
        return null;
    }

    public ExpressionBuilder getExpressionBuilder() {
        return expressionBuilder;
    }

    /**
     * Enregistre une dépendance entre cellules
     */
    public void addDependency(Cell dependentCell, Cell sourceCell) {
        dependencies.computeIfAbsent(sourceCell, k -> new ArrayList<>()).add(dependentCell);
    }

    /**
     * Notifie les cellules dépendantes lorsqu'une cellule change
     */
    public void notifyCellChanged(Cell cell) {
        List<Cell> dependents = dependencies.get(cell);
        if (dependents != null) {
            for (Cell dependent : dependents) {
                dependent.recalculate();
            }
        }
    }
}

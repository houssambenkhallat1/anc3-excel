package excel.model;

import java.util.*;

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
    public void addDependency(Cell dependentCell, Cell sourceCell) throws CircularReferenceException {
        // Prevent adding a dependency if it would cause a cycle
        if (hasCircularDependency(sourceCell, dependentCell)) {
            throw new CircularReferenceException(
                    "Circular dependency detected when adding dependency from "
                            + dependentCell.getAddress() + " to " + sourceCell.getAddress());
        }
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

    public boolean hasCircularDependency(Cell source, Cell dependent) {
        return hasPath(dependent, source, new HashSet<>());
    }

    private boolean hasPath(Cell current, Cell target, Set<Cell> visited) {
        if (current == target) {
            return true;
        }
        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        List<Cell> dependents = dependencies.get(current);
        if (dependents != null) {
            for (Cell next : dependents) {
                if (hasPath(next, target, visited)) {
                    return true;
                }
            }
        }
        return false;
    }
}

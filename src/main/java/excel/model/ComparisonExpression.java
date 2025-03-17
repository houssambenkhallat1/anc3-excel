package excel.model;

/**
 * Expression représentant une comparaison (>, <, >=, <=, =, !=)
 */
public class ComparisonExpression implements Expression {
    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN_OR_EQUAL("<=");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    private final Expression left;
    private final Expression right;
    private final Operator operator;

    public ComparisonExpression(Expression left, Expression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) {
        CellValue leftValue = left.evaluate(spreadsheet);
        CellValue rightValue = right.evaluate(spreadsheet);

        // Vérifier si l'une des opérandes est une erreur
        if (leftValue.isError()) return leftValue;
        if (rightValue.isError()) return rightValue;

        // Comparaison selon les types
        if (leftValue.isNumber() && rightValue.isNumber()) {
            double leftNum = leftValue.getNumberValue();
            double rightNum = rightValue.getNumberValue();
            return evaluateNumberComparison(leftNum, rightNum);
        }

        if (leftValue.isBoolean() && rightValue.isBoolean()) {
            boolean leftBool = leftValue.getBooleanValue();
            boolean rightBool = rightValue.getBooleanValue();
            return evaluateBooleanComparison(leftBool, rightBool);
        }

        // Par défaut, comparer les représentations textuelles
        String leftText = leftValue.getTextValue();
        String rightText = rightValue.getTextValue();
        return evaluateTextComparison(leftText, rightText);
    }

    private CellValue evaluateNumberComparison(double left, double right) {
        switch (operator) {
            case EQUAL: return CellValue.ofBoolean(left == right);
            case NOT_EQUAL: return CellValue.ofBoolean(left != right);
            case GREATER_THAN: return CellValue.ofBoolean(left > right);
            case LESS_THAN: return CellValue.ofBoolean(left < right);
            case GREATER_THAN_OR_EQUAL: return CellValue.ofBoolean(left >= right);
            case LESS_THAN_OR_EQUAL: return CellValue.ofBoolean(left <= right);
            default: return CellValue.ofError(CellError.VALUE_ERROR);
        }
    }

    private CellValue evaluateBooleanComparison(boolean left, boolean right) {
        switch (operator) {
            case EQUAL: return CellValue.ofBoolean(left == right);
            case NOT_EQUAL: return CellValue.ofBoolean(left != right);
            default: return CellValue.ofError(CellError.VALUE_ERROR);
        }
    }

    private CellValue evaluateTextComparison(String left, String right) {
        int comparison = left.compareTo(right);
        switch (operator) {
            case EQUAL: return CellValue.ofBoolean(comparison == 0);
            case NOT_EQUAL: return CellValue.ofBoolean(comparison != 0);
            case GREATER_THAN: return CellValue.ofBoolean(comparison > 0);
            case LESS_THAN: return CellValue.ofBoolean(comparison < 0);
            case GREATER_THAN_OR_EQUAL: return CellValue.ofBoolean(comparison >= 0);
            case LESS_THAN_OR_EQUAL: return CellValue.ofBoolean(comparison <= 0);
            default: return CellValue.ofError(CellError.VALUE_ERROR);
        }
    }
}
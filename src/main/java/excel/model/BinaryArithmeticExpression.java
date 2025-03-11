package excel.model;

/**
 * Expression représentant une opération arithmétique binaire (+, -, *, /)
 */
public class BinaryArithmeticExpression implements Expression {
    public enum Operator {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/");

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

    public BinaryArithmeticExpression(Expression left, Expression right, Operator operator) {
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

        // Pour les opérations arithmétiques, convertir en nombres
        double leftNum = leftValue.isNumber() ? leftValue.getNumberValue() :
                leftValue.isBoolean() ? (leftValue.getBooleanValue() ? 1 : 0) : 0;

        double rightNum = rightValue.isNumber() ? rightValue.getNumberValue() :
                rightValue.isBoolean() ? (rightValue.getBooleanValue() ? 1 : 0) : 0;

        switch (operator) {
            case ADD:
                return CellValue.ofNumber(leftNum + rightNum);
            case SUBTRACT:
                return CellValue.ofNumber(leftNum - rightNum);
            case MULTIPLY:
                return CellValue.ofNumber(leftNum * rightNum);
            case DIVIDE:
                if (rightNum == 0) {
                    return CellValue.ofError(CellError.DIV_ZERO);
                }
                return CellValue.ofNumber(leftNum / rightNum);
            default:
                return CellValue.ofError(CellError.VALUE_ERROR);
        }
    }
}
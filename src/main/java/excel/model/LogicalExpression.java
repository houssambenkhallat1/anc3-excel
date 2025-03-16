package excel.model;

/**
 * Expression représentant une opération logique (AND, OR, NOT)
 */
public class LogicalExpression implements Expression {
    public enum Operator {
        AND("and"), OR("or"), NOT("not");

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
    private final Expression right; // peut être null pour l'opérateur NOT
    private final Operator operator;

    // Constructeur pour les opérateurs binaires (AND, OR)
    public LogicalExpression(Expression left, Expression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    // Constructeur pour l'opérateur unaire (NOT)
    public LogicalExpression(Expression expression, Operator operator) {
        this.left = expression;
        this.right = null;
        this.operator = operator;
    }

    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet)throws CircularReferenceException {
        CellValue leftValue = left.evaluate(spreadsheet);

        // Vérifier si l'opérande gauche est une erreur
        if (leftValue.isError()) return leftValue;

        boolean leftBool = toBooleanValue(leftValue);

        // Opérateur NOT (unaire)
        if (operator == Operator.NOT) {
            return CellValue.ofBoolean(!leftBool);
        }

        // Pour les opérateurs binaires, évaluer l'opérande droite
        CellValue rightValue = right.evaluate(spreadsheet);
        if (rightValue.isError()) return rightValue;
        if (!rightValue.isBoolean()) return CellValue.ofError(CellError.SYNTAX_ERROR);

        boolean rightBool = toBooleanValue(rightValue);

        // Opérateurs AND et OR
        switch (operator) {
            case AND: return CellValue.ofBoolean(leftBool && rightBool);
            case OR: return CellValue.ofBoolean(leftBool || rightBool);
            default: return CellValue.ofError(CellError.VALUE_ERROR);
        }
    }

    /**
     * Convertit une valeur en booléen selon les règles du tableur:
     * - Booléen: tel quel
     * - Nombre: 0 = faux, non-0 = vrai
     * - Texte: vide = faux, non-vide = vrai
     */
    private boolean toBooleanValue(CellValue value) {
        if (value.isBoolean()) {
            return value.getBooleanValue();
        } else if (value.isNumber()) {
            return value.getNumberValue() != 0;
        } else {
            return !value.getTextValue().isEmpty();
        }
    }
}

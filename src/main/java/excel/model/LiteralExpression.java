package excel.model;

/**
 * Expression représentant une valeur littérale (nombre, texte, booléen)
 */
public class LiteralExpression implements Expression {
    private final CellValue value;

    public LiteralExpression(CellValue value) {
        this.value = value;
    }

    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) {
        return value;
    }
}
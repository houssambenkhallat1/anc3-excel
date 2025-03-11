package excel.model;

public class LiteralExpression implements Expression {
    @Override
    public CellValue evaluate(SpreadsheetModel spreadsheet) throws CircularReferenceException {
        return null;
    }
}

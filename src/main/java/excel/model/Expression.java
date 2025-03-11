package excel.model;

public interface Expression {

    CellValue evaluate(SpreadsheetModel spreadsheet) ;
}
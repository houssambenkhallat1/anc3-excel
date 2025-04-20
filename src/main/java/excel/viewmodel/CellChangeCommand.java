package excel.viewmodel;

import excel.model.Cell;
import excel.model.SpreadsheetModel;

public class CellChangeCommand implements Command {
    private final SpreadsheetModel model;
    private final int row;
    private final int col;
    private final String oldContent;
    private final String newContent;

    public CellChangeCommand(SpreadsheetModel model, int row, int col, String oldContent, String newContent) {
        this.model = model;
        this.row = row;
        this.col = col;
        this.oldContent = oldContent;
        this.newContent = newContent;
    }

    @Override
    public void execute() {
        model.setCellContentSilently(row, col, newContent);
    }

    @Override
    public void undo() {
        model.setCellContentSilently(row, col, oldContent);
    }
}

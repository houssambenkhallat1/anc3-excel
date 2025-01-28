package excel;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        // L'exemple ci-dessous est tiré de la documentation de ControlsFX
        // https://controlsfx.github.io/javadoc/11.1.0/org.controlsfx.controls/org/controlsfx/control/spreadsheet/SpreadsheetView.html
        int rowCount = 15;
        int columnCount = 10;

        GridBase grid = new GridBase(rowCount, columnCount);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,"value");
                // INFO : une SpreadsheetCell a un itemProperty qui encapsule la valeur de la cellule (cell.itemProperty())
                list.add(cell);
            }
            rows.add(list);
        }
        grid.setRows(rows);

        SpreadsheetView spreadsheetView = new SpreadsheetView(grid);
        // INFO : un SpreadsheetView a un selectionModel qui permet de gérer les cellules sélectionnées
        // INFO : un SpreadsheetView a un editingCellProperty qui permet de récupérer la cellule en cours d'édition

        Scene scene = new Scene(spreadsheetView, 633, 315);
        primaryStage.setTitle("Spreadsheet");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
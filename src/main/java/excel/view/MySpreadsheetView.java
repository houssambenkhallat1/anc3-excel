package excel.view;

import excel.viewmodel.SpreadsheetViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.Objects;

public class MySpreadsheetView extends SpreadsheetView {
    private final SpreadsheetViewModel viewModel;
    private static final int CELL_PREF_WIDTH = 150;
    private final GridBase grid;
    private boolean updatingCellFromViewModel = false; // empêche d'update le contenu de la cellule lorsque l'on set display value

    public MySpreadsheetView(SpreadsheetViewModel viewModel) {
        this.viewModel = viewModel;
        this.setPadding(new Insets(0, 20, 20, 20));

        this.grid = createGridAndBindings();
        this.setGrid(this.grid);

        // Configurer l'édition
        this.setEditable(true);

        // Écouter les changements de cellule en édition
        this.editingCellProperty().addListener((observableValue, oldVal, newVal) -> {
            if ( oldVal != null){
                viewModel.setNotEditingCell(oldVal.getRow(), oldVal.getColumn());
            } else if (newVal != null) {
                viewModel.setEditingCell(newVal.getRow(), newVal.getColumn());
            }
        });

        // Écouter les changements de sélection
        this.getSelectionModel().getSelectedCells().addListener((ListChangeListener.Change<? extends TablePosition> change) -> {
            if (!change.getList().isEmpty()) {
                TablePosition cell = change.getList().get(0);
                viewModel.selectCell(cell.getRow(), cell.getColumn());
            }
        });

        // Valider l'édition lorsque l'utilisateur appuie sur Entrée
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                viewModel.commitEdit();
                event.consume();
            }
        });

        layoutSpreadSheet();
    }
    private void layoutSpreadSheet() {
        for (int column = 0; column < grid.getColumnCount(); column++) {
            this.getColumns().get(column).setPrefWidth(CELL_PREF_WIDTH);
        }
    }

    private GridBase createGridAndBindings() {
        GridBase grid = new GridBase(viewModel.getRowCount(), viewModel.getColumnCount());

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "");

                int finalRow = row;
                int finalColumn = column;

                // Lier la modification de cellule dans la vue au ViewModel
                cell.itemProperty().addListener((observableValue, oldVal, newVal) -> {
                    if (!Objects.equals(oldVal, newVal) && newVal != null && !updatingCellFromViewModel) {
                        viewModel.updateCellContent(finalRow, finalColumn, (String) newVal);
                    }
                });

                // Lier les modifications du ViewModel à la vue
                viewModel.getCellValueProperty(finalRow, finalColumn).addListener((observableValue, oldVal, newVal) -> {
                    if (!Objects.equals(oldVal, newVal) && !Objects.equals(cell.getItem(), newVal)) {
                        updatingCellFromViewModel = true;
                        cell.setItem(newVal);
                        updatingCellFromViewModel = false;
                    }
                });

                list.add(cell);
            }
            rows.add(list);
        }
        grid.setRows(rows);
        return grid;
    }
}
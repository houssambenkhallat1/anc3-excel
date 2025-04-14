package excel.viewmodel;

import excel.model.Cell;
import excel.model.SpreadsheetFileHandler;
import java.io.IOException;
import excel.tools.ExcelConverter;
import excel.model.SpreadsheetModel;
import javafx.beans.property.*;
import java.util.ArrayDeque;
import java.util.Deque;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * ViewModel pour le tableur
 */
public class SpreadsheetViewModel {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private final BooleanProperty canUndo = new SimpleBooleanProperty(false);
    private final BooleanProperty canRedo = new SimpleBooleanProperty(false);

    private final SpreadsheetModel model;
    private final StringProperty editBarContent = new SimpleStringProperty("");
    private final ObjectProperty<int[]> selectedCell = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty editableProperty = new SimpleBooleanProperty(true);

    // Pour le journal d'actions (peut être utile pour le débogage)
    private final ObservableList<String> actions = FXCollections.observableArrayList();
    private final StringProperty lastAction = new SimpleStringProperty("");

    public SpreadsheetViewModel(SpreadsheetModel model) {
        this.model = model;

        // Mettre à jour la barre d'édition quand la cellule sélectionnée change
        selectedCell.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Cell cell = model.getCell(newVal[0], newVal[1]);
                if (cell != null) {
                    editBarContent.set(cell.getContent());
                } else {
                    editBarContent.set("");
                }
            } else {
                editBarContent.set("");
            }
        });

        // Enregistrer les actions (pour le débogage)
        actions.addListener((ListChangeListener<? super String>) (c) -> {
            if (!actions.isEmpty()) {
                lastAction.set(actions.get(actions.size() - 1));
            }
        });
    }
    public SpreadsheetModel getModel() {
        return model;
    }
    public void loadFromFile(String filepath) throws IOException, IllegalArgumentException {
        SpreadsheetModel newModel = SpreadsheetFileHandler.loadSpreadsheet(filepath);

        // Check dimensions
        if (newModel.getRowCount() != model.getRowCount() || newModel.getColumnCount() != model.getColumnCount()) {
            throw new IllegalArgumentException("Loaded file dimensions do not match current spreadsheet.");
        }

        // Copy cell contents
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell newCell = newModel.getCell(row, col);
                Cell currentCell = model.getCell(row, col);
                currentCell.setContent(newCell.getContent());
            }
        }
        undoStack.clear();
        redoStack.clear();
        updateUndoRedoState();
    }
    public int getRowCount() {
        return model.getRowCount();
    }

    public int getColumnCount() {
        return model.getColumnCount();
    }

    /**
     * Retourne la propriété pour le contenu de la barre d'édition
     */
    public StringProperty editBarContentProperty() {
        return editBarContent;
    }

    /**
     * Retourne la propriété pour la cellule sélectionnée
     */
    public ObjectProperty<int[]> selectedCellProperty() {
        return selectedCell;
    }

    /**
     * Retourne la valeur affichée d'une cellule
     */
    public StringProperty getCellValueProperty(int row, int column) {
        Cell cell = model.getCell(row, column);
        if (cell != null) {
            return cell.displayValueProperty();
        }
        return new SimpleStringProperty("");
    }

    /**
     * Sélectionne une cellule
     */
    public void selectCell(int row, int column) {
        addAction("Select cell at " + row + "," + column);
        selectedCell.set(new int[]{row, column});
    }

    /**
     * Indique qu'une cellule est en cours d'édition
     */
    public void setEditingCell(int row, int column) {
        addAction("Editing cell at " + row + "," + column);
        // Cette méthode peut être utilisée pour suivre l'état d'édition
    }

    /**
     * Met à jour le contenu d'une cellule (appelé depuis la vue)
     */
    public void updateCellContent(int row, int column, String content) {
        addAction("Update cell content at " + row + "," + column + ": " + content);
        Cell cell = model.getCell(row, column);
        if (cell != null) {
            String oldContent = cell.getContent();
            Command command = new CellChangeCommand(model, row, column, oldContent, content);
            executeCommand(command);
        }
    }
    private void executeCommand(Command command) {
        undoStack.push(command);
        command.execute();
        redoStack.clear();
        updateUndoRedoState();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            updateUndoRedoState();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            updateUndoRedoState();
        }
    }

    private void updateUndoRedoState() {
        canUndo.set(!undoStack.isEmpty());
        canRedo.set(!redoStack.isEmpty());
    }

    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    public BooleanProperty canRedoProperty() {
        return canRedo;
    }

    /**
     * Valide l'édition de la barre d'édition
     */
    public void commitEdit() {
        int[] position = selectedCell.get();
        if (position != null) {
            updateCellContent(position[0], position[1], editBarContent.get());
            addAction("Commit edit for cell at " + position[0] + "," + position[1]);
        }
    }


    /**
     * Ajoute une action au journal
     */
    public boolean addAction(String action) {
        return actions.add(action);
    }


}
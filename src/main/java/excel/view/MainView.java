package excel.view;

import excel.viewmodel.SpreadsheetViewModel;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import excel.model.SpreadsheetFileHandler;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;

public class MainView extends BorderPane {

    public MainView(SpreadsheetViewModel viewModel, Stage stage) {
        // Create the menu bar
        MenuBar menuBar = createMenuBar(viewModel, stage);

        // Header with cell editor
        HeaderView headerView = new HeaderView(viewModel);

        // Combine menu bar and header in a VBox
        VBox topContainer = new VBox(menuBar, headerView);
        this.setTop(topContainer);

        // Spreadsheet view
        this.setCenter(new MySpreadsheetView(viewModel));

        //pour garantir que le panneau reçoit les événements clavier
        this.setFocusTraversable(true);

        Label footerLabel = new Label("footer label");

        this.setBottom(footerLabel);

    }

    private MenuBar createMenuBar(SpreadsheetViewModel viewModel, Stage stage) {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem openItem = new MenuItem("Open");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fileMenu.getItems().addAll(openItem, saveItem);

        // Edit Menu (Placeholder)
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        // Set actions
        undoItem.setOnAction(e -> viewModel.undo());
        redoItem.setOnAction(e -> viewModel.redo());
        // Ajouter les raccourcis clavier
        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        editMenu.getItems().addAll(undoItem, redoItem);

        // Gestion des raccourcis au niveau de la scène
        this.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                if (event.getCode() == KeyCode.Z) {
                    viewModel.undo();
                    event.consume();
                } else if (event.getCode() == KeyCode.Y) {
                    viewModel.redo();
                    event.consume();
                }
                else if (event.getCode() == KeyCode.S) {
                    try {
                        viewModel.handleSave(stage);
                    } catch (IOException e) {
                        showErrorDialog("Save Error", "Could not save file: " + e.getMessage(), stage);
                    };
                    event.consume();
                } else if (event.getCode() == KeyCode.N) {
                    try {
                        viewModel.handleOpen(stage);
                    } catch (IOException e) {
                        showErrorDialog("Load Error", "Could not load file: " + e.getMessage(), stage);
                    }
                    event.consume();
                }
            }
        });



        menuBar.getMenus().addAll(fileMenu, editMenu);
        undoItem.disableProperty().bind(viewModel.canUndoProperty().not());
        redoItem.disableProperty().bind(viewModel.canRedoProperty().not());


        // Set actions for Save and Open
        saveItem.setOnAction(event ->{
        try {
            viewModel.handleSave(stage);
        } catch (IOException e) {
            showErrorDialog("Save Error", "Could not save file: " + e.getMessage(), stage);
                }
        } );


        openItem.setOnAction(event -> {
            try {
                viewModel.handleOpen(stage);
            } catch (IOException e) {
                showErrorDialog("Load Error", "Could not load file: " + e.getMessage(), stage);
            }
        } );

        return menuBar;
    }


    private void showErrorDialog(String title, String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
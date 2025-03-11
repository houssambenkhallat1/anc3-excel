package excel.view;

import excel.tools.ExcelConverter;
import excel.viewmodel.SpreadsheetViewModel;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HeaderView extends VBox {
    private final SpreadsheetViewModel viewModel;
    private final TextField cellContentEditor;
    private final Label cellAddressLabel;

    public HeaderView(SpreadsheetViewModel viewModel) {
        this.viewModel = viewModel;
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // Créer un label pour afficher l'adresse de la cellule sélectionnée
        cellAddressLabel = new Label("Cellule: ");

        // Créer un champ de texte pour éditer le contenu de la cellule
        cellContentEditor = new TextField();
        cellContentEditor.setPrefWidth(400);

        // Lier le contenu de l'éditeur à la propriété du ViewModel
        cellContentEditor.textProperty().bindBidirectional(viewModel.editBarContentProperty());

        // Configurer l'action quand l'utilisateur appuie sur Entrée
        cellContentEditor.setOnAction(event -> viewModel.commitEdit());

        // Mettre à jour l'affichage de l'adresse quand la cellule sélectionnée change
        viewModel.selectedCellProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int[] position = newVal;
                cellAddressLabel.setText("Cellule: " + ExcelConverter.rowColToExcel(position[0], position[1]));
            } else {
                cellAddressLabel.setText("Cellule: ");
            }
        });

        // Créer un layout horizontal pour l'éditeur de cellule
        HBox editorBar = new HBox(10);
        editorBar.getChildren().addAll(cellAddressLabel, cellContentEditor);

        // Ajouter à la vue
        this.getChildren().add(editorBar);
    }
}
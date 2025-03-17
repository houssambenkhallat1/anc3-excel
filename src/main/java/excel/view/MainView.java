package excel.view;

import excel.viewmodel.SpreadsheetViewModel;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {
    public MainView(SpreadsheetViewModel viewModel) {
        // En-tête avec l'éditeur de cellule
        this.setTop(new HeaderView(viewModel));

        // Vue principale du tableur
        this.setCenter(new MySpreadsheetView(viewModel));
    }
}
package excel;

import excel.model.SpreadsheetModel;
import excel.view.MainView;
import excel.viewmodel.SpreadsheetViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final int ROWS = 10;
    private static final int COLUMNS = 4;

    @Override
    public void start(Stage primaryStage) {
        // Créer le modèle
        SpreadsheetModel model = new SpreadsheetModel(ROWS, COLUMNS);

        // Créer le ViewModel
        SpreadsheetViewModel viewModel = new SpreadsheetViewModel(model);

        // Créer la vue
        MainView mainView = new MainView(viewModel, primaryStage);

        // Configurer la scène
        Scene scene = new Scene(mainView, 800, 600);
        primaryStage.setTitle("Mini Tableur");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
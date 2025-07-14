package frontend;

import backend.repository.DownloadHistory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/views/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650); // starting size
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Internet Video Downloader");

        stage.setResizable(true); // ✅ allow resizing
        stage.setMinWidth(800);   // optional
        stage.setMinHeight(600);  // optional

        stage.show();
    }


    public static void main(String[] args) {
        DownloadHistory.initializeDatabase(); // Init DB on startup
        launch(args);
    }
}

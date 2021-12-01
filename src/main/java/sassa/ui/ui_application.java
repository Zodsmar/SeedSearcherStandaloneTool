package sassa.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sassa.Main;

public class ui_application extends Application {
    public void startGUI(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/sassa/fxml/layout.fxml"));
        BorderPane window = loader.load();

        Scene scene = new Scene(window);
        // scene.getStylesheets().add("/sassa/ui.css");
        // mainStage.setTitle("Sassa-" + VERSION);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

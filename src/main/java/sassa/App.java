package sassa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sassa.gui.UIController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App extends Application {

    public static String VERSION;
    public static boolean DEV_MODE = false;
    public static String fxmlFile = "/sassa/fxml/layout.fxml";

    public static void main(String... args){

        setVersion();
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        BorderPane vbox = loader.load();

        Scene scene = new Scene(vbox);
        mainStage.setTitle("Sassa-" + VERSION);
        mainStage.setScene(scene);
        mainStage.show();
    }


    /*
    Gets the version from gradle.properties (This way whatever version for the tool is set in gradle.properties
    is used across the entire project)
    */
    public static void setVersion() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("gradle.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            VERSION = prop.getProperty("version");
        } catch (IOException ex) {
            VERSION = "DEVELOPMENT";
            ex.printStackTrace();
        }
    }
}
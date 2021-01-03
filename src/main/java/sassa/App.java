package sassa;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App extends Application {

    public static String VERSION;
    public static boolean DEV_MODE = false;

    public static void main(String... args){

        setVersion();
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(Main.class.getResource("/sassa/fxml/layout.fxml"));
//        BorderPane vbox = loader.load();

//        Scene scene = new Scene(vbox);
        mainStage.setTitle("Sassa-" + VERSION);
//        mainStage.setScene(scene);
        mainStage.show();
//        fxmlController fxml = new fxmlController();
//        fxml.startSeedSearcher();
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
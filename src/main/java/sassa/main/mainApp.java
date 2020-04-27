package sassa.main;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import sassa.gui.fxmlController;
import sassa.util.Version;

import java.io.IOException;

public class mainApp extends Application {

    public static final String VERSION = "v0.5.0";
    public static boolean DEV_MODE = false;

	/*
	 Dev mode currently gives access to:

	 */

    public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException, ParseException {
        Version.registerSupportedVersions();
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/sassa/fxml/layout.fxml"));
        BorderPane vbox = loader.load();

        Scene scene = new Scene(vbox);
        mainStage.setTitle("Sassa-" + VERSION);
        mainStage.setScene(scene);
        mainStage.show();
        fxmlController fxml = new fxmlController();
        fxml.startSeedSearcher();
    }
}

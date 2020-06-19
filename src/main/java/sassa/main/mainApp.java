package sassa.main;

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

    public static final String VERSION = "v0.6.0";
    public static boolean DEV_MODE = false;

	/*
	 Dev mode currently gives access to:

	 */

    public static void main(String... args) throws IOException, ParseException {
        Version.registerSupportedVersions();
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/sassa/fxml/layout.fxml"));
        BorderPane vbox = loader.load();

        //Random test = new Random();

        // long seed = test.nextLong();

        // get biomes via BiomeUtils
//        OverworldBiomeSource overworld = new OverworldBiomeSource(MCVersion.v1_15, 1);
//        overworld.build();
//        System.out.println(overworld.getBiome(0,0,0) == Biome.OCEAN);
//        Iterator regIt = Biome.REGISTRY.entrySet().iterator();
//        while(regIt.hasNext()){
//            Map.Entry mapElement = (Map.Entry)regIt.next();
//            Biome b = (Biome) mapElement.getValue();
//            System.out.println(mapElement.getKey() + " : " + b.getName().toUpperCase());
//        }

        Scene scene = new Scene(vbox);
        mainStage.setTitle("Sassa-" + VERSION);
        mainStage.setScene(scene);
        mainStage.show();
        fxmlController fxml = new fxmlController();
        fxml.startSeedSearcher();
    }
}

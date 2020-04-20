package sassa.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import sassa.gui.GenerateGUI;
import sassa.gui.fxmlController;
import sassa.util.Util;
import sassa.util.Version;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import sassa.gui.GUI;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {


//	public static final int BACK_FRAME_WIDTH = 924;
//	public static final int BACK_FRAME_HEIGHT = 515;
	
	public static final int BACK_FRAME_WIDTH = Math.max(924, (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.9));
	public static final int BACK_FRAME_HEIGHT = Math.max(515, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.8));
	
	public static final int CONSOLE_WIDTH = Math.max((int) (BACK_FRAME_WIDTH * 0.3), 240);
	public static final int CONSOLE_HEIGHT = (int) (BACK_FRAME_HEIGHT * 0.8);
	
	public static final int FRAME_WITHOUT_CONSOLE_WIDTH = BACK_FRAME_WIDTH - CONSOLE_WIDTH;
	public static final int FRAME_WITHOUT_CONSOLE_HEIGHT = BACK_FRAME_HEIGHT - CONSOLE_HEIGHT;
	
	public static final int FRAME_SCROLL_BAR_WIDTH = (BACK_FRAME_WIDTH - CONSOLE_WIDTH) - 18;
	public static final int FRAME_SCROLL_BAR_HEIGHT = BACK_FRAME_HEIGHT - 93;
	
	public static final String VERSION = "v0.4.4";
	public static boolean DEV_MODE = false;
	public static boolean RANDOM_SEEDS = true;
	/*
	 Quick test of git connection through VS Code

	 Dev mode currently gives access to:
	 	- Structures
	 	- New box selection for included/excluded biomes
	 */
	
	public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException, ParseException {
		Version.registerSupportedVersions();
		//GenerateGUI.showGenerateGUI();
		Application.launch(args);
		new GUI().startSeedSearcher();
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/sassa/fxml/layout.fxml"));
		BorderPane vbox = loader.load();

		Scene scene = new Scene(vbox);
		mainStage.setTitle("Sassa: " + VERSION);
		mainStage.setScene(scene);
		mainStage.show();
	}
}

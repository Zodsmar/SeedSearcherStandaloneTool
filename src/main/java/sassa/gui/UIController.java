package sassa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.*;

public class UIController implements Initializable {

    String[] worldTypes = {
            "DEFAULT/AMP", "LARGE BIOMES"
    };

    @FXML
    private Text cRejSeedCount;

    @FXML
    private Text tRejSeedCount;

    @FXML
    private Button startBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Text timeElapsed;

    @FXML
    private ComboBox<String> mcVersions;


    @FXML
    private TextField seedsToFind;

    @FXML
    private TextField searchRadius;

    @FXML
    private CheckBox devMode;

    @FXML
    private CheckBox bedrockMode;

    @FXML
    private Text bedrockWarning;

    @FXML
    private CheckBox randomSeed;

    @FXML
    private Pane randomSeedPane;

    @FXML
    private CheckBox setSeed;

    @FXML
    private Pane setSeedPane;

    @FXML
    private Button seedFileBrowser;

    @FXML
    private Label seedFileText;

    @FXML
    private Pane worldTypePane;

    @FXML
    private TextField minSeed;

    @FXML
    private TextField maxSeed;

    @FXML
    private TextArea console;

    @FXML
    private Text notificationLabel;

    @FXML
    private Tab biomesTab;

    @FXML
    private Tab structuresTab;

    @FXML
    private Text sequencedSeed;

    @FXML
    private ImageView paypalDonate;

    @FXML
    private Button saveConsole;

    @FXML
    private CheckBox autoSaveConsole;

    @FXML
    private Button outputFileBrowser;

    @FXML
    private Label outputFileText;

    @FXML
    private ComboBox<String> worldType;

    //Get the grid in Biomes tab to dynamically build it.
    @FXML
    private GridPane biomesGrid;

    @FXML
    private GridPane structuresGrid;

    @FXML
    private GridPane biomeSetsGrid;

    @FXML
    private Slider amountOfCores;

    @FXML
    private Text coresAmount;

    @FXML
    private TextField incrementer;

    @FXML
    private TextField biomePrecision;

    @FXML
    private CheckBox shadowMode;

    @FXML
    private Button resetUIBtn;

    @FXML
    private Pane spawnPointPane;

    @FXML
    private CheckBox spawnPoint;

    @FXML
    private TextField xCoordSpawn;

    @FXML
    private TextField zCoordSpawn;

    @FXML
    private TextField marginOfError;

    String[] include_exclude_txt = {"", "Include", "Exclude"};
//    Singleton singleton = Singleton.getInstance();
//    MCVersion defaultVersion = MCVersion.v1_16_4;
//    Util util;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        //This gets called as soon as the app starts

    }
}

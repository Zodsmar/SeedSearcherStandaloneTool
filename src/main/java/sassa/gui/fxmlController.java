package sassa.gui;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.MCVersion;
import sassa.searcher.Searcher;
import sassa.util.Singleton;

import sassa.util.Version;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class fxmlController implements Initializable {

    @FXML
    private Text cRejSeedCount;

    @FXML
    private Text tRejSeedCount;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Text timeElapsed;

    @FXML
    private ComboBox<String> mcVersions;

    @FXML
    private TextField mcPath;

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
    private Button directoryBrowser;

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


    String[] include_exclude_txt = {"", "Include", "Exclude"};
    Singleton singleton = Singleton.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singleton.setBiomesGridPane(biomesGrid);
        singleton.setConsole(console);
        singleton.setMinecraftVersion(Version.getDefaultVersion());
        singleton.setMCPath(mcPath);
        singleton.setCRejSeed(cRejSeedCount);
        singleton.setTRejSeed(tRejSeedCount);
        singleton.setSeedCount(seedsToFind);
        singleton.setSequenceSeed(sequencedSeed);
        singleton.setStructureGridPane(structuresGrid);
        singleton.setBiomeSetsGridPane(biomeSetsGrid);
        singleton.setAutoSave(autoSaveConsole);
        singleton.setController(this);
        singleton.setWorldType(worldType);


//        util = new Util();
//        guiCollector = new guiCollector();
        startBtn.setOnAction(buttonHandler);
        pauseBtn.setOnAction(buttonHandler);
        clearBtn.setOnAction(buttonHandler);
        bedrockMode.setOnAction(buttonHandler);
        randomSeed.setOnAction(buttonHandler);
        devMode.setOnAction(buttonHandler);
        mcVersions.setOnAction(buttonHandler);
        directoryBrowser.setOnAction(buttonHandler);
        saveConsole.setOnAction(buttonHandler);

//        mcVersions.setItems(FXCollections
//                .observableArrayList(versions));
        mcVersions.setValue(Version.getDefaultVersion());

        //worldType.setItems(FXCollections.observableArrayList(worldTypes));
        singleton.getWorldType().setValue("DEFAULT");
    }

    EventHandler<ActionEvent> buttonHandler = new EventHandler<javafx.event.ActionEvent>() {
        @Override
        public void handle(javafx.event.ActionEvent e) {
//            if (e.getSource() == devMode) {
////                Main.DEV_MODE = !Main.DEV_MODE;
//
//            } else if (e.getSource() == randomSeed) {
//                if(randomSeed.isSelected()){
//                    randomSeedPane.setVisible(false);
//                } else {
//                    randomSeedPane.setVisible(true);
//                }
//                RANDOM_SEEDS = !RANDOM_SEEDS;
//            } else if (e.getSource() == bedrockMode){
//                if(bedrockMode.isSelected()){
//                    BEDROCK = true;
//                    bedrockWarning.setVisible(true);
//                    structuresTab.setDisable(true);
//                    singleton.getWorldType().setValue("DEFAULT");
//                    worldTypePane.setDisable(true);
//                } else {
//                    BEDROCK = false;
//                    bedrockWarning.setVisible(false);
//                    structuresTab.setDisable(false);
//                    worldTypePane.setDisable(false);
//                }
//            } else if (e.getSource() == startBtn) {
//                try {
//                    toggleRunning();
//                } catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
//                        UnknownBiomeIndexException e1) {
//                    e1.printStackTrace();
//                }
//            } else if (e.getSource() == pauseBtn) {
//                togglePause();
//            } else if (e.getSource() == clearBtn) {
//                try {
//                    reset();
//                } catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
//                        UnknownBiomeIndexException e1) {
//                    e1.printStackTrace();
//                }
//            } else if (e.getSource() == mcVersions) {
//                String selected = mcVersions.getSelectionModel().getSelectedItem();
//                minecraftVersion = selected;
//                Singleton.getInstance().setMinecraftVersion(minecraftVersion);
//                System.out.println("Version: "+minecraftVersion+":"+mcVersions.getSelectionModel().getSelectedIndex());
//                clearGridPane(biomesGrid);
//                clearGridPane(structuresGrid);
//                clearGridPane(biomeSetsGrid);
//                buildGridPane(biomesGrid, "Biomes");
//                buildGridPane(structuresGrid, "Structures");
//                buildGridPane(biomeSetsGrid, "Biome Sets");
//
//            } else if(e.getSource() == directoryBrowser){
//                util.chooseDirectory(outputFileText);
//            } else if(e.getSource() == saveConsole){
//                util.appendToFile(Singleton.getInstance().getOutputFile(), console.getText());
//            }
        }

    };
    public static final Village VILLAGE = new Village(MCVersion.v1_15);
    public static final BuriedTreasure BURIED_TREASURE = new BuriedTreasure(MCVersion.v1_15);
    public static final Igloo IGLOO = new Igloo(MCVersion.v1_15);
    public static final PillagerOutpost PILLAGER_OUTPOST = new PillagerOutpost(MCVersion.v1_15);
    public static final DesertPyramid DESERT_PYRAMID = new DesertPyramid(MCVersion.v1_15);
    public static final JunglePyramid JUNGLE_PYRAMID = new JunglePyramid(MCVersion.v1_15);
    public static final OceanRuins OCEAN_RUINS = new OceanRuins(MCVersion.v1_15);
    public static final Mansion MANSION = new Mansion(MCVersion.v1_15);
    public static final Mineshaft MINESHAFT = new Mineshaft(MCVersion.v1_15, Mineshaft.Type.EITHER);
    public static final Monument MONUMENT = new Monument(MCVersion.v1_15);
    public static final Shipwreck SHIPWRECK = new Shipwreck(MCVersion.v1_15);
    public static final SwampHut SWAMP_HUT = new SwampHut(MCVersion.v1_15);

    public static final BastionRemnant BASTION_REMNANT = new BastionRemnant(MCVersion.v1_16);
    public static final EndCity END_CITY = new EndCity(MCVersion.v1_15);

    //Fortress doesnt use regions pre 1.16
    public static final Fortress FORTRESS = new Fortress(MCVersion.v1_16);
    public static final NetherFossil NETHER_FOSSIL = new NetherFossil(MCVersion.v1_16);
    public static final RuinedPortal RUINED_PORTAL = new RuinedPortal(MCVersion.v1_16);

    //TODO: Will add strongholds later
    //public static final Stronghold STRONGHOLD = new Stronghold(MCVersion.v1_15);

    public void startSeedSearcher() throws IOException {

        long worldSeed = 4320562085990449695L;
        int searchRadius = 1000;
        int incrementer = 1;

        long startTime = System.nanoTime();

        ArrayList<Biome> biomesToFind = new ArrayList<>();
        biomesToFind.add(Biome.BIRCH_FOREST);
        biomesToFind.add(Biome.OCEAN);
        biomesToFind.add(Biome.FOREST);
        biomesToFind.add(Biome.FLOWER_FOREST);
        biomesToFind.add(Biome.MUSHROOM_FIELDS);

        ArrayList<Biome.Category> cat = new ArrayList<>();
        cat.add(Biome.Category.FOREST);
        cat.add(Biome.Category.ICY);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = biomeSearcher.findBiomeFromCategory(searchRadius, new Random().nextLong(), cat, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            //System.out.println(count++);
//        } while(!b);


        ArrayList<RegionStructure<?, ?>> structuresToFind = new ArrayList<>();
        structuresToFind.add(VILLAGE);
        structuresToFind.add(MONUMENT);
        structuresToFind.add(DESERT_PYRAMID);
        structuresToFind.add(PILLAGER_OUTPOST);
        structuresToFind.add(IGLOO);
        structuresToFind.add(SWAMP_HUT);
        //structuresToFind.add(MANSION);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = biomeSearcher.findBiome(searchRadius, new Random().nextLong(), Biome.MUSHROOM_FIELDS, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            System.out.println(count++);
//        } while(!b);

        Searcher.searchRandomly(searchRadius, structuresToFind, biomesToFind, "OVERWORLD", incrementer, 16);

        //biomeSearcher.findBiome(searchRadius, worldSeed, Biome.PLAINS, "OVERWORLD", incrementer);
//
        //structureSearcher.findStructure(searchRadius, worldSeed, VILLAGE, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MONUMENT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, DESERT_PYRAMID, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, PILLAGER_OUTPOST, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, IGLOO, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, SWAMP_HUT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MANSION, "OVERWORLD");



        //structureSearcher.findStructureRandomly(searchRadius, structuresToFind, "OVERWORLD");

        //structureSearcher.findStructure(searchRadius, worldSeed, FORTRESS, "NETHER");
        //structureSearcher.findStructure(searchRadius, worldSeed, END_CITY, "END");
        //structureSearcher.findMineshaft(searchRadius, worldSeed, MINESHAFT);
        long elapsedTime = System.nanoTime() - startTime;

        System.out.println(elapsedTime/1000000 + "/ms");


        //structureSearcher.findMineshaft(1024, 4320562085990449695L, MCVersion.v1_15, Mineshaft.Type.EITHER);
        //updateDisplay();
        //util.console("Welcome to SeedTool!");
        //util.console("Please select at least one biome before searching!");
    }

    public void donate(){
        // util.openWebPage("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9E3YQAKQWC34&currency_code=CAD&source=url");
    }
}

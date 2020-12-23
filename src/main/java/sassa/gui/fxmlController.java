package sassa.gui;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.MCVersion;
import sassa.searcher.SearchingThread;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Structures;
import sassa.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class fxmlController implements Initializable {

    private static final int DELAY = 0;
    static Timer timer;
    public static boolean running;

    private static long startTime; // TODO use this in the future to tell user when they started
    private static long elapsedTime;

    private static ArrayList<Thread> currentThreads = new ArrayList<>();

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
    private CheckBox shadowMode;

    @FXML
    private Button resetUIBtn;

    String[] include_exclude_txt = {"", "Include", "Exclude"};
    Singleton singleton = Singleton.getInstance();
    MCVersion defaultVersion = MCVersion.v1_16;
    Util util;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singleton.setBiomesGridPane(biomesGrid);
        singleton.setConsole(console);
        singleton.setMinecraftVersion(defaultVersion);
        singleton.setCRejSeed(cRejSeedCount);
        singleton.setTRejSeed(tRejSeedCount);
        singleton.setSeedCount(seedsToFind);
        singleton.setSequenceSeed(sequencedSeed);
        singleton.setStructureGridPane(structuresGrid);
        singleton.setBiomeSetsGridPane(biomeSetsGrid);
        singleton.setAutoSave(autoSaveConsole);
        singleton.setController(this);
        singleton.setWorldType(worldType);
        singleton.setAmountOfCores(amountOfCores);
        singleton.setCoresAmount(coresAmount);
        singleton.setShadowMode(shadowMode);
        singleton.setIncrementer(incrementer);
        singleton.setBedrockMode(bedrockMode);
        singleton.setMaxSeed(maxSeed);
        singleton.setMinSeed(minSeed);
        singleton.setRandomSeed(randomSeed);
        singleton.setSetSeed(setSeed);

        amountOfCores.setMax(Runtime.getRuntime().availableProcessors());
        coresAmount.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        amountOfCores.valueProperty()
                )
        );

        startBtn.setOnAction(buttonHandler);
        clearBtn.setOnAction(buttonHandler);
        bedrockMode.setOnAction(buttonHandler);
        randomSeed.setOnAction(buttonHandler);
        setSeed.setOnAction(buttonHandler);
        seedFileBrowser.setOnAction(buttonHandler);
        devMode.setOnAction(buttonHandler);
        mcVersions.setOnAction(buttonHandler);
        outputFileBrowser.setOnAction(buttonHandler);
        saveConsole.setOnAction(buttonHandler);
        resetUIBtn.setOnAction(buttonHandler);


        ArrayList<String> versions = new ArrayList<>();
        for(MCVersion v : MCVersion.values()){
            if(v.release > 12){
                versions.add(v.name);
            }
        }
        mcVersions.setItems(FXCollections
                .observableArrayList(versions));
        mcVersions.setValue(defaultVersion.name);

        worldType.setItems(FXCollections.observableArrayList(worldTypes));
        singleton.getWorldType().setValue("DEFAULT/AMP");

        util = new Util();

        rebuildUI(defaultVersion);
    }

    EventHandler<ActionEvent> buttonHandler = new EventHandler<javafx.event.ActionEvent>() {
        @Override
        public void handle(javafx.event.ActionEvent e) {
            if (e.getSource() == devMode) {
//                Main.DEV_MODE = !Main.DEV_MODE;

            } else if (e.getSource() == randomSeed) {
                if(randomSeed.isSelected()){
                    randomSeedPane.setVisible(false);
                    setSeed.setSelected(false);
                    setSeedPane.setVisible(false);
                    amountOfCores.setDisable(false);
                } else {
                    randomSeedPane.setVisible(true);
                    amountOfCores.setValue(1);
                    amountOfCores.setDisable(true);
                }
                //RANDOM_SEEDS = !RANDOM_SEEDS;
            } else if (e.getSource() == setSeed) {
                if(setSeed.isSelected()){
                    setSeedPane.setVisible(true);
                    randomSeed.setSelected(false);
                    randomSeedPane.setVisible(false);
                    amountOfCores.setValue(1);
                    amountOfCores.setDisable(true);
                } else {
                    setSeedPane.setVisible(false);
                    amountOfCores.setDisable(false);
                }
            } else if (e.getSource() == bedrockMode){
                if(bedrockMode.isSelected()){
                    //BEDROCK = true;
                    bedrockWarning.setVisible(true);
                    structuresTab.setDisable(true);
                    singleton.getWorldType().setValue("DEFAULT/AMP");
                    worldTypePane.setDisable(true);
                } else {
                   // BEDROCK = false;
                    bedrockWarning.setVisible(false);
                    structuresTab.setDisable(false);
                    worldTypePane.setDisable(false);
                }
            } else if (e.getSource() == startBtn) {
                try {
                    toggleRunning();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            } else if (e.getSource() == clearBtn) {
                try {
                    reset();
                } catch (InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == mcVersions) {
                String selected = mcVersions.getSelectionModel().getSelectedItem();
                MCVersion version = MCVersion.fromString(selected);
                Singleton.getInstance().setMinecraftVersion(version);
                System.out.println("Version: "+selected+":"+mcVersions.getSelectionModel().getSelectedIndex());
                rebuildUI(version);

            } else if(e.getSource() == outputFileBrowser) {
                util.chooseDirectory(outputFileText, "output");
            } else if(e.getSource() == seedFileBrowser) {
                util.chooseDirectory(seedFileText, "seed");
            } else if(e.getSource() == saveConsole){
                util.appendToFile(Singleton.getInstance().getOutputFile(), console.getText());
            } else if (e.getSource() == resetUIBtn) {
                rebuildUI(singleton.getMinecraftVersion());
            }
        }

    };

    public void startSeedSearcher() {
        updateDisplay();
        util.console("Welcome to SeedTool!");
        util.console("Please select at least one biome before searching!");
    }

    void createNewThreads() throws IOException, InterruptedException {
        ArrayList<StructureProvider> structuresIN = GuiCollector.getStructures(structuresGrid, "Include");
        ArrayList<StructureProvider> structuresOUT = GuiCollector.getStructures(structuresGrid, "Exclude");
        ArrayList<Biome> biomesIN = GuiCollector.getBiomesFromUI(biomesGrid, "Include");
        ArrayList<Biome> biomesOUT = GuiCollector.getBiomesFromUI(biomesGrid, "Exclude");
        ArrayList<Biome.Category> categoriesIN = GuiCollector.getCategoryFromUI(biomeSetsGrid, "Include");
        ArrayList<Biome.Category> categoriesOUT = GuiCollector.getCategoryFromUI(biomeSetsGrid, "Exclude");
        if (structuresIN.size() == 0 && structuresOUT.size() == 0
                && biomesIN.size() == 0 && biomesOUT.size() == 0 //
                && categoriesIN.size() == 0 && categoriesOUT.size() == 0) {
            util.console("Select something to search...");
            toggleRunning();
            //print out the world seed (Plus possibly more information)
        } else {
            for(int i = 0; i < singleton.getAmountOfCores().getValue(); i++) {
                long startingStructureSeed = (long) Math.floor(Math.pow(2, 48)/singleton.getAmountOfCores().getValue() * i);
                long endStructureSeed = Math.min((long) Math.floor(Math.pow(2, 48)/singleton.getAmountOfCores().getValue() * (i+1)), 1L << 48);
                Thread t = new SearchingThread(startingStructureSeed, endStructureSeed, Integer.parseInt(searchRadius.getText()),structuresIN, structuresOUT, biomesIN, biomesOUT, categoriesIN, categoriesOUT);
                t.start();
                currentThreads.add(t);
            }
        }
    }

    private void initTimer() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDisplay();
            }
        },DELAY,1);
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (running) {
                timeElapsed.setText(util.getElapsedTimeHoursMinutesFromMilliseconds(System.currentTimeMillis() - elapsedTime));
                notificationLabel.setText("Running");

                if (cRejSeedCount != null) cRejSeedCount.setText("" + Variables.worldsSinceAccepted());
                if (tRejSeedCount != null) tRejSeedCount.setText("" + Variables.checkedWorlds());
            }
        });
    }

    private void toggleRunning() throws InterruptedException, IOException {
        if (running) {
            System.out.println("Shutting Down...");
            stop();
        } else {
            start();
        }

    }

    public boolean isRunning(){
        return running;
    }

    private void start() throws IOException, InterruptedException {
        startBtn.setText("Stop");
        Variables.reset();
        searchRadius.setEditable(false);
        seedsToFind.setEditable(false);
        incrementer.setEditable(false);
        startTime = System.currentTimeMillis();
        elapsedTime = System.currentTimeMillis();
        running = true;
        initTimer();
        createNewThreads();
    }

    public void stop(){
        running = false;
        for(Thread t : currentThreads) {
            if(t != null){
                t.interrupt();
            }
        }
        currentThreads = new ArrayList<>();

        searchRadius.setEditable(true);
        seedsToFind.setEditable(true);
        incrementer.setEditable(true);
        startBtn.setText("Start");
        notificationLabel.setText("Stopped");
        sequencedSeed.setText("0");
        if(timer != null)
            timer.cancel();


        util.console("---END OF SEARCH---");
    }

    private void reset() throws InterruptedException, IOException {

        stop();
        util.consoleWipe();
        timeElapsed.setText("00:00:00");
        startTime = System.currentTimeMillis();
        elapsedTime = System.currentTimeMillis();
        cRejSeedCount.setText("0");
        tRejSeedCount.setText("0");
        notificationLabel.setText("Offline");

        updateDisplay();
    }

    private ArrayList<String> generateBiomesUI(MCVersion version){

        ArrayList<String> validBiomes = new ArrayList<>();

        Iterator regIt = Biome.REGISTRY.entrySet().iterator();
        while(regIt.hasNext()){
            Map.Entry mapElement = (Map.Entry)regIt.next();
            Biome b = (Biome) mapElement.getValue();
            if(b.getVersion().release <= version.release){
                validBiomes.add(b.getName());
            }
        }
        return validBiomes;
    }

    private ArrayList<String> generateCategoryUI(){

        ArrayList<String> validCategory = new ArrayList<>();

       for(Biome.Category c : Biome.Category.values()){
           validCategory.add(c.getName());
       }
        return validCategory;
    }

    private ArrayList<String> generateStructuresUI(MCVersion version){

        ArrayList<String> validStructures = new ArrayList<>();

        Iterator<Map.Entry<String, StructureProvider>> it = Structures.STRUCTURE.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<String, StructureProvider> e = it.next();
            String name = e.getKey();
            StructureProvider struct = e.getValue();
            if(struct.getVersion().release <= version.release) {
                validStructures.add(name);
            }
        }
        return validStructures;
    }

    private void buildGridPane(GridPane grid, ArrayList<String> searchList, boolean textField){

        int k = 0;
        for (int i = 0; i < (searchList.size() / 3) + 1; i++) {
            for (int j = 0; j < 3; j++) {
                if (k < searchList.size()) {
                    VBox tempGrid = new VBox();
                    GridPane.setHgrow(tempGrid, Priority.ALWAYS);
                    GridPane.setVgrow(tempGrid, Priority.ALWAYS);
                    tempGrid.setAlignment(Pos.CENTER);
                    tempGrid.setSpacing(5);
                    grid.add(tempGrid, j, i);

                    Text tempText = new Text(searchList.get(k));
                    ComboBox<String> temp = new ComboBox<String>(FXCollections
                            .observableArrayList(include_exclude_txt));
                    tempGrid.getChildren().add(tempText);
                    tempGrid.getChildren().add(temp);
                    if(textField == true) {

                        TextField tempField = new TextField();
                        tempField.setMaxWidth(50);
                        tempField.setTooltip(new Tooltip("How many structures do you want to have? (Default if blank is 1 and its a minimum value)"));
                        tempGrid.getChildren().add(tempField);
                    }
                    k++;
                } else {
                    Pane empty = new Pane();
                    empty.setVisible(false);
                    grid.add(empty, j, i + 1);
                }
            }
        }
    }

    private void clearGridPane(GridPane pane){
        pane.getChildren().clear();
        pane.getColumnConstraints().clear();
        pane.getRowConstraints().clear();
    }

    private void rebuildUI(MCVersion version){
        clearGridPane(biomesGrid);
        clearGridPane(structuresGrid);
        clearGridPane(biomeSetsGrid);
        buildGridPane(biomesGrid, generateBiomesUI(version), false);
        buildGridPane(structuresGrid, generateStructuresUI(version),true);
        buildGridPane(biomeSetsGrid, generateCategoryUI(),false);
    }

    public void donate(){
        util.openWebPage("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9E3YQAKQWC34&currency_code=CAD&source=url");
    }
}

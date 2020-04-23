package sassa.gui;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.parsing.FormatException;
import javafx.collections.FXCollections;
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
import org.json.simple.parser.ParseException;
import sassa.main.BiomeSearcher;
import sassa.util.Singleton;
import sassa.util.Util;
import sassa.util.Version;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class fxmlController implements Initializable {

    private static final int DELAY = 0;
    static Timer timer;
    public static boolean running;
    public static boolean paused;
    private static long pausedTime;
    @SuppressWarnings("unused")
    private static long startTime; // TODO use this in the future to tell user when they started
    private static long elapsedTime;

    static Thread t;
    static boolean allowThreadToSearch = true;
    static BiomeSearcher r;

    public static String minecraftVersion = Version.V1_15_2;
    String[] versions = {
            /*1.15.x*/	Version.V1_15_2, Version.V1_15_1, Version.V1_15,
            /*1.14.x*/	Version.V1_14_4, Version.V1_14_3, Version.V1_14,
            /*1.13.x*/	Version.V1_13_2, Version.V1_13_1, Version.V1_13,
            /*1.12.x*/	Version.V1_12_2, Version.V1_12,
            /*1.11.x*/	Version.V1_11_2, Version.V1_11,
            /*1.10.x*/	Version.V1_10_2,
            /*1.9.x*/	Version.V1_9_4, Version.V1_9_2,
            /*1.8.x*/	Version.V1_8_9, Version.V1_8_3, Version.V1_8_1, Version.V1_8,
            /*1.7.x*/	Version.V1_7_10};
    ///*1.6.x*/	Version.V1_6_4};

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
    private TextField searchX;

    @FXML
    private TextField searchZ;

    @FXML
    private CheckBox devMode;

    @FXML
    private CheckBox findStructures;

    @FXML
    private CheckBox bedrockMode;

    @FXML
    private Text bedrockWarning;

    @FXML
    private CheckBox randomSeed;

    @FXML
    private Pane randomSeedPane;

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

    //Get the grid in Biomes tab to dynamically build it.
    @FXML
    private GridPane biomesGrid;

    @FXML
    private GridPane structuresGrid;

    @FXML
    private GridPane biomeSetsGrid;


    String[] include_exclude_txt = {"", "Include", "Exclude"};

    Util util;
    guiCollector guiCollector;
    Singleton singleton = Singleton.getInstance();

    public static boolean RANDOM_SEEDS = true;
    public static boolean BEDROCK = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        singleton.setBiomesGridPane(biomesGrid);
        singleton.setConsole(console);
        singleton.setMinecraftVersion(minecraftVersion);
        singleton.setMCPath(mcPath);
        singleton.setCRejSeed(cRejSeedCount);
        singleton.setTRejSeed(tRejSeedCount);
        singleton.setSeedCount(seedsToFind);
        singleton.setSequenceSeed(sequencedSeed);
        singleton.setStructureGridPane(structuresGrid);
        singleton.setController(this);


        util = new Util();
        guiCollector = new guiCollector();
        startBtn.setOnAction(buttonHandler);
        pauseBtn.setOnAction(buttonHandler);
        clearBtn.setOnAction(buttonHandler);
        findStructures.setOnAction(buttonHandler);
        bedrockMode.setOnAction(buttonHandler);
        randomSeed.setOnAction(buttonHandler);
        devMode.setOnAction(buttonHandler);
        mcVersions.setOnAction(buttonHandler);

        mcVersions.setItems(FXCollections
                .observableArrayList(versions));
        mcVersions.setValue(minecraftVersion);

        buildGridPane(biomesGrid, "Biomes");
        buildGridPane(structuresGrid, "Structures");
        buildGridPane(biomeSetsGrid, "Biome Sets");
    }

    EventHandler<javafx.event.ActionEvent> buttonHandler = new EventHandler<javafx.event.ActionEvent>() {
        @Override
        public void handle(javafx.event.ActionEvent e) {
            if(e.getSource() == findStructures) {
                if (findStructures.isSelected()) {
                    structuresTab.setDisable(false);
                } else {
                    structuresTab.setDisable(true);
                }
            }

            if (e.getSource() == devMode) {
//                Main.DEV_MODE = !Main.DEV_MODE;
//                initialize();
            } else if (e.getSource() == randomSeed) {
                if(randomSeed.isSelected()){
                    randomSeedPane.setVisible(false);
                } else {
                    randomSeedPane.setVisible(true);
                }
                RANDOM_SEEDS = !RANDOM_SEEDS;
            } else if (e.getSource() == bedrockMode){
                if(bedrockMode.isSelected()){
                    BEDROCK = true;
                    bedrockWarning.setVisible(true);
                    structuresTab.setDisable(true);
                    findStructures.setDisable(true);
                } else {
                    BEDROCK = false;
                    bedrockWarning.setVisible(false);
                    findStructures.setDisable(false);
                    structuresTab.setDisable(false);
                }
            } else if (e.getSource() == startBtn) {
                try {
                    toggleRunning();
                } catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
                        UnknownBiomeIndexException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == pauseBtn) {
                togglePause();
            } else if (e.getSource() == clearBtn) {
                try {
                    reset();
                } catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
                        UnknownBiomeIndexException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == mcVersions) {
                String selected = mcVersions.getSelectionModel().getSelectedItem();
                minecraftVersion = selected;
                Singleton.getInstance().setMinecraftVersion(minecraftVersion);
                System.out.println("Version: "+minecraftVersion+":"+mcVersions.getSelectionModel().getSelectedIndex());
                clearGridPane(biomesGrid);
                clearGridPane(structuresGrid);
                clearGridPane(biomeSetsGrid);
                buildGridPane(biomesGrid, "Biomes");
                buildGridPane(structuresGrid, "Structures");
                buildGridPane(biomeSetsGrid, "Biome Sets");

                //initialize();
            }
        }

    };

    BiomeSearcher createNewThread() throws IOException, FormatException, MinecraftInterfaceCreationException {
        r = new BiomeSearcher(
                minecraftVersion,
                Integer.parseInt(searchX.getText()),
                Integer.parseInt(searchZ.getText()),
                Integer.parseInt(seedsToFind.getText()),
                Long.parseLong(minSeed.getText()),
                Long.parseLong(maxSeed.getText()),
                RANDOM_SEEDS,
                BEDROCK);
        return r;
    }

    public void donate(){
        util.openWebPage("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9E3YQAKQWC34&currency_code=CAD&source=url");
    }

    public void startSeedSearcher() throws IOException, FormatException, MinecraftInterfaceCreationException {
        updateDisplay();
        util.console("Welcome to SeedTool!");
        util.console("Please select at least one biome before searching!");
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
        if (!paused && running) {
            timeElapsed.setText(util.getElapsedTimeHoursMinutesFromMilliseconds(System.currentTimeMillis() - elapsedTime));
            notificationLabel.setText("Running");
        } else if(paused) {
            notificationLabel.setText("Paused");
        }

    }

    private void toggleRunning() throws InterruptedException, IOException, FormatException,
            MinecraftInterfaceCreationException, UnknownBiomeIndexException {
        allowThreadToSearch = true;
        if (running) {
            System.out.println("Shutting Down...");
            stop();
        } else {
            if (allowThreadToSearch) {
                start();
            } else {
                stop();
            }
        }

    }

    public boolean isRunning(){
        return running;
    }

    public boolean isStructureSearching(){
        return findStructures.isSelected();
    }

    private void start() throws IOException, FormatException, MinecraftInterfaceCreationException {
        startBtn.setText("Stop");
        searchX.setEditable(false);
        searchZ.setEditable(false);
        seedsToFind.setEditable(false);
        startTime = System.currentTimeMillis();
        elapsedTime = System.currentTimeMillis();
        running = true;
        initTimer();
        t = new Thread(createNewThread());
        t.start();
    }

    public void stop() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException {
        searchX.setEditable(true);
        searchZ.setEditable(true);
        seedsToFind.setEditable(true);
        startBtn.setText("Start");
        pauseBtn.setText("Pause");
        running = false;
        notificationLabel.setText("Stopped");
        sequencedSeed.setText("0");
        if(timer != null)
        timer.cancel();
        if (t != null) t.interrupt();
    }

    private void togglePause() {
        if (!running) {
            util.console("Cannot pause when you aren't running!");
        } else {
            paused = !paused;
            String text = (paused) ? "Paused" : "Pause";

            if (paused) {
                pausedTime = System.currentTimeMillis();
               timer.cancel();
            } else {
                elapsedTime += System.currentTimeMillis() - pausedTime;
                initTimer();

                //startTime = timeAtPause;
            }
            pauseBtn.setText(text);
            updateDisplay();
        }
    }

    public boolean isPaused(){
        return paused;
    }
    private void reset() throws InterruptedException, IOException, FormatException,
            MinecraftInterfaceCreationException, UnknownBiomeIndexException {
        if (paused) {
            togglePause();
        }
        stop();
        util.consoleWipe();
        timeElapsed.setText("00:00:00");
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        elapsedTime = System.currentTimeMillis();
        cRejSeedCount.setText("0");
        tRejSeedCount.setText("0");
        notificationLabel.setText("Offline");

        updateDisplay();
    }

    private void buildGridPane(GridPane grid, String searchName){
        ArrayList<String> searchingList = null;
        try {
            searchingList = util.createSearchLists(searchName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int k = 0;
        for (int i = 0; i < (searchingList.size() / 3) + 1; i++) {
            for (int j = 0; j < 3; j++) {
                if (k < searchingList.size()) {
                    VBox tempGrid = new VBox();
                    GridPane.setHgrow(tempGrid, Priority.ALWAYS);
                    GridPane.setVgrow(tempGrid, Priority.ALWAYS);
                    tempGrid.setAlignment(Pos.CENTER);
                    grid.add(tempGrid, j, i + 1);

                    Text tempText = new Text(searchingList.get(k));
                    ComboBox<String> temp = new ComboBox<String>(FXCollections
                            .observableArrayList(include_exclude_txt));
                    tempGrid.getChildren().add(tempText);
                    tempGrid.getChildren().add(temp);

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



}

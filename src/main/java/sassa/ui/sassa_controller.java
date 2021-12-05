package sassa.ui;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.version.MCVersion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import sassa.Main;
import sassa.enums.BiomeListType;
import sassa.enums.SearchType;
import sassa.enums.SpawnType;
import sassa.enums.WorldType;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.models.features.Feature_Registry;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class sassa_controller implements Initializable {

    List<String> inex = Arrays.asList(" ", "INCLUDE", "EXCLUDE");
    List<String> in = Arrays.asList(" ", "INCLUDE");

    @FXML
    ChoiceBox search_type, spawn_type, world_type, minecraft_version;

    @FXML
    TextField biome_precision_input, search_radius_input, incrementer_input, seeds_to_find_input, range_start_seed_input, range_end_seed_input;

    @FXML
    Slider thread_slider;

    @FXML
    Label thread_number;

    @FXML
    ScrollPane seed_scrollpane;

    @FXML
    VBox console_vbox, set_seed_option_panel, range_seed_option_panel;

    @FXML
    FlowPane biomes_grid, biomeset_grid, structure_grid;

    @FXML
    public Button start_button;

    @FXML
    public void startButton() {
        System.out.println(start_button.getText());
        if (start_button.getText().equals("Start")) {
            start_button.setText("Stop");
            Main.beginThreads();
        } else if (start_button.getText().equals("Stop")) {
            stopButton();
        }
    }

    public void stopButton() {
        start_button.setText("Start");
        Main.stopAllThreads();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        try {
            setupChoiceBoxes(search_type, Searcher_Model.class.getMethod("setSearchType", SearchType.class));

            setupChoiceBoxes(world_type, Searcher_Model.class.getMethod("setWorldType", WorldType.class));
            setupChoiceBoxes(spawn_type, Searcher_Model.class.getMethod("setSpawnType", SpawnType.class));
            setupChoiceBoxes(minecraft_version, Searcher_Model.class.getMethod("setSelectedVersion", MCVersion.class));

            setupTextFields(biome_precision_input, Searcher_Model.class.getMethod("setBiomePrecision", int.class));
            setupTextFields(search_radius_input, Searcher_Model.class.getMethod("setSearchRadius", int.class));
            setupTextFields(incrementer_input, Searcher_Model.class.getMethod("setIncrementer", int.class));
            setupTextFields(seeds_to_find_input, Searcher_Model.class.getMethod("setSeedsToFind", int.class));
            setupTextFields(range_start_seed_input, Searcher_Model.class.getMethod("setStartRange", long.class));
            setupTextFields(range_end_seed_input, Searcher_Model.class.getMethod("setEndRange", long.class));

            setupSlider(thread_slider, thread_number, Searcher_Model.class.getMethod("setThreadsToUse", int.class));

            setupBiomePane(biomes_grid);
            setupStructureGrid(structure_grid);
            setupBiomeSetGrid(biomeset_grid);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        rebuild();
    }

    void rebuild() {

        updateTextFields(biome_precision_input, Main.defaultModel.getBiomePrecision());
        updateTextFields(search_radius_input, Main.defaultModel.getSearchRadius());
        updateTextFields(incrementer_input, Main.defaultModel.getIncrementer());
        updateTextFields(seeds_to_find_input, Main.defaultModel.getSeedsToFind());
        updateTextFields(range_start_seed_input, Main.defaultModel.getStartRange());
        updateTextFields(range_end_seed_input, Main.defaultModel.getStartRange());

        updateChoiceBox(search_type, Main.defaultModel.getSearchType());
        updateChoiceBox(world_type, Main.defaultModel.getWorldType());
        updateChoiceBox(spawn_type, Main.defaultModel.getSpawnType());
        updateChoiceBox(minecraft_version, Main.defaultModel.getSelectedVersion());

        updateSlider(thread_slider, thread_number, Main.defaultModel.getThreadsToUse());
    }


    //region SEED

    public void spawnSeed(String seed) throws IOException {

//        HBox parent = new HBox();
//        //label
//        VBox labelparent = new VBox();
//        Label label = new Label();
//
//        label.setText(seed);
//
//        labelparent.getChildren().add(label);
//        //butons
//        VBox buttonParent = new VBox();
//        HBox buttonrow = new HBox();
//        Button clipboard = new Button();
//        Button thumbnail = new Button();
//        buttonrow.getChildren().add(clipboard);
//        buttonrow.getChildren().add(thumbnail);
//        buttonParent.getChildren().add(buttonrow);
//
//        parent.getChildren().add(labelparent);
//        parent.getChildren().add(buttonParent);
//
//        console_vbox.getChildren().add(parent);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("/sassa/fxml/seed_component.fxml"));
                Node root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                seeddisplay_controller controller = loader.getController();

                controller.setSeed_label(seed);
                console_vbox.getChildren().add(root);
                seed_scrollpane.setVvalue(1);
            }
        });


    }

    //end region

    //region UPDATE UI

    <E> void updateTextFields(TextField textField, E defaultValue) {
        textField.setText(String.valueOf(defaultValue));
    }

    <E> void updateChoiceBox(ChoiceBox choiceBox, E defaultValue) {
        choiceBox.setValue(defaultValue);
        //System.out.println("   ChoiceBox.getValue(): " + choiceBox.getValue());
    }

    void updateSlider(Slider slider, Label label, int value) {
        slider.adjustValue(value);
        label.setText(String.valueOf(value));
    }
    //endregion

    //region SETUP UI

    void setupBiomeSetGrid(FlowPane flow) {
        //List<String> allBiomesAsStrings = new ArrayList<>();
        Biome.Category[] allBiomes = Biome.Category.values();
        //Biomes.REGISTRY.values().forEach(biome -> allBiomesAsStrings.add(biome.getName()));
        for (int i = 0; i < Biome.Category.values().length; i++) {

            Biome.Category curCategory = allBiomes[i];

            VBox tempVBox = new VBox();
            tempVBox.setAlignment(Pos.CENTER);
            tempVBox.setPrefWidth(200);
            tempVBox.setPrefHeight(75);
            tempVBox.setSpacing(10);


            Label tempText = new Label(curCategory.getName());
            ComboBox<String> temp = new ComboBox<>(FXCollections.observableArrayList(inex));
            temp.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal == "INCLUDE") {
                        Main.defaultModel.getBiomeSetList().addBiomeSetListFromCategory(curCategory, BiomeListType.INCLUDED);
                    }
                    if (newVal == "EXCLUDE") {
                        Main.defaultModel.getBiomeSetList().addBiomeSetListFromCategory(curCategory, BiomeListType.EXCLUDED);
                    }
                    if (newVal == " ") {
                        Main.defaultModel.getBiomeSetList().removeBiomeSetListFromCategory(curCategory);
                    }
                }
            });


            tempVBox.getChildren().add(tempText);
            tempVBox.getChildren().add(temp);

            flow.setVgap(5);
            flow.getChildren().add(tempVBox);
        }
    }

    void setupStructureGrid(FlowPane flow) {


        Feature_Registry.REGISTRY.forEach((name, factory) -> {
            VBox tempVBox = new VBox();
            tempVBox.setAlignment(Pos.CENTER);
            tempVBox.setPrefWidth(200);
            tempVBox.setPrefHeight(125);
            tempVBox.setSpacing(10);

            Label tempText = new Label(name);
            ComboBox<String> temp = new ComboBox<>(FXCollections.observableArrayList(in));

            TextField tempField = new TextField();
            tempField.setMaxWidth(50);
            tempField.setTooltip(new Tooltip("How many structures do you want to have? (Default if blank is 1 and its a minimum value)"));


            temp.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal == "INCLUDE") {
                        Main.defaultModel.getIncludedFeatures().addFeature(new Feature_Model(name, parseToInt(tempField.getText())));
                    }
//                        if (newVal == "EXCLUDE") {
//                            Main.defaultModel.get().addFeature(new Feature_Model(name, parseToInt(tempField.getText())));
//                        }
                    if (newVal == " ") {
                        Main.defaultModel.getIncludedFeatures().removeFeature(name);
                    }
                }
            });
            tempField.textProperty().addListener((obs, oldText, newText) -> {
                Feature_Model model = Main.defaultModel.getIncludedFeatures().getFeatureModelFromName(name);
                if (model != null) {
                    model.setAmount(parseToInt(tempField.getText()));
                }
            });

            tempVBox.getChildren().add(tempText);
            tempVBox.getChildren().add(temp);
            tempVBox.getChildren().add(tempField);

            flow.setVgap(5);
            flow.getChildren().add(tempVBox);
        });
    }

    void setupBiomePane(FlowPane flow) {
        //List<String> allBiomesAsStrings = new ArrayList<>();
        Object[] allBiomes = Biomes.REGISTRY.values().toArray();
        //Biomes.REGISTRY.values().forEach(biome -> allBiomesAsStrings.add(biome.getName()));
        for (int i = 0; i < Biomes.REGISTRY.size(); i++) {

            Biome curBiome = (Biome) allBiomes[i];

            VBox tempVBox = new VBox();
            tempVBox.setAlignment(Pos.CENTER);
            tempVBox.setPrefWidth(200);
            tempVBox.setPrefHeight(75);
            tempVBox.setSpacing(10);


            Label tempText = new Label(((Biome) allBiomes[i]).getName());
            ComboBox<String> temp = new ComboBox<>(FXCollections.observableArrayList(inex));
            temp.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal == "INCLUDE") {
                        Main.defaultModel.getBiomeList().addBiome(curBiome, BiomeListType.INCLUDED);
                    }
                    if (newVal == "EXCLUDE") {
                        Main.defaultModel.getBiomeList().addBiome(curBiome, BiomeListType.EXCLUDED);
                    }
                    if (newVal == " ") {
                        Main.defaultModel.getBiomeList().removeBiome(curBiome);
                    }
                }
            });


            tempVBox.getChildren().add(tempText);
            tempVBox.getChildren().add(temp);

            flow.setVgap(5);
            flow.getChildren().add(tempVBox);
        }
    }

    void setupSlider(Slider slider, Label label, Method method) {
        slider.setMin(1);
        slider.setMax(Runtime.getRuntime().availableProcessors());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {

            try {
                method.invoke(Main.defaultModel, newValue.intValue());
                label.setText(String.valueOf(newValue.intValue()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


        });

    }

    void setupChoiceBoxes(ChoiceBox choiceBox, Method method) {
        Parameter[] type = method.getParameters();

        for (Object optionName : type[0].getType().getEnumConstants()) {
            choiceBox.getItems().add(optionName);
        }

        choiceBox.setOnAction((event) -> {
            Object selectedItem = choiceBox.getValue();
            System.out.println(type[0].getType());
            try {
                method.invoke(Main.defaultModel, type[0].getType().cast(selectedItem));
                if (type[0].getType() == SearchType.class) {
                    SearchType item = (SearchType) selectedItem;
                    set_seed_option_panel.setVisible(false);
                    range_seed_option_panel.setVisible(false);
                    if (item.equals(SearchType.SET_SEED_SEARCH)) {
                        set_seed_option_panel.setVisible(true);
                    } else if (item.equals(SearchType.RANGE_SEARCH)) {
                        range_seed_option_panel.setVisible(true);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        });
    }

    void setupTextFields(TextField textField, Method method) {
        Parameter[] type = method.getParameters();
        textField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                if (type[0].getType() == int.class) {
                    method.invoke(Main.defaultModel, Integer.parseInt(newText));
                } else {
                    method.invoke(Main.defaultModel, Long.parseLong(newText));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    //endregion

    public static int parseToInt(String stringToParse) {
        int valueAsInt;
        try {
            valueAsInt = Integer.parseInt(stringToParse);
        } catch (NumberFormatException ex) {
            valueAsInt = 1; //Use default value if parsing failed
        }

        if (valueAsInt < 0) {
            valueAsInt = 1;
        }
        return valueAsInt;
    }


}

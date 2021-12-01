package sassa.ui;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.version.MCVersion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import sassa.Main;
import sassa.enums.BiomeListType;
import sassa.enums.SearchType;
import sassa.enums.SpawnType;
import sassa.enums.WorldType;
import sassa.models.Searcher_Model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class sassa_controller {

    List<String> inex = Arrays.asList(" ", "INCLUDE", "EXCLUDE");

    @FXML
    ChoiceBox search_type, spawn_type, world_type, minecraft_version;

    @FXML
    TextField biome_precision_input, search_radius_input, incrementer_input, seeds_to_find_input;

    @FXML
    Slider thread_slider;

    @FXML
    Label thread_number;

    @FXML
    FlowPane biomes_grid;

    @FXML

    public void startButton() {
        Main.beginThreads();
    }

    @FXML
    public void initialize() throws NoSuchMethodException {

        setupChoiceBoxes(search_type, Searcher_Model.class.getMethod("setSearchType", SearchType.class));
        setupChoiceBoxes(world_type, Searcher_Model.class.getMethod("setWorldType", WorldType.class));
        setupChoiceBoxes(spawn_type, Searcher_Model.class.getMethod("setSpawnType", SpawnType.class));
        setupChoiceBoxes(minecraft_version, Searcher_Model.class.getMethod("setSelectedVersion", MCVersion.class));

        setupTextFields(biome_precision_input, Searcher_Model.class.getMethod("setBiomePrecision", int.class));
        setupTextFields(search_radius_input, Searcher_Model.class.getMethod("setSearchRadius", int.class));
        setupTextFields(incrementer_input, Searcher_Model.class.getMethod("setIncrementer", int.class));
        setupTextFields(seeds_to_find_input, Searcher_Model.class.getMethod("setSeedsToFind", int.class));

        setupSlider(thread_slider, thread_number, Searcher_Model.class.getMethod("setThreadsToUse", int.class));

        setupGridPane(biomes_grid);

        rebuild();
    }

    void rebuild() {

        updateTextFields(biome_precision_input, Main.defaultModel.getBiomePrecision());
        updateTextFields(search_radius_input, Main.defaultModel.getSearchRadius());
        updateTextFields(incrementer_input, Main.defaultModel.getIncrementer());
        updateTextFields(seeds_to_find_input, Main.defaultModel.getSeedsToFind());

        updateChoiceBox(search_type, Main.defaultModel.getSearchType());
        updateChoiceBox(world_type, Main.defaultModel.getWorldType());
        updateChoiceBox(spawn_type, Main.defaultModel.getSpawnType());
        updateChoiceBox(minecraft_version, Main.defaultModel.getSelectedVersion());

        updateSlider(thread_slider, thread_number, Main.defaultModel.getThreadsToUse());
    }

    //region UPDATE UI

    void updateTextFields(TextField textField, int defaultValue) {
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

    void setupGridPane(FlowPane flow) {
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
//                    if(textField == true) {
//
//                        TextField tempField = new TextField();
//                        tempField.setMaxWidth(50);
//                        tempField.setTooltip(new Tooltip("How many structures do you want to have? (Default if blank is 1 and its a minimum value)"));
//                        tempGrid.getChildren().add(tempField);
//                    }


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
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        });
    }

    void setupTextFields(TextField textField, Method method) {
        textField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                method.invoke(Main.defaultModel, Integer.parseInt(newText));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    //endregion

}

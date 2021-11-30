package sassa.ui;

import com.seedfinding.mccore.version.MCVersion;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import sassa.Main;
import sassa.enums.SearchType;
import sassa.enums.SpawnType;
import sassa.enums.WorldType;
import sassa.models.Searcher_Model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class sassa_controller {

    @FXML
    ChoiceBox search_type, spawn_type, world_type, minecraft_version;

    @FXML
    TextField biome_precision_input, search_radius_input, incrementer_input, seeds_to_find_input;

    @FXML
    Slider thread_slider;

    @FXML
    Label thread_number;

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
            Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
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

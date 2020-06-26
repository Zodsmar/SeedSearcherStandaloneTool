package sassa.gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import kaptainwutax.biomeutils.Biome;
import sassa.util.StructureProvider;
import sassa.util.Structures;

import java.util.*;

public class GuiCollector {

    /**
     * Some Biomes come back as null. No idea. The Names match each other so it
     * should work (Apparently it works like 1 in 10 times...)
     *
     * @return
     */
    private static List<String> comboBoxManager(GridPane pane, String inORex) {
        List<String> checkedTexts = new ArrayList<String>();
        int k = 0;
        for(int i = 0; i < (pane.getChildren().size() / 3) + 1; i++) {
            for(int j =0; j < 3; j++) {
                //Adding an empty pane to the grid to fill in blanks check based on visiblity because its the only object going to be invisible
                if(pane.getChildren().get(k).isVisible()){
                    VBox tempVbox = (VBox) pane.getChildren().get(k);
                    Text tempText = (Text) tempVbox.getChildren().get(0);
                    ComboBox tempCombo = (ComboBox) tempVbox.getChildren().get(1);
                    if (tempCombo.getValue() != null && tempCombo.getValue().equals(inORex)) {
                        checkedTexts.add(tempText.getText());
                    }
                    k++;
                }
            }
        }

       return checkedTexts;
    }

    private static HashMap<String, String> comboBoxManagerWithValue(GridPane pane, String inORex) {
        HashMap<String, String> checkedTexts = new HashMap<>();
        int k = 0;
        for(int i = 0; i < (pane.getChildren().size() / 3) + 1; i++) {
            for(int j =0; j < 3; j++) {
                //Adding an empty pane to the grid to fill in blanks check based on visiblity because its the only object going to be invisible
                if(pane.getChildren().get(k).isVisible()){
                    VBox tempVbox = (VBox) pane.getChildren().get(k);
                    Text tempText = (Text) tempVbox.getChildren().get(0);
                    ComboBox tempCombo = (ComboBox) tempVbox.getChildren().get(1);
                    TextField tempField = (TextField) tempVbox.getChildren().get(2);
                    if (tempCombo.getValue() != null && tempCombo.getValue().equals(inORex)) {
                        if(tempField != null){
                            checkedTexts.put(tempText.getText(), tempField.getText());
                        }
                    }
                    k++;
                }
            }
        }
        return checkedTexts;
    }

    public static ArrayList<Biome> getBiomesFromUI(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        ArrayList<Biome> biomesList = new ArrayList<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
            Iterator regIt = Biome.REGISTRY.entrySet().iterator();
            while(regIt.hasNext()){
                Map.Entry mapElement = (Map.Entry)regIt.next();
                Biome b = (Biome) mapElement.getValue();
                if(b.getName() == checkedTexts.get(i)){
                    biomesList.add(b);
                }
            }

        }
        return biomesList;
    }

    public static ArrayList<Biome.Category> getCategoryFromUI(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        ArrayList<Biome.Category> categoryList = new ArrayList<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
            Iterator regIt = Biome.REGISTRY.entrySet().iterator();
            while(regIt.hasNext()){
                Map.Entry mapElement = (Map.Entry)regIt.next();
                Biome b = (Biome) mapElement.getValue();
                if(b.getCategory().getName() == checkedTexts.get(i) && !categoryList.contains(b.getCategory())){
                    categoryList.add(b.getCategory());
                    //System.out.println(b.getCategory().getName());
                }
            }

        }

        return categoryList;
    }

    public static ArrayList<StructureProvider> getStructures(GridPane pane, String inORex){
        HashMap<String, String> checkedTexts = comboBoxManagerWithValue(pane, inORex);
        ArrayList<StructureProvider> structuresList = new ArrayList<>();
        Iterator regIt = Structures.STRUCTURE.entrySet().iterator();
        while(regIt.hasNext()){
            Map.Entry mapElement = (Map.Entry)regIt.next();
            String name = (String) mapElement.getKey();
            StructureProvider s = (StructureProvider) mapElement.getValue();
            if(checkedTexts.containsKey(name)){
                int value = validateInputNumeric(checkedTexts.get(name));
                if(value <= -1){
                    value = 1;
                }
                s.setMinimumValue(value);
                //System.out.println(name + " " + s.getMinimumValue());
                for(int i = 0; i < s.getMinimumValue(); i++) {
                    structuresList.add(s);
                }
            }
        }

        return structuresList;
    }

    /**
     *  Make sure the str is 0+ only and numeric only
     * @param str
     * @return
     */
    private static int validateInputNumeric(String str){
        try
        {
            return Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return -1;
        }
    }
}



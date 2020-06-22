package sassa.gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.structure.RegionStructure;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Structures;
import sassa.util.Util;

import java.util.*;

public class GuiCollector {

    Util util = new Util();
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

    public static ArrayList<RegionStructure<?,?>> getStructuresFromUI(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        ArrayList<RegionStructure<?,?>> structuresList = new ArrayList<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
            Iterator regIt = Structures.STRUCTURE.entrySet().iterator();
            while(regIt.hasNext()){
                Map.Entry mapElement = (Map.Entry)regIt.next();
                String name = (String) mapElement.getKey();
                StructureProvider s = (StructureProvider) mapElement.getValue();
                if(name == checkedTexts.get(i)){
                    structuresList.add(s.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion()));
                    System.out.println(name);
                }
            }
        }
        return structuresList;
    }


    // Returns Map with Dimensions
    public static HashMap<String, RegionStructure<?,?>> getStructuresFromUIWithDim(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        HashMap<String, RegionStructure<?,?>> structuresList = new HashMap<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
            Iterator regIt = Structures.STRUCTURE.entrySet().iterator();
            while(regIt.hasNext()){
                Map.Entry mapElement = (Map.Entry)regIt.next();
                String name = (String) mapElement.getKey();
                StructureProvider s = (StructureProvider) mapElement.getValue();
                if(name == checkedTexts.get(i)){
                    structuresList.put(s.getDimension(), s.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion()));
                }
            }
        }
        return structuresList;
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
                System.out.println(name + " " + s.getMinimumValue());
                structuresList.add(s);

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

//
//    public HashMap<Biome, String> getBiomesSetsFromHashMap(GridPane pane, String inORex) throws IOException, ParseException {
//        List<String> checkedTexts = comboBoxManager(pane, inORex);
//        HashMap<Biome, String> completeBiomesList = new HashMap<>();
//        HashMap<String, String> sets = (HashMap) util.createSearchLists("getBiomeSets");
//        for( HashMap.Entry<String,String> e: sets.entrySet() ){
//            if(checkedTexts.contains(e.getValue())){
//                Biome biome = Biome.getByName(e.getKey());
//                String set = e.getValue();
//                completeBiomesList.put(biome, set);
//            }
//        }
//
//        return completeBiomesList;
//    }

//    public StructureSearcher_old.Type[] getStructuresFromArrayList(GridPane pane, String inORex){
//        List<String> checkedTexts = comboBoxManager(pane, inORex);
//        StructureSearcher_old.Type[] structures = new StructureSearcher_old.Type[checkedTexts.size()];
//        for (int i = 0; i < checkedTexts.size(); i++) {
//            structures[i] = StructureSearcher_old.Type.valueOf(checkedTexts.get(i).replaceAll(" ", "_").toUpperCase());
//        }
//        return structures;
//    }

//    public boolean checkIfBiomesSelected(Biome[] searchable, boolean check){
//        if (searchable.length == 0 && check) {
//            check = false;
//        }
//        return check;
//    }
//    public boolean checkIfBiomeSetsSelected(HashMap<Biome, String> searchable, boolean check){
//        if (searchable.size() == 0 && check) {
//            check = false;
//        }
//        return check;
//    }

//    public boolean checkIfStructuresSelected(StructureSearcher_old.Type[] searchable, boolean check){
//        if (searchable.length == 0 && check) {
//            check = false;
//        }
//        return check;
//    }
}



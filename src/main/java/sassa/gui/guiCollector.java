package sassa.gui;

import amidst.mojangapi.world.biome.Biome;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.simple.parser.ParseException;
import sassa.main.StructureSearcher;
import sassa.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class guiCollector {

    Util util = new Util();
    /**
     * Some Biomes come back as null. No idea. The Names match each other so it
     * should work (Apparently it works like 1 in 10 times...)
     *
     * @return
     */
    private List<String> comboBoxManager(GridPane pane, String inORex) {
        List<String> checkedTexts = new ArrayList<String>();
        int k = 0;
        for(int i = 0; i < pane.getRowCount(); i++) {
            for(int j =0; j < pane.getColumnCount(); j++) {
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

    public Biome[] getBiomesFromArrayList(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        Biome[] biomes = new Biome[checkedTexts.size()];
        for (int i = 0; i < checkedTexts.size(); i++) {
            biomes[i] = Biome.getByName(checkedTexts.get(i));
        }
        return biomes;
    }

    public HashMap<Biome, String> getBiomesSetsFromHashMap(GridPane pane, String inORex) throws IOException, ParseException {
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        HashMap<Biome, String> completeBiomesList = new HashMap<>();
        HashMap<String, String> sets = (HashMap) util.createSearchLists("getBiomeSets");
        for( HashMap.Entry<String,String> e: sets.entrySet() ){
            if(checkedTexts.contains(e.getValue())){
                Biome biome = Biome.getByName(e.getKey());
                String set = e.getValue();
                completeBiomesList.put(biome, set);
            }
        }

        return completeBiomesList;
    }

    public StructureSearcher.Type[] getStructuresFromArrayList(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        StructureSearcher.Type[] structures = new StructureSearcher.Type[checkedTexts.size()];
        for (int i = 0; i < checkedTexts.size(); i++) {
            structures[i] = StructureSearcher.Type.valueOf(checkedTexts.get(i).replaceAll(" ", "_").toUpperCase());
        }
        return structures;
    }

    public boolean checkIfBiomesSelected(Biome[] searchable, boolean check){
        if (searchable.length == 0 && check) {
            check = false;
        }
        return check;
    }
    public boolean checkIfBiomeSetsSelected(HashMap<Biome, String> searchable, boolean check){
        if (searchable.size() == 0 && check) {
            check = false;
        }
        return check;
    }

    public boolean checkIfStructuresSelected(StructureSearcher.Type[] searchable, boolean check){
        if (searchable.length == 0 && check) {
            check = false;
        }
        return check;
    }
}



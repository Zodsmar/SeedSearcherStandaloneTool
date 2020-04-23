package sassa.gui;

import amidst.mojangapi.world.biome.Biome;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sassa.main.StructureSearcher;

import java.util.ArrayList;
import java.util.List;

public class guiCollector {


    /**
     * Some Biomes come back as null. No idea. The Names match each other so it
     * should work (Apparently it works like 1 in 10 times...)
     *
     * @return
     */
    private List<String> comboBoxManager(GridPane pane, String inORex) {
        //System.out.println("Row: " + pane.getRowCount() + " Column: " + pane.getColumnCount());
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

    public Biome[] getBiomesSetsFromArrayList(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        Biome[] biomes = new Biome[checkedTexts.size()];
        for (int i = 0; i < checkedTexts.size(); i++) {
            biomes[i] = Biome.getByName(checkedTexts.get(i));
        }
        return biomes;
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

    public boolean checkIfStructuresSelected(StructureSearcher.Type[] searchable, boolean check){
        if (searchable.length == 0 && check) {
            check = false;
        }
        return check;
    }
}



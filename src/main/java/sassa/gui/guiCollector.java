package sassa.gui;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.parsing.FormatException;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sassa.main.StructureSearcher;
import sassa.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class guiCollector {


    /**
     * Some Biomes come back as null. No idea. The Names match each other so it
     * should work (Apparently it works like 1 in 10 times...)
     *
     * @return
     */
    public List<String> comboBoxManager(GridPane pane, String inORex) {
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
        System.out.println(checkedTexts.size());
        for (int i = 0; i < checkedTexts.size(); i++) {
            biomes[i] = Biome.getByName(checkedTexts.get(i));
        }

        return biomes;
    }

}

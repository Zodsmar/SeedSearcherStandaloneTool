package sassa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.json.simple.parser.ParseException;
import sassa.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class fxmlController implements Initializable {

    //Get the grid in Biomes tab to dynamically build it.
    @FXML
    private GridPane biomesGrid;

    String[] include_exclude_txt = {"", "Include", "Exclude"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Util gen = new Util();
        ArrayList<String> searchingList = null;
        try {
            searchingList = gen.createSearchLists("Biomes");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int k =0;
        for(int i = 0; i < searchingList.size()/3; i++) {
            for(int j =0; j < 3; j++){
                VBox tempGrid = new VBox();
                Text tempText = new Text(searchingList.get(k));
                k++;
                ComboBox<String> temp = new ComboBox<String>(FXCollections
                        .observableArrayList(include_exclude_txt));
                GridPane.setHgrow(tempGrid, Priority.ALWAYS);
                GridPane.setVgrow(tempGrid, Priority.ALWAYS);
                tempGrid.setAlignment(Pos.CENTER);
                biomesGrid.add(tempGrid,j,i + 1);

                tempGrid.getChildren().add(tempText);
                tempGrid.getChildren().add(temp);
            }


        }




    }


}

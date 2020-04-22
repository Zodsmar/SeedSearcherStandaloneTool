package sassa.util;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import sassa.gui.fxmlController;

public class Singleton {
    private static Singleton instance = new Singleton();

    public static Singleton getInstance(){
        return instance;
    }

    private GridPane biomesPane;
    private TextArea console;
    private String minecraftVersion;
    private Text tRejSeed, cRejSeed, sequencedSeed;
    private TextField seedCount, mcPath;
    private fxmlController controller;

    public void setController(fxmlController controller){
        this.controller = controller;
    }
    public fxmlController getController(){
        return controller;
    }

    public GridPane getBiomesGridPane(){
        return biomesPane;
    }

    public void setBiomesGridPane(GridPane pane){
        this.biomesPane = pane;
    }

    public TextArea getConsole(){
        return console;
    }

    public void setConsole(TextArea console){
        this.console = console;
    }
    public String getMinecraftVersion(){
        return minecraftVersion;
    }

    public void setMinecraftVersion(String minecraftVersion){
        this.minecraftVersion = minecraftVersion;
    }

    public TextField getMCPath(){
        return mcPath;
    }

    public void setMCPath(TextField mcPath){
        this.mcPath = mcPath;
    }

    public TextField getSeedCount(){
        return seedCount;
    }

    public void setSeedCount(TextField seedCount){
        this.seedCount = seedCount;
    }

    public Text getCRejSeed(){
        return cRejSeed;
    }

    public void setCRejSeed(Text cRejSeed){
        this.cRejSeed = cRejSeed;
    }

    public Text getTRejSeed(){
        return tRejSeed;
    }

    public void setTRejSeed(Text tRejSeed){
        this.tRejSeed = tRejSeed;
    }

    public Text getSequenceSeed(){
        return sequencedSeed;
    }

    public void setSequenceSeed(Text sequencedSeed){
        this.sequencedSeed = sequencedSeed;
    }
}

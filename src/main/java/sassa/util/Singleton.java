package sassa.util;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import sassa.gui.fxmlController;
import kaptainwutax.mcutils.version.MCVersion;
    
import java.io.File;

//Might rename this to Global Config in the next version (That's basically what it is)

public class Singleton {
    private static Singleton instance = new Singleton();

    public static Singleton getInstance(){
        return instance;
    }

    private GridPane biomesPane, structurePane, biomeSetsPane;
    private TextArea console;
    private MCVersion minecraftVersion;
    private Text tRejSeed, cRejSeed, sequencedSeed, coresAmount;
    private TextField seedCount, incrementer, minSeed, maxSeed, biomePrecision, xCoordSpawn, zCoordSpawn, marginOfError;
    private fxmlController controller;
    private CheckBox autoSave, shadowMode, bedrockMode, randomSeed, setSeed, spawnPoint;
    private File outputFile, seedFile;
    private ComboBox worldType;
    private Slider amountOfCores;



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

    public GridPane getBiomeSetsGridPane(){
        return biomeSetsPane;
    }

    public void setBiomeSetsGridPane(GridPane pane){
        this.biomeSetsPane = pane;
    }

    public GridPane getStructureGridPane(){
        return structurePane;
    }

    public void setStructureGridPane(GridPane pane){
        this.structurePane = pane;
    }

    public TextArea getConsole(){
        return console;
    }

    public void setConsole(TextArea console){
        this.console = console;
    }
    public MCVersion getMinecraftVersion(){
        return minecraftVersion;
    }

    public void setMinecraftVersion(MCVersion minecraftVersion){
        this.minecraftVersion = minecraftVersion;
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

    public CheckBox getAutoSave() {
        return autoSave;
    }

    public void setAutoSave(CheckBox autoSave) {
        this.autoSave = autoSave;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public ComboBox getWorldType() {
        return worldType;
    }

    public void setWorldType(ComboBox worldType) {
        this.worldType = worldType;
    }

    public Slider getAmountOfCores(){
        return amountOfCores;
    }

    public void setAmountOfCores(Slider amountOfCores) {
        this.amountOfCores = amountOfCores;
    }

    public TextField getIncrementer()  {
        return incrementer;
    }

    public void setIncrementer(TextField incrementer){
        this.incrementer = incrementer;
    }

    public TextField getBiomePrecision()  {
        return biomePrecision;
    }

    public void setBiomePrecision(TextField biomePrecision){
        this.biomePrecision = biomePrecision;
    }

    public CheckBox getShadowMode(){
        return shadowMode;
    }

    public void setShadowMode(CheckBox shadowMode){
        this.shadowMode = shadowMode;
    }

    public CheckBox getBedrockMode(){
        return bedrockMode;
    }

    public void setBedrockMode(CheckBox bedrockMode){
        this.bedrockMode = bedrockMode;
    }

    public Text getCoresAmount(){
        return coresAmount;
    }

    public void setCoresAmount(Text coresAmount){
        this.coresAmount = coresAmount;
    }

    public TextField getMinSeed()  {
        return minSeed;
    }

    public void setMinSeed(TextField minSeed){
        this.minSeed = minSeed;
    }

    public TextField getMaxSeed()  {
        return maxSeed;
    }

    public void setMaxSeed(TextField maxSeed){
        this.maxSeed = maxSeed;
    }

    public void setRandomSeed(CheckBox randomSeed){
        this.randomSeed = randomSeed;
    }

    public CheckBox getRandomSeed(){
        return randomSeed;
    }

    public void setSetSeed(CheckBox setSeed){
        this.setSeed = setSeed;
    }

    public CheckBox getSetSeed(){
        return setSeed;
    }

    public void setSeedFile(File seedFile) {
        this.seedFile = seedFile;
    }

    public File getSeedFile() {
        return seedFile;
    }

    public void setSpawnPoint(CheckBox spawnPoint){
        this.spawnPoint = spawnPoint;
    }

    public CheckBox getSpawnPoint(){
        return spawnPoint;
    }

    public TextField getXCoordSpawn()  {
        return xCoordSpawn;
    }

    public void setXCoordSpawn(TextField xCoordSpawn){
        this.xCoordSpawn = xCoordSpawn;
    }

    public TextField getZCoordSpawn()  {
        return zCoordSpawn;
    }

    public void setZCoordSpawn(TextField zCoordSpawn){
        this.zCoordSpawn = zCoordSpawn;
    }

    public TextField getMarginOfError()  {
        return marginOfError;
    }

    public void setMarginOfError(TextField marginOfError){
        this.marginOfError = marginOfError;
    }
}

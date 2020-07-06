package sassa.searcher;

import javafx.application.Platform;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import sassa.gui.Variables;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SearchingThread extends Thread implements Runnable{

    private long startSeedStructure;
    private ArrayList<StructureProvider> structuresIN;
    private ArrayList<StructureProvider> structuresOUT;
    private ArrayList<Biome> biomesIN;
    private ArrayList<Biome> biomesOUT;
    private ArrayList<Biome.Category> categoriesIN;
    private ArrayList<Biome.Category> categoriesOUT;
    private int searchRadius;

    public SearchingThread(long startSeedStructure, int searchRadius, ArrayList<StructureProvider> structuresIN, ArrayList<StructureProvider> structuresOUT, ArrayList<Biome> biomesIN, ArrayList<Biome> biomesOUT, ArrayList<Biome.Category> categoriesIN, ArrayList<Biome.Category> categoriesOUT) {
        this.startSeedStructure = startSeedStructure;
        this.structuresIN = structuresIN;
        this.structuresOUT = structuresOUT;
        this.biomesIN = biomesIN;
        this.biomesOUT = biomesOUT;
        this.categoriesIN = categoriesIN;
        this.categoriesOUT = categoriesOUT;
        this.searchRadius = searchRadius;
    }

    @Override
    public void run() {
        /*
         * - Create the appropriate searching lists
         * - Determine which searching functions to use based on lists
         * - check that all values are being passed up correctly to variables
         * - Profit (Run this as many times to speed up searching within computer limits ofc)
         */
        try {
            searching();
        } catch (IOException e) {
            System.out.println("IO Exception");
        } catch (InterruptedException e) {
            System.out.println("Interupeted");
        }
    }

    private void searching() throws IOException, InterruptedException {
        Singleton sg = Singleton.getInstance();
        Util util = new Util();
        boolean startNotRandom = false, endOfRandoms = false;
        long randomSeed = 0;
        while ( Long.parseLong(sg.getSeedCount().getText()) > Variables.acceptedWorlds() && fxmlController.running == true && endOfRandoms == false) {

            
            if(!sg.getRandomSeed().isSelected() && startNotRandom == false){
                randomSeed = Long.parseLong(sg.getMinSeed().getText());
                startNotRandom = true;
                Variables.updateCurrentSeed(randomSeed);
            } else if (!sg.getRandomSeed().isSelected() && startNotRandom == true){
                if(randomSeed >= Long.parseLong(sg.getMaxSeed().getText())){
                    break;
                }

                randomSeed++;
                Variables.updateCurrentSeed(randomSeed);
            } else {
                if(sg.getBedrockMode().isSelected()){
                    randomSeed = new Random().nextInt();
                } else {
                    randomSeed = new Random().nextLong();
                }
            }

            int incrementer = Integer.parseInt(sg.getIncrementer().getText());
            //Make sure to create new copies everytime so it doesnt give false positives
            ArrayList<StructureProvider> si = new ArrayList<>(this.structuresIN);
            ArrayList<StructureProvider> so = new ArrayList<>(this.structuresOUT);
            ArrayList<Biome> bi = new ArrayList<>(this.biomesIN);
            ArrayList<Biome> bo = new ArrayList<>(this.biomesOUT);
            ArrayList<Biome.Category> ci = new ArrayList<>(this.categoriesIN);
            ArrayList<Biome.Category> co  = new ArrayList<>(this.categoriesOUT);

            if(si.size() != 0 && sg.getRandomSeed().isSelected()) {
                Searcher.searchRandomly(searchRadius, startSeedStructure, si, so, bi, bo, ci, co, "OVERWORLD", incrementer, 16);
                break;
            } else {
                Variables.checkWorld(1);

                if (si.size() != 0) {
                    si = StructureSearcher.findStructure(searchRadius, randomSeed, si);
                    if (si.size() != 0) continue;
                }
                if (so.size() != 0) {
                    so = StructureSearcher.findStructureEx(searchRadius, randomSeed, so);
                    if (so.size() != 0) continue;
                }
                if (bi.size() != 0) {
                    bi = BiomeSearcher.findBiome(searchRadius, randomSeed, bi, incrementer);
                    if (bi.size() != 0) continue;
                }
                if (bo.size() != 0) {
                    bo = BiomeSearcher.findBiomeEx(searchRadius, randomSeed, bo, incrementer);
                    if (bo.size() != 0) continue;
                }
                if (ci.size() != 0) {
                    ci = BiomeSearcher.findBiomeFromCategory(searchRadius, randomSeed, ci, incrementer);
                    if (ci.size() != 0) continue;

                }
                if (co.size() != 0) {
                    co = BiomeSearcher.findBiomeFromCategoryEx(searchRadius, randomSeed, co, incrementer);
                    if (co.size() != 0) continue;
                }

                if (si.size() == 0 && so.size() == 0
                        && bi.size() == 0 && bo.size() == 0 //
                        && ci.size() == 0 && co.size() == 0 && fxmlController.running == true) {
                    if(Singleton.getInstance().getShadowMode().isSelected()){
                        util.console(String.valueOf(randomSeed) + " (Shadow: " + WorldSeed.getShadowSeed(randomSeed) + " )");
                    } else {
                        util.console(String.valueOf(randomSeed));
                    }

                    //print out the world seed (Plus possibly more information)
                } else {
                    System.out.println("Failed");
                }
            }
            Variables.acceptWorld();
            Variables.minOneCheckWorld();
        }
        // Should stop
        if(fxmlController.running == true){
            fxmlController.running = false;
            Platform.runLater(() -> {
                Singleton.getInstance().getController().stop();
            });
        }
    }
}

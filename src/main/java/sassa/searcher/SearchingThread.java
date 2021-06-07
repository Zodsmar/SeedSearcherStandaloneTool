package sassa.searcher;

import javafx.application.Platform;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import sassa.gui.Variables;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SearchingThread extends Thread implements Runnable{

    private long startSeedStructure, endSeedStructure;
    private ArrayList<StructureProvider> structuresIN;
    private ArrayList<StructureProvider> structuresOUT;
    private ArrayList<Biome> biomesIN;
    private ArrayList<Biome> biomesOUT;
    private ArrayList<Biome.Category> categoriesIN;
    private ArrayList<Biome.Category> categoriesOUT;
    private int searchRadius;

    public SearchingThread(long startSeedStructure, long endSeedStructure, int searchRadius, ArrayList<StructureProvider> structuresIN, ArrayList<StructureProvider> structuresOUT, ArrayList<Biome> biomesIN, ArrayList<Biome> biomesOUT, ArrayList<Biome.Category> categoriesIN, ArrayList<Biome.Category> categoriesOUT) {
        this.startSeedStructure = startSeedStructure;
        this.endSeedStructure = endSeedStructure;
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
        ArrayList<String> seeds = new ArrayList<String>();
        while ( Long.parseLong(sg.getSeedCount().getText()) > Variables.acceptedWorlds() && fxmlController.running == true && endOfRandoms == false) {


            if(!sg.getRandomSeed().isSelected() && startNotRandom == false && !sg.getSetSeed().isSelected()){
                randomSeed = Long.parseLong(sg.getMinSeed().getText());
                startNotRandom = true;
                Variables.updateCurrentSeed(randomSeed);
            } else if (!sg.getRandomSeed().isSelected() && startNotRandom == true && !sg.getSetSeed().isSelected()){
                randomSeed = Long.parseLong(sg.getMinSeed().getText());
                if(randomSeed >= Long.parseLong(sg.getMaxSeed().getText())){
                    break;
                }

                randomSeed++;
                Variables.updateCurrentSeed(randomSeed);
            } else if (!sg.getRandomSeed().isSelected() && sg.getSetSeed().isSelected()) {
                if (seeds.size() == 0) {
                    seeds = util.readFromFile(sg.getSeedFile());
                }
                try {
                    randomSeed = Long.parseLong(seeds.get(0));
                } catch (NumberFormatException e) {
                    randomSeed = seeds.get(0).hashCode();
                }
                //randomSeed = Long.parseLong(seeds.get(0));
                seeds.remove(0);
                if(randomSeed == -1){
                    break;
                }
            } else {
                //randomSeed = Long.parseLong(sg.getMinSeed().getText());
                if(sg.getBedrockMode().isSelected()){
                    randomSeed = new Random().nextInt();
                } else {
                    randomSeed = new Random().nextLong();
                }
            }

            int incrementer = Integer.parseInt(sg.getIncrementer().getText());
            int biomePrecision = Integer.parseInt(sg.getBiomePrecision().getText());
            if(biomePrecision > 16) {
                biomePrecision = 16;
            } else if(biomePrecision < 0) {
                biomePrecision = 0;
            }
            //Make sure to create new copies everytime so it doesnt give false positives
            ArrayList<StructureProvider> si = new ArrayList<>(this.structuresIN);
            ArrayList<StructureProvider> so = new ArrayList<>(this.structuresOUT);
            ArrayList<Biome> bi = new ArrayList<>(this.biomesIN);
            ArrayList<Biome> bo = new ArrayList<>(this.biomesOUT);
            ArrayList<Biome.Category> ci = new ArrayList<>(this.categoriesIN);
            ArrayList<Biome.Category> co  = new ArrayList<>(this.categoriesOUT);

            if(si.size() != 0 && sg.getRandomSeed().isSelected()) {
                Searcher.searchRandomly(searchRadius, startSeedStructure, endSeedStructure, si, so, bi, bo, ci, co, Dimension.OVERWORLD, incrementer, biomePrecision);
                break;
            } else {
                Variables.checkWorld(1);
                if(Singleton.getInstance().getSpawnPoint().isSelected()){
                    if(!Searcher.checkSpawnPoint(new OverworldBiomeSource(Singleton.getInstance().getMinecraftVersion(), randomSeed))){
                        continue;
                    }
                }

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
                        util.console(String.valueOf(randomSeed) + " (Shadow: " + WorldSeed.getShadowSeed(randomSeed) + ")");
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

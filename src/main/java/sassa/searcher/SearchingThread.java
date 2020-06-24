package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import sassa.gui.Variables;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class SearchingThread extends Thread implements Runnable{

    private long startSeed;
    private ArrayList<StructureProvider> structuresIN;
    private ArrayList<StructureProvider> structuresOUT;
    private ArrayList<Biome> biomesIN;
    private ArrayList<Biome> biomesOUT;
    private ArrayList<Biome.Category> categoriesIN;
    private ArrayList<Biome.Category> categoriesOUT;
    private int searchRadius;

    public SearchingThread(long startSeed, int searchRadius, ArrayList<StructureProvider> structuresIN, ArrayList<StructureProvider> structuresOUT, ArrayList<Biome> biomesIN, ArrayList<Biome> biomesOUT, ArrayList<Biome.Category> categoriesIN, ArrayList<Biome.Category> categoriesOUT) {
        this.startSeed = startSeed;
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
        searching();
    }

    private void searching() {
        Singleton sg = Singleton.getInstance();
        Util util = new Util();
        //TODO: Change from 0 to seedsFound
        while ( Integer.parseInt(sg.getSeedCount().getText()) >= Variables.acceptedWorlds() && fxmlController.running == true && fxmlController.paused == false) {

            long randomSeed = new Random().nextLong();
            int incrementer = Integer.parseInt(sg.getIncrementer().getText());
            //Make sure to create new copies everytime so it doesnt give false positives
            ArrayList<StructureProvider> si = new ArrayList<>(this.structuresIN);
            ArrayList<StructureProvider> so = new ArrayList<>(this.structuresOUT);
            ArrayList<Biome> bi = new ArrayList<>(this.biomesIN);
            ArrayList<Biome> bo = new ArrayList<>(this.biomesOUT);
            ArrayList<Biome.Category> ci = new ArrayList<>(this.categoriesIN);
            ArrayList<Biome.Category> co  = new ArrayList<>(this.categoriesOUT);

            Variables.checkWorld();

            if (si.size() != 0) {
                si = StructureSearcher.findStructure(searchRadius, randomSeed, si);
                //After searching if size still doesn't equal 0 (meaning it didnt find everything its looking for continue)
                if (si.size() != 0) {
                    continue;
                }
            }
            if (so.size() != 0) {
                so = StructureSearcher.findStructureEx(searchRadius, randomSeed, so);
                if (so.size() != 0) {
                    continue;
                }
            }
            if (bi.size() != 0) {
                bi = BiomeSearcher.findBiome(searchRadius, randomSeed, bi, incrementer);
                if (bi.size() != 0) {
                    continue;
                }
            }
            if (bo.size() != 0) {
                bo = BiomeSearcher.findBiomeEx(searchRadius, randomSeed, bo, incrementer);
                if (bo.size() != 0) {
                    continue;
                }
            }
            if (ci.size() != 0) {

                if (ci.size() != 0) {
                    continue;
                }
            }
            if (co.size() != 0) {

                if (co.size() != 0) {
                    continue;
                }
            }

            if (si.size() == 0 && so.size() == 0
                    && bi.size() == 0 && bo.size() == 0 //
                    && ci.size() == 0 && co.size() == 0) {
                util.console(String.valueOf(randomSeed));
                Variables.acceptWorld();
                Variables.minOneCheckWorld();
                //print out the world seed (Plus possibly more information)
            } else {
                System.out.println("Failed");
            }
        }
        //if(seedsFound >= Integer.parseInt(sg.getSeedCount().getText())) {
        //     STOP EVERYTHING
        //}
    }
}

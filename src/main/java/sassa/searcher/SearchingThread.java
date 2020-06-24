package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;

import java.util.ArrayList;
import java.util.Random;

public class SearchingThread extends Thread implements Runnable{

    private long startSeed;
    private ArrayList<StructureProvider> structuresIN;
    private ArrayList<StructureProvider> structuresOUT;
    private ArrayList<Biome> biomesIN;
    private ArrayList<Biome> biomesOUT;
    private ArrayList<Biome.Category> categoriesIN;
    private ArrayList<Biome.Category> categoriesOUT;

    public SearchingThread(long startSeed, ArrayList<StructureProvider> structuresIN, ArrayList<StructureProvider> structuresOUT, ArrayList<Biome> biomesIN, ArrayList<Biome> biomesOUT, ArrayList<Biome.Category> categoriesIN, ArrayList<Biome.Category> categoriesOUT) {
        this.startSeed = startSeed;
        this.structuresIN = structuresIN;
        this.structuresOUT = structuresOUT;
        this.biomesIN = biomesIN;
        this.biomesOUT = biomesOUT;
        this.categoriesIN = categoriesIN;
        this.categoriesOUT = categoriesOUT;
    }

    @Override
    public void run() {
        /*
         * - Create the appropriate searching lists
         * - Determine which searching functions to use based on lists
         * - check that all values are being passed up correctly to variables
         * - Profit (Run this as many times to speed up searching within computer limits ofc)
         */
    }

    private void searching() {
        Singleton sg = Singleton.getInstance();

        //TODO: Change from 0 to seedsFound
        while ( Integer.parseInt(sg.getSeedCount().getText()) >= 0 && fxmlController.running == true && fxmlController.paused == false) {

            long randomSeed = new Random().nextLong();

            //Make sure to create new copies everytime so it doesnt give false positives
            ArrayList<StructureProvider> si = new ArrayList<>(this.structuresIN);
            ArrayList<StructureProvider> so = new ArrayList<>(this.structuresOUT);
            ArrayList<Biome> bi = new ArrayList<>(this.biomesIN);
            ArrayList<Biome> bo = new ArrayList<>(this.biomesOUT);
            ArrayList<Biome.Category> ci = new ArrayList<>(this.categoriesIN);
            ArrayList<Biome.Category> co  = new ArrayList<>(this.categoriesOUT);


            if (si.size() != 0) {

                //After searching if size still doesn't equal 0 (meaning it didnt find everything its looking for continue)
                if (si.size() != 0) {
                    continue;
                }
            }
            if (so.size() != 0) {

                if (so.size() != 0) {
                    continue;
                }
            }
            if (bi.size() != 0) {

                if (bi.size() != 0) {
                    continue;
                }
            }
            if (bo.size() != 0) {

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

                //print out the world seed (Plus possibly more information)
            }
        }
        //if(seedsFound >= Integer.parseInt(sg.getSeedCount().getText())) {
        //     STOP EVERYTHING
        //}
    }
}

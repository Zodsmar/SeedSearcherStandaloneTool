package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import sassa.gui.GuiCollector;
import sassa.util.StructureProvider;

import java.util.ArrayList;

public class SearchingThread extends Thread implements Runnable{

    private ArrayList<StructureProvider> structuresIN;
    private ArrayList<StructureProvider> structuresOUT;
    private ArrayList<Biome> biomesIN;
    private ArrayList<Biome> biomesOUT;
    private ArrayList<Biome.Category> categoriesIN;
    private ArrayList<Biome.Category> categoriesOUT;

    public SearchingThread(ArrayList<StructureProvider> structuresIN, ArrayList<StructureProvider> structuresOUT, ArrayList<Biome> biomesIN, ArrayList<Biome> biomesOUT, ArrayList<Biome.Category> categoriesIN, ArrayList<Biome.Category> categoriesOUT) {
        this.structuresIN = new ArrayList<>(structuresIN);
        this.structuresOUT = new ArrayList<>(structuresOUT);
        this.biomesIN = new ArrayList<>(biomesIN);
        this.biomesOUT = new ArrayList<>(biomesOUT);
        this.categoriesIN = new ArrayList<>(categoriesIN);
        this.categoriesOUT = new ArrayList<>(categoriesOUT);
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
        if(structuresIN.size() != 0) {

        }
        if(structuresOUT.size() != 0) {

        }
        if(biomesIN.size() != 0) {

        }
        if(biomesOUT.size() != 0) {

        }
        if(categoriesIN.size() != 0) {

        }
        if(categoriesOUT.size() != 0) {

        }
    }
}

package sassa;

import com.seedfinding.mcbiome.biome.Biomes;
import sassa.enums.BiomeListType;
import sassa.enums.SearchType;
import sassa.models.BiomeSet_Model;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.models.features.Feature_Registry;
import sassa.searcher.Searching_Thread;
import sassa.ui.ui_application;
import sassa.util.ConfigParser;
import sassa.util.FileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The New MAIN FILE. This will get renamed later
public class Main {

    private static ArrayList<Thread> currentThreads = new ArrayList<>();
    public static Searcher_Model defaultModel;

    //This is going to be the new starting file will rename to Main after the refactor is complete
    //args are model path, headless,
    public static void main(String[] args) {
        ConfigParser configParser = new ConfigParser();
        if (args.length == 0) {
            defaultModel = new Searcher_Model();
            ui_application ui = new ui_application();
            ui.startGUI(args);
        }
        if (args.length > 0) {
            if (args.length > 1) {
                defaultModel = configParser.ReadConfigFile(args[0]);
            } else {
                defaultModel = new Searcher_Model();
            }
            //TODO I need to make this path not hardcoded
            if (args[0].equals("headless")) {
                beginThreads();
            } else if (args[0].equals("gui")) {
                ui_application ui = new ui_application();
                ui.startGUI(args);
            }

        }
        //

        //defaultModel.getBiomeList().addBiomes(Arrays.asList(Biomes.FOREST), BiomeListType.INCLUDED);
        //defaultModel.getBiomeList().addBiomes(Arrays.asList(Biomes.FOREST, Biomes.PLAINS, Biomes.JUNGLE, Biomes.DESERT), BiomeListType.EXCLUDED);
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.ZOMBIEVILLAGE, 2)));
        defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 2), new Feature_Model(Feature_Registry.OWRUINEDPORTAL), new Feature_Model(Feature_Registry.PILLAGEROUTPOST), new Feature_Model(Feature_Registry.ZOMBIEVILLAGE)));
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 4), new Feature_Model(Feature_Registry.OWRUINEDPORTAL, 2), new Feature_Model(Feature_Registry.PILLAGEROUTPOST)));
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 3), new Feature_Model(Feature_Registry.OWRUINEDPORTAL, 2), new Feature_Model(Feature_Registry.ZOMBIEVILLAGE), new Feature_Model(Feature_Registry.PILLAGEROUTPOST)));
        //configParser.WriteConfigFile(defaultModel);


    }

    public static void beginThreads() {
        if (preliminaryChecks(defaultModel)) {
            //This call is to create the features for the current version you are searching and is strickly a runtime variable
            defaultModel.setFeatureList(defaultModel.getIncludedFeatures().getCreatedFeatureListFromVersion(defaultModel.getSelectedVersion()));

            createNewThreads(defaultModel);
        }
    }

    static boolean preliminaryChecks(Searcher_Model model) {
        if (!model.getBiomeList().getIncludedBiomes().isEmpty() ||
                !model.getBiomeList().getExcludedBiomes().isEmpty() ||
                !model.getBiomeSetList().getIncludedBiomeSet().isEmpty() ||
                !model.getIncludedFeatures().getFeatureList().isEmpty()) {
            return true;
        }
        System.out.println("Please make sure you have at least a biome or biome set selected!");
        return false;
    }

    static void createNewThreads(Searcher_Model model) {
        List<Long> seeds;
        if (defaultModel.getSearchType() == SearchType.SET_SEED_SEARCH) {
            seeds = FileHelper.getSeedsAsListFromFile(model.getSeedFile());
        } else {
            seeds = new ArrayList<>();
        }

        for (int i = 0; i < model.getThreadsToUse(); i++) {

            //Since it is multithreaded, we want to make sure that each thread starts at different seeds and goes up sequentially

            Thread t = new Searching_Thread(model, i, seeds);
            t.start();
            System.out.format("%d Thread Running \n", i);
            currentThreads.add(t);
        }

    }

    public static void stopAllThreads() {
        for (Thread t : currentThreads) {
            if (t != null) {
                t.interrupt();
            }
        }

        System.out.println("Stopped all threads");
        currentThreads = new ArrayList<>();
    }


    static void TestGenerateConfigs(String fileName, Searcher_Model model, ConfigParser parser) {


        model.setConfigName(fileName);
        model.setSearchRadius(100);
        model.setIncrementer(25);
        model.setSeedsToFind(10);

        model.getBiomeList().addBiome(Biomes.BADLANDS, BiomeListType.INCLUDED);
        model.getBiomeList().addBiomes(Arrays.asList(Biomes.ICE_PLAINS_SPIKES, Biomes.MUSHROOM_ISLAND, Biomes.MESA), BiomeListType.INCLUDED);


        BiomeSet_Model bsM = new BiomeSet_Model("Custom1", Arrays.asList(Biomes.ICE_PLAINS_SPIKES, Biomes.MESA));
        model.getBiomeSetList().addBiomeSetList(bsM, BiomeListType.INCLUDED);
        bsM = new BiomeSet_Model("Custom2", Arrays.asList(Biomes.FOREST, Biomes.FLOWER_FOREST));
        model.getBiomeSetList().addBiomeSetList(bsM, BiomeListType.INCLUDED);


        BiomeSet_Model customSet1 = new BiomeSet_Model("Custom 1", Arrays.asList(Biomes.ICE_PLAINS_SPIKES, Biomes.MUSHROOM_ISLAND));
        model.getBiomeSetList().addBiomeSetList(customSet1, BiomeListType.INCLUDED);
        model.getBiomeList().addBiomes(Arrays.asList(Biomes.ICE_PLAINS_SPIKES, Biomes.FOREST), BiomeListType.INCLUDED);

        /* GOOD INCLUDED BIOME SET TEMPLATE
        BiomeSetList_Model bsModel = new BiomeSetList_Model();
        BiomeSet_Model bsM = new BiomeSet_Model("Custom1", Arrays.asList(Biomes.ICE_PLAINS_SPIKES, Biomes.MUSHROOM_ISLAND));
        bsModel.addBiomeSetList(bsM, BiomeListType.INCLUDED);
        bsM = new BiomeSet_Model("Custom2", Arrays.asList(Biomes.MESA, Biomes.GIANT_SPRUCE_TAIGA));
        bsModel.addBiomeSetList(bsM, BiomeListType.INCLUDED);
        defaultModel.setBiomeSetList(bsModel);
        */


        parser.WriteConfigFile(model);
    }


}

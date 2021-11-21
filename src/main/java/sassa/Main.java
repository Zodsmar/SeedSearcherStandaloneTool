package sassa;

import com.seedfinding.mcbiome.biome.Biomes;
import sassa.enums.BiomeListType;
import sassa.models.BiomeSet_Model;
import sassa.models.Searcher_Model;
import sassa.searcher.Searching_Thread;
import sassa.util.ConfigParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// The New MAIN FILE. This will get renamed later
public class Main {

    private static ArrayList<Thread> currentThreads = new ArrayList<>();
    public static Searcher_Model defaultModel;

    //This is going to be the new starting file will rename to Main after the refactor is complete
    public static void main(String[] args) throws CloneNotSupportedException, IOException, InterruptedException {
        ConfigParser configParser = new ConfigParser();


        if (args.length > 0) {
            //TODO I need to make this path not hardcoded
            defaultModel = configParser.ReadConfigFile(args[0]);

        } else {

            defaultModel = new Searcher_Model();
        }

        //defaultModel.getBiomeList().addBiomes(Arrays.asList(Biomes.FLOWER_FOREST, Biomes.PLAINS), BiomeListType.INCLUDED);
        //defaultModel.getBiomeList().addBiomes(Arrays.asList(Biomes.FOREST, Biomes.PLAINS, Biomes.JUNGLE, Biomes.DESERT), BiomeListType.EXCLUDED);
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.ZOMBIEVILLAGE, 1), new Feature_Model(Feature_Registry.IGLOOBASEMENT, 1)));
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 4), new Feature_Model(Feature_Registry.OWRUINEDPORTAL), new Feature_Model(Feature_Registry.PILLAGEROUTPOST)));
        //defaultModel.getIncludedFeatures().addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 3), new Feature_Model(Feature_Registry.OWRUINEDPORTAL, 2)));
        //configParser.WriteConfigFile(defaultModel);
        if (preliminaryChecks(defaultModel)) {
            //This call is to create the features for the current version you are searching and is strickly a runtime variable
            defaultModel.setFeatureList(defaultModel.getIncludedFeatures().getCreatedFeatureListFromVersion(defaultModel.getSelectedVersion()));

            createNewThreads(defaultModel);
        }


        /////// TESTING SECTION //////

        //Search radius must always be at least 1.

        //TestGenerateConfigs("Wow", defaultModel, configParser);


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

        for (int i = 0; i < model.getThreadsToUse(); i++) {

            //Since it is multithreaded, we want to make sure that each thread starts at different seeds and goes up sequentially
            //TODO this needs to change when I start bringing in static searches, need to look into that
            long startFeatureSeed = (long) Math.floor(Math.pow(2, 48) / model.getThreadsToUse() * i);
            long endFeatureSeed = Math.min((long) Math.floor(Math.pow(2, 48) / model.getThreadsToUse() * (i + 1)), 1L << 48);

            Thread t = new Searching_Thread(model, startFeatureSeed, endFeatureSeed);
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

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
public class Launch {

    private static ArrayList<Thread> currentThreads = new ArrayList<>();

    //This is going to be the new starting file will rename to Main after the refactor is complete
    public static void main(String[] args) throws CloneNotSupportedException, IOException, InterruptedException {
        ConfigParser configParser = new ConfigParser();

        Searcher_Model defaultModel;
        if (args.length > 0) {
            //TODO I need to make this path not hardcoded
            defaultModel = configParser.ReadConfigFile(args[0]);

        } else {

            defaultModel = new Searcher_Model();
        }

        defaultModel.getBiomeList().addBiomes(Arrays.asList(Biomes.ICE_PLAINS_SPIKES), BiomeListType.INCLUDED);
        if (preliminaryChecks(defaultModel)) {
            createNewThreads(defaultModel);
        }


        /////// TESTING SECTION //////

        //Search radius must always be at least 1.

        //TestGenerateConfigs("Wow", defaultModel, configParser);


    }

    static boolean preliminaryChecks(Searcher_Model model) {
        if (!model.getBiomeList().getIncludedBiomes().isEmpty() ||
                !model.getBiomeList().getExcludedBiomes().isEmpty() ||
                !model.getBiomeSetList().getIncludedBiomeSet().isEmpty()) {
            return true;
        }
        System.out.println("Please make sure you have at least a biome or biome set selected!");
        return false;
    }

    static void createNewThreads(Searcher_Model model) {

        for (int i = 0; i < model.getThreadsToUse(); i++) {

            Thread t = new Searching_Thread(model, i);
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

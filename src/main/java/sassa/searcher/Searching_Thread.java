package sassa.searcher;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Launch;
import sassa.enums.WorldType;
import sassa.models.BiomeList_Model;
import sassa.models.BiomeSetList_Model;
import sassa.models.BiomeSet_Model;
import sassa.models.Searcher_Model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Searching_Thread extends Thread implements Runnable {
    Searcher_Model model;
    int threadID;
    static AtomicInteger currentSeedCount = new AtomicInteger(0);


    public Searching_Thread(Searcher_Model model, int threadID) {
        this.model = model;
        this.threadID = threadID;
    }

    private void searching() throws IOException, InterruptedException, CloneNotSupportedException {
        while (model.getSeedsToFind() - 1 >= currentSeedCount.get()) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            Long randomSeed = new Random().nextLong();
            //Clone the model every time we search to make sure we have proper data for each search
            biomeSearching((Searcher_Model) model.clone(), randomSeed);
        }
    }

    void biomeSearching(Searcher_Model model, long seed) throws InterruptedException {
        ////////////// World Source Creation ////////////////
        OverworldBiomeSource sourceO;
        if (model.getWorldType() == WorldType.LARGE_BIOMES) {
            sourceO = new OverworldBiomeSource(model.getSelectedVersion(), seed, 6, 4);
        } else {
            sourceO = new OverworldBiomeSource(model.getSelectedVersion(), seed);
        }
        NetherBiomeSource sourceN = new NetherBiomeSource(model.getSelectedVersion(), seed);
        EndBiomeSource sourceE = new EndBiomeSource(model.getSelectedVersion(), seed);

        ///////////// SpawnPoint Checking ////////////////

        int spawnPointX;
        int spawnPointZ;

        switch (model.getSpawnType()) {
            case APPROXIMATE:
                BPos spawnPoint = SpawnPoint.getApproximateSpawn(sourceO);
                spawnPointX = spawnPoint.getX();
                spawnPointZ = spawnPoint.getZ();
                break;
            case TRUESPAWN:
                OverworldTerrainGenerator ts = new OverworldTerrainGenerator(sourceO);
                BPos spawnPointT = SpawnPoint.getSpawn(ts);
                spawnPointX = spawnPointT.getX();
                spawnPointZ = spawnPointT.getZ();
                break;
            case ZERO_ZERO:
            default:
                spawnPointX = 0;
                spawnPointZ = 0;
                break;
        }


        ///////////// Biome Models ////////////////
        BiomeList_Model bModel = model.getBiomeList();
        BiomeSetList_Model bsModel = model.getBiomeSetList();


        ///////////// Checking World ////////////////
        for (int x = -model.getSearchRadius() + spawnPointX; x < model.getSearchRadius() + spawnPointX; x += model.getIncrementer()) {
            for (int z = -model.getSearchRadius() + spawnPointZ; z < model.getSearchRadius() + spawnPointZ; z += model.getIncrementer()) {

                //Takes all 3 world source objects (Overworld, Nether and End) and gets the biome at X and Z location for each world individually
                List<Biome> foundInWorlds = Arrays.asList(sourceO.getBiome(x, 0, z), sourceN.getBiome(x, 0, z), sourceE.getBiome(x, 0, z));

                //excludedSets Im not sure if they are even useful... it is here... not really sure how to test it
                //this might not be in the UI In the future
                /*
                for (BiomeSet_Model excludedSet : bsModel.getExcludedBiomeSet()) {
                    if (excludedSet.containsAny(foundInWorlds)) {
                        return;
                    }
                }
                 */

                //NOTE is this perfomant? Should I find a better way?
                //Checking if any biome set has the biome found. If so remove it from the included list
                for (BiomeSet_Model includedSet : bsModel.getIncludedBiomeSet()) {
                    if (includedSet.containsAny(foundInWorlds)) {

                        bsModel.getIncludedBiomeSet().remove(includedSet);
                        break;
                    }
                }

                //Returning if we found and excluded biome
                if (!Collections.disjoint(foundInWorlds, bModel.getExcludedBiomes())) {
                    return;
                }

                //Removing found biomes from included biomes
                bModel.getIncludedBiomes().removeAll(foundInWorlds);

                //Allowed to validate if no excluded biomes were passed in
                if (bModel.getExcludedBiomes().isEmpty() && validateSeed(seed, bModel, bsModel)) {
                    return;
                }
            }
        }

        // Validate the seed after it has finished all loops
        validateSeed(seed, bModel, bsModel);


    }

    boolean validateSeed(long seed, BiomeList_Model bModel, BiomeSetList_Model bsModel) {
        if (bModel.getIncludedBiomes().isEmpty() && bsModel.getIncludedBiomeSet().isEmpty()) {
            //System.out.format("Found biomes %d : Model %d\n ", foundBiomes.size(), bModel.getIncludedBiomes().size());

            // This is only because if what you are searching will pass pretty much everytime it can go over the limit
            // Ideally I would like to avoid this
            if (model.getSeedsToFind() - 1 >= currentSeedCount.get()) {
                System.out.format("%d: Found world seed %d\n ", currentSeedCount.incrementAndGet(), seed);
            }
            return true;
        }
        return false;
    }


    @Override
    public void run() {
        try {
            searching();
        } catch (IOException e) {
            System.out.println("IO Exception");
        } catch (InterruptedException e) {
            Launch.stopAllThreads();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}

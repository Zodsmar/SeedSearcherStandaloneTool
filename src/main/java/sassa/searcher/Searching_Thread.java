package sassa.searcher;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Main;
import sassa.enums.PassType;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.util.BiomeSources;
import sassa.util.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Searching_Thread extends Thread implements Runnable {
    Searcher_Model model;

    static AtomicInteger currentSeedCount = new AtomicInteger(0);

    long startFeatureSeed;
    long endFeatureSeed;

    public Searching_Thread(Searcher_Model model) {
        this.model = model;
    }

    public Searching_Thread(Searcher_Model model, long startFeatureSeed, long endFeatureSeed) {
        this.model = model;
        this.startFeatureSeed = startFeatureSeed;
        this.endFeatureSeed = endFeatureSeed;
    }

    private void searching() throws IOException, InterruptedException, CloneNotSupportedException {


        //Clone the model every time we search to make sure we have proper data for each search
        randomSearching(model);

    }

    void randomSearching(Searcher_Model model) throws InterruptedException {
        //This is required for feature searching
        ChunkRand rand = new ChunkRand();
        ThreadLocalRandom threadLocalRandomizer = ThreadLocalRandom.current();


        ///////////// Features ////////////////
        List<Feature_Model> featureList = model.getFeatureList();

        ///////////// Checking World ////////////////


        //TODO This is way to expensive of a call
        //Create a list of all possible structure seeds for this thread
        //List<Long> range = LongStream.range(startFeatureSeed, endFeatureSeed).boxed().collect(Collectors.toList());
        //Shuffle it, so it is random (This way searching will always yield different results)
        // Collections.shuffle(range);

        // For now this will loop within the range to make sure we stay within the range for this thread
        for (long structureSeedIncrementer = startFeatureSeed; structureSeedIncrementer < endFeatureSeed; structureSeedIncrementer++) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get()) {
                return;
            }

            //TODO This call can have duplicates, ideally we want to get the next random seed and make sure its not a duplicate
            // tried above but the call is way to expensive need to look back into this.
            long structureSeed = threadLocalRandomizer.nextLong(startFeatureSeed, endFeatureSeed);

            //Do the feature search and if the features we wanted exists it will return true along with all the chunk positions
            // if it returns false then the remaining code is passed, and it will start a new seed
            //Validate that all the structures we want to spawn are possible first or if no structures are wanted just continue to biomes

            Feature_Searcher feature_searcher = new Feature_Searcher(model);
            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = feature_searcher.featureSearch(featureList, BPos.ORIGIN, structureSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }

            //Now we convert the structure seed to a world seed and check the biomes
            for (long upperBits = 0; upperBits < 1L << model.getBiomePrecision(); upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;

                BiomeSources biomeSources = new BiomeSources(worldSeed);
                ///////////// SpawnPoint Checking ////////////////
                BPos spawnPoint = getSpawnPoint(worldSeed, biomeSources.getOverworldBiomeSource());

                if (!feature_searcher.featuresCanSpawn(checkingFeatures.getData(), biomeSources, rand)) {
                    continue;
                }

                Biome_Searcher biome_searcher = new Biome_Searcher(model);
                //Here we do a biome search, this can be done without structure searching
                if (!biome_searcher.biomeSearch(biomeSources, spawnPoint)) {
                    continue;
                }

                //Once we get here the seed should be valid at no point should it make it here if its not valid

                // Validate the seed after it has finished all loops
                outputSeed(worldSeed);
            }

        }

    }


    public BPos getSpawnPoint(long worldSeed, OverworldBiomeSource overworldBiomeSource) {

        BPos spawnPoint;
        switch (model.getSpawnType()) {
            case APPROXIMATE:
                spawnPoint = SpawnPoint.getApproximateSpawn(overworldBiomeSource);
                break;
            case TRUESPAWN:
                OverworldTerrainGenerator ts = new OverworldTerrainGenerator(overworldBiomeSource);
                spawnPoint = SpawnPoint.getSpawn(ts);
                break;
            case ZERO_ZERO:
            default:
                spawnPoint = BPos.ORIGIN;
                break;
        }
        return spawnPoint;
    }


    void outputSeed(long seed) {
        // This is only because if what you are searching will pass pretty much everytime it can go over the limit
        // Ideally I would like to avoid this
        if (model.getSeedsToFind() - 1 >= currentSeedCount.get()) {
            //TODO Have this talk to the GUI or update a variable somewhere to keep track of the seeds
            System.out.format("%d: Found world seed %d\n ", currentSeedCount.incrementAndGet(), seed);
        }
    }


    @Override
    public void run() {
        try {
            searching();
        } catch (IOException e) {
            System.out.println("IO Exception");
        } catch (InterruptedException e) {
            Main.stopAllThreads();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}

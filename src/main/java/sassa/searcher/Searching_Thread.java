package sassa.searcher;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import javafx.application.Platform;
import sassa.Main;
import sassa.enums.PassType;
import sassa.enums.SpawnType;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.ui.ui_application;
import sassa.util.BiomeSources;
import sassa.util.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Searching_Thread extends Thread implements Runnable {
    Searcher_Model model;
    Biome_Searcher biome_searcher;
    Feature_Searcher feature_searcher;
    ThreadLocalRandom threadLocalRandomizer = ThreadLocalRandom.current();
    ChunkRand rand = new ChunkRand();


    List<Long> seeds;

    public static AtomicInteger currentSeedCount = new AtomicInteger(0);

    int id;

    public Searching_Thread(Searcher_Model model, int id) {
        this.model = model;
        this.id = id;
    }

    public Searching_Thread(Searcher_Model model, int id, List<Long> seeds) {
        this.model = model;
        this.id = id;
        this.seeds = seeds;
    }


    private void searching() throws IOException, InterruptedException, CloneNotSupportedException {

        biome_searcher = new Biome_Searcher(model);
        feature_searcher = new Feature_Searcher(model);

        //Clone the model every time we search to make sure we have proper data for each search
        switch (model.getSearchType()) {
            case RANDOM_SEARCH:
                if (!model.getFeatureList().isEmpty()) {
                    if (model.getSpawnType() == SpawnType.ZERO_ZERO) {
                        randomSearchingWithFeature();
                    } else {
                        randomSearchingWithFeatureAndSpawn();
                    }

                } else {
                    randomSearching();
                }
                break;
            case SET_SEED_SEARCH:
                setSeedSearch();
                break;
            case RANGE_SEARCH:
                rangeSearch();
                break;
        }
    }


    void randomSearchingWithFeature() {

        long startFeatureSeed = (long) Math.floor(Math.pow(2, 48) / model.getThreadsToUse() * id);
        long endFeatureSeed = Math.min((long) Math.floor(Math.pow(2, 48) / model.getThreadsToUse() * (id + 1)), 1L << 48);

        ///////////// Checking World ////////////////

        //TODO This is way to expensive of a call
        //Create a list of all possible structure seeds for this thread
        //List<Long> range = LongStream.range(startFeatureSeed, endFeatureSeed).boxed().collect(Collectors.toList());
        //Shuffle it, so it is random (This way searching will always yield different results)
        // Collections.shuffle(range);

        // For now this will loop within the range to make sure we stay within the range for this thread
        for (long structureSeedIncrementer = startFeatureSeed; structureSeedIncrementer < endFeatureSeed; structureSeedIncrementer++) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }

            //TODO This call can have duplicates, ideally we want to get the next random seed and make sure its not a duplicate
            // tried above but the call is way to expensive need to look back into this.
            long structureSeed = threadLocalRandomizer.nextLong(startFeatureSeed, endFeatureSeed);

            //Do the feature search and if the features we wanted exists it will return true along with all the chunk positions
            // if it returns false then the remaining code is passed, and it will start a new seed
            //Validate that all the structures we want to spawn are possible first or if no structures are wanted just continue to biomes


            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = feature_searcher.featureSearch(BPos.ORIGIN, structureSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }

            //Now we convert the structure seed to a world seed and check the biomes
            //TODO check that this is actually correct. I dont seem to be getting negative seeds or even long seeds
            for (long upperBits = 0; upperBits < 1L << model.getBiomePrecision(); upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;


                if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                    return;
                }

                BiomeSources biomeSources = new BiomeSources(worldSeed);
                ///////////// SpawnPoint Checking ////////////////
                BPos spawnPoint = getSpawnPoint(biomeSources.getOverworldBiomeSource());

                if (!feature_searcher.featuresCanSpawn(checkingFeatures.getData(), biomeSources, rand)) {
                    continue;
                }


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


    void randomSearchingWithFeatureAndSpawn() {

        long startId = -Long.MAX_VALUE;
        long endId = Long.MAX_VALUE;
        long range = Math.abs((long) ((endId) / ((double) model.getThreadsToUse() / 2))) + 1;
        long startSeed = startId + range * id;
        long endSeed;
        if (id == model.getThreadsToUse() - 1) {
            endSeed = endId;
        } else {
            endSeed = startId + range * (id + 1);
        }

        // For now this will loop within the range to make sure we stay within the range for this thread
        //TODO pass in the ranges which the world seed can be in for now its duplicate
        for (long seedIncrementer = startSeed; seedIncrementer < endSeed; seedIncrementer++) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }

            long worldSeed = threadLocalRandomizer.nextLong(startSeed, endSeed);

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }
            BiomeSources biomeSources = new BiomeSources(worldSeed);
            ///////////// SpawnPoint Checking ////////////////
            BPos spawnPoint = getSpawnPoint(biomeSources.getOverworldBiomeSource());


            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = feature_searcher.featureSearch(spawnPoint, worldSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }

            if (!feature_searcher.featuresCanSpawn(checkingFeatures.getData(), biomeSources, rand)) {
                continue;
            }

            //Here we do a biome search, this can be done without structure searching
            if (!biome_searcher.biomeSearch(biomeSources, spawnPoint)) {
                continue;
            }

            //Once we get here the seed should be valid at no point should it make it here if its not valid

            // Validate the seed after it has finished all loops
            outputSeed(worldSeed);
        }


    }

    void randomSearching() {

//        long startSeed = (long) Math.floor(Math.pow(2, 64) / model.getThreadsToUse() * id);
//        long endSeed = Math.min((long) Math.floor(Math.pow(2, 64) / model.getThreadsToUse() * (id + 1)), 1L << 64);

        //System.out.println(startSeed + "     " + endSeed);
        ///////////// Checking World ////////////////

        //This could be done once and passed into the threads
        long startId = -Long.MAX_VALUE;
        long endId = Long.MAX_VALUE;
        long range = Math.abs((long) ((endId) / ((double) model.getThreadsToUse() / 2))) + 1;
        long startSeed = startId + range * id;
        long endSeed;
        if (id == model.getThreadsToUse() - 1) {
            endSeed = endId;
        } else {
            endSeed = startId + range * (id + 1);
        }

        // For now this will loop within the range to make sure we stay within the range for this thread
        //TODO pass in the ranges which the world seed can be in for now its duplicate
        for (long seedIncrementer = startSeed; seedIncrementer < endSeed; seedIncrementer++) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }

            long worldSeed = threadLocalRandomizer.nextLong(startSeed, endSeed);

            BiomeSources biomeSources = new BiomeSources(worldSeed);
            ///////////// SpawnPoint Checking ////////////////
            BPos spawnPoint = getSpawnPoint(biomeSources.getOverworldBiomeSource());

            //Here we do a biome search, this can be done without structure searching
            if (!biome_searcher.biomeSearch(biomeSources, spawnPoint)) {
                continue;
            }

            //Once we get here the seed should be valid at no point should it make it here if its not valid

            // Validate the seed after it has finished all loops
            outputSeed(worldSeed);
        }
    }

    void rangeSearch() {

        // For now this will loop within the range to make sure we stay within the range for this thread
        for (long currentSeed = model.getStartRange() + id; currentSeed < model.getEndRange(); currentSeed += model.getThreadsToUse()) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }

            BiomeSources biomeSources = new BiomeSources(currentSeed);
            BPos spawnPoint = getSpawnPoint(biomeSources.getOverworldBiomeSource());

            Feature_Searcher feature_searcher = new Feature_Searcher(model);
            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = feature_searcher.featureSearch(spawnPoint, currentSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }


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
            outputSeed(currentSeed);
        }
    }

    void setSeedSearch() {

        // For now this will loop within the range to make sure we stay within the range for this thread
        for (int count = 0 + id; count < seeds.size(); count += model.getThreadsToUse()) {

            if (model.getSeedsToFind() - 1 < currentSeedCount.get() || Thread.interrupted()) {
                return;
            }

            long currentSeed = seeds.get(count);

            BiomeSources biomeSources = new BiomeSources(currentSeed);
            BPos spawnPoint = getSpawnPoint(biomeSources.getOverworldBiomeSource());

            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = feature_searcher.featureSearch(spawnPoint, currentSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }


            if (!feature_searcher.featuresCanSpawn(checkingFeatures.getData(), biomeSources, rand)) {
                continue;
            }

            //Here we do a biome search, this can be done without structure searching
            if (!biome_searcher.biomeSearch(biomeSources, spawnPoint)) {
                continue;
            }

            //Once we get here the seed should be valid at no point should it make it here if its not valid

            // Validate the seed after it has finished all loops
            outputSeed(currentSeed);
        }
    }


    public BPos getSpawnPoint(OverworldBiomeSource overworldBiomeSource) {

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


    synchronized void outputSeed(long seed) {
        // This is only because if what you are searching will pass pretty much everytime it can go over the limit
        // Ideally I would like to avoid this
        if (model.getSeedsToFind() - 1 >= currentSeedCount.get() && !Thread.interrupted()) {
            //TODO Have this talk to the GUI or update a variable somewhere to keep track of the seeds

            //Spawn the seed
            try {
                ui_application.mainController.spawnSeed(String.valueOf(seed));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Show it in console
            System.out.format("%d: Found world seed %d\n ", currentSeedCount.incrementAndGet(), seed);

            //Once we finish we need to reset the stop button
            if (currentSeedCount.get() >= model.getSeedsToFind() - 1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ui_application.mainController.stopButton();
                    }
                });
            }


        }
    }


    @Override
    public void run() {
        try {
            Searching_Thread.currentSeedCount.set(0);
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

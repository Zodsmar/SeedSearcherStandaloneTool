package sassa.searcher;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.GenerationContext;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Launch;
import sassa.enums.WorldType;
import sassa.models.BiomeList_Model;
import sassa.models.BiomeSetList_Model;
import sassa.models.BiomeSet_Model;
import sassa.models.Searcher_Model;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Searching_Thread extends Thread implements Runnable {
    Searcher_Model model;

    static AtomicInteger currentSeedCount = new AtomicInteger(0);

    long startFeatureSeed;
    long endFeatureSeed;

    public Searching_Thread(Searcher_Model model, long startFeatureSeed, long endFeatureSeed) {
        this.model = model;
        this.startFeatureSeed = startFeatureSeed;
        this.endFeatureSeed = endFeatureSeed;
    }

    private void searching() throws IOException, InterruptedException, CloneNotSupportedException {
        while (model.getSeedsToFind() - 1 >= currentSeedCount.get()) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            //Clone the model every time we search to make sure we have proper data for each search
            randomSearching((Searcher_Model) model.clone());
        }
    }

    void randomSearching(Searcher_Model model) throws InterruptedException {

        ////////////// Init Values Creation ////////////////

        //This is required for feature searching
        ChunkRand rand = new ChunkRand();
        ThreadLocalRandom threadLocalRandomizer = ThreadLocalRandom.current();


        ///////////// Biome Models ////////////////
        BiomeList_Model bModel = model.getBiomeList();
        BiomeSetList_Model bsModel = model.getBiomeSetList();

        ///////////// Features ////////////////
        List<Feature> featureList = model.getFeatureList();


        ///////////// Checking World ////////////////


        //TODO This is way to expensive of a call
        //Create a list of all possible structure seeds for this thread
        //List<Long> range = LongStream.range(startFeatureSeed, endFeatureSeed).boxed().collect(Collectors.toList());
        //Shuffle it, so it is random (This way searching will always yield different results)
        // Collections.shuffle(range);

        // For now this will loop within the range to make sure we stay within the range for this thread
        for (long structureSeedIncrementer = startFeatureSeed; structureSeedIncrementer < endFeatureSeed; structureSeedIncrementer++) {
            start:
            {
                //TODO This call can have duplicates, ideally we want to get the next random seed and make sure its not a duplicate
                // tried above but the call is way to expensive need to look back into this.
                long structureSeed = threadLocalRandomizer.nextLong(startFeatureSeed, endFeatureSeed);

                //Do the feature search and if the features we wanted exists it will return true along with all the chunk positions
                // if it returns false then the remaining code is passed, and it will start a new seed
                HashMap<Boolean, HashMap<Feature, List<CPos>>> checkingFeatures = featureSearch(featureList, structureSeed, rand);
                boolean shouldContinue = checkingFeatures.containsKey(true);

                //Validate that all the structures we want to spawn are possible first or if no structures are wanted just continue to biomes
                if (shouldContinue || featureList.size() == 0) {
                    Map<Feature, List<CPos>> foundFeatures = checkingFeatures.get(true);
                    //Now we convert the structure seed to a world seed and check the biomes
                    for (long upperBits = 0; upperBits < 1L << model.getBiomePrecision(); upperBits++) {
                        long worldSeed = (upperBits << 48) | structureSeed;

                        //At this point we can now get the spawnpoint and world types
                        GenerationContext.Context contextO = GenerationContext.getContext(worldSeed, Dimension.OVERWORLD, model.getSelectedVersion());
                        OverworldBiomeSource sourceO = (OverworldBiomeSource) contextO.getBiomeSource();
                        if (model.getWorldType() == WorldType.LARGE_BIOMES) {
                            sourceO.biomeSize = 6;
                        }

                        GenerationContext.Context contextN = GenerationContext.getContext(worldSeed, Dimension.NETHER, model.getSelectedVersion());
                        NetherBiomeSource sourceN = (NetherBiomeSource) contextN.getBiomeSource();

                        GenerationContext.Context contextE = GenerationContext.getContext(worldSeed, Dimension.END, model.getSelectedVersion());
                        EndBiomeSource sourceE = (EndBiomeSource) contextE.getBiomeSource();

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
                        List<Feature> spawnedFeatures = new ArrayList<>();
                        for (HashMap.Entry<Feature, List<CPos>> feature : foundFeatures.entrySet()) {

                            //This is a check again to make sure we are working with structures not decorators
                            RegionStructure structure;
                            if ((structure = (RegionStructure) feature.getKey()) instanceof RegionStructure) {
                                for (CPos chunkPos : feature.getValue()) {

                                    if (!structure.canSpawn(chunkPos, sourceO) && !structure.canSpawn(chunkPos, sourceN) && !structure.canSpawn(chunkPos, sourceE))
                                        continue;

                                    //TODO For now this is only checking if 1 structure exists, need to bring back multi searching
                                    spawnedFeatures.add(structure);
                                    break;
                                }
                            }
                        }

                        //If we missed even one feature from our list the seed is invalid and start a new seed
                        if (spawnedFeatures.size() != featureList.size()) {
                            break;
                        }

                        List<Biome> foundIncludedBiomes = new ArrayList<>();
                        List<BiomeSet_Model> foundIncludedBiomeSets = new ArrayList<>();
                        ///////////// Checking Biomes ////////////////
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
                                    if (includedSet.containsAny(foundInWorlds) && !foundIncludedBiomeSets.contains(includedSet)) {

                                        foundIncludedBiomeSets.add(includedSet);
                                        break;
                                    }
                                }

                                //Returning if we found and excluded biome
                                if (!Collections.disjoint(foundInWorlds, bModel.getExcludedBiomes())) {
                                    break start;
                                }

                                //Removing found biomes from included biomes
                                //bModel.getIncludedBiomes().removeAll(foundInWorlds);

                                //Todo write my own containsAny function
                                foundInWorlds.forEach(biome -> {
                                    if (model.getBiomeList().getIncludedBiomes().contains(biome) && !foundIncludedBiomes.contains(biome)) {
                                        foundIncludedBiomes.add(biome);
                                    }
                                });


                                //Allowed to validate if no excluded biomes were passed in
                                if (bModel.getExcludedBiomes().isEmpty() && validateSeed(worldSeed, foundIncludedBiomes, foundIncludedBiomeSets, spawnedFeatures)) {
                                    return;
                                }
                            }
                        }

                        // Validate the seed after it has finished all loops
                        validateSeed(worldSeed, foundIncludedBiomes, foundIncludedBiomeSets, spawnedFeatures);
                    }
                }
            }
        }
    }


    HashMap<Boolean, HashMap<Feature, List<CPos>>> featureSearch(List<Feature> featureList, long structureSeed, ChunkRand rand) {
        HashMap<Boolean, HashMap<Feature, List<CPos>>> data = new HashMap<>();

        //This is a list of the features that were found. This should match the model of features you want to find once we finish the for loop
        HashMap<Feature, List<CPos>> foundFeatures = new HashMap<>();
        for (Feature feature : model.getFeatureList()) {

            RegionStructure structure;
            //Structure strongholdData;
            // Checks if we are looking at a structure or a decorator

            if ((structure = (RegionStructure) feature) instanceof RegionStructure) {
                //Get the lower and upperbound of the chunks possible

                //It is not possible to get the spawn location off of a structure seed. There will be a small chance the structures are a little outside the search radius
                RegionStructure.Data<?> lowerBound = structure.at(-model.getSearchRadius() >> 4, -model.getSearchRadius() >> 4);
                RegionStructure.Data<?> upperBound = structure.at(model.getSearchRadius() >> 4, model.getSearchRadius() >> 4);
//                RegionStructure.Data<?> lowerBound = structure.at(-model.getSearchRadius() >> 4 + spawnPointX >> 4, -model.getSearchRadius() >> 4 + spawnPointZ >> 4);
//                RegionStructure.Data<?> upperBound = structure.at(model.getSearchRadius() >> 4 + spawnPointX >> 4, model.getSearchRadius() >> 4 + spawnPointZ >> 4);

                //Possible chunks the structure can spawn in but we need to check against biomes. In the seed theoretically this can spawn but if the biomes don't
                //line up it won't be possible
                List<CPos> possibleChunks = new ArrayList<>();
                for (int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
                    for (int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                        CPos struct = structure.getInRegion(structureSeed, regionX, regionZ, rand);
                        if (struct == null) continue;
                        // TODO Why is this line needed
                        if (struct.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > model.getSearchRadius() >> 4)
                            continue;

                        // This will keep finding all of the structure of that type

                        possibleChunks.add(struct);
                    }
                }

                //At this point if we found no possible structures, break back to the structure seed and start again
                if (possibleChunks.isEmpty()) break;
                foundFeatures.put(feature, possibleChunks);
            }

//            if ((strongholdData = (Structure) feature) instanceof Structure) {
//
//            }
        }

        //At this point if the features we found are not the same size and the features we want to find break and go to the next seed
        //TODO this does not take into considerations the amount of a structure we want to find
        if (foundFeatures.size() != featureList.size()) {
            data.put(false, foundFeatures);
            return data;
        }

        //Now that we know the features we want can exist in the structure seed we need to check the biome seed
        data.put(true, foundFeatures);
        return data;
    }


    boolean validateSeed(long seed, List<Biome> foundIncludedBiomes, List<BiomeSet_Model> foundIncludedBiomeSets, List<Feature> foundFeatures) {
        //We need to pass in the found features to make sure, the size matches. Since we checking that before biomes, we can't rely on the featurelist being zero
        //TODO See if I can do the same thing with found features and remove it as they are found
        if (foundIncludedBiomes.size() == model.getBiomeList().getIncludedBiomes().size() &&
                foundIncludedBiomeSets.size() == model.getBiomeSetList().getIncludedBiomeSet().size() &&
                foundFeatures.size() == model.getFeatureList().size()) {

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

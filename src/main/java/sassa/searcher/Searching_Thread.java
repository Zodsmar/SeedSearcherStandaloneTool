package sassa.searcher;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.SpiralIterator;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.Decorator;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Main;
import sassa.enums.BiomeListType;
import sassa.enums.PassType;
import sassa.models.BiomeList_Model;
import sassa.models.BiomeSetList_Model;
import sassa.models.BiomeSet_Model;
import sassa.models.Searcher_Model;
import sassa.util.BiomeSources;
import sassa.util.Result;

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


        //Clone the model every time we search to make sure we have proper data for each search
        randomSearching(model);

    }

    void randomSearching(Searcher_Model model) throws InterruptedException {

        ////////////// Init Values Creation ////////////////

        //This is required for feature searching
        ChunkRand rand = new ChunkRand();
        ThreadLocalRandom threadLocalRandomizer = ThreadLocalRandom.current();


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

            if (model.getSeedsToFind() - 1 < currentSeedCount.get()) {
                return;
            }

            //TODO This call can have duplicates, ideally we want to get the next random seed and make sure its not a duplicate
            // tried above but the call is way to expensive need to look back into this.
            long structureSeed = threadLocalRandomizer.nextLong(startFeatureSeed, endFeatureSeed);

            //Do the feature search and if the features we wanted exists it will return true along with all the chunk positions
            // if it returns false then the remaining code is passed, and it will start a new seed

            //Validate that all the structures we want to spawn are possible first or if no structures are wanted just continue to biomes
            //TODO if the featurelist is empty we shouldn't even do structure seed searching... no point
            Result<PassType, HashMap<Feature, List<Feature.Data<?>>>> checkingFeatures = featureSearch(featureList, BPos.ORIGIN, structureSeed, rand);
            if (checkingFeatures.isFailure()) {
                continue;
            }

            //Now we convert the structure seed to a world seed and check the biomes
            for (long upperBits = 0; upperBits < 1L << model.getBiomePrecision(); upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;

                BiomeSources biomeSources = new BiomeSources(worldSeed);
                ///////////// SpawnPoint Checking ////////////////
                BPos spawnPoint = getSpawnPoint(worldSeed, biomeSources.getOverworldBiomeSource());

                if (!featuresCanSpawn(checkingFeatures.getData(), biomeSources, rand)) {
                    continue;
                }

                //Here we do a biome search, this can be done without structure searching
                if (!biomeSearch(biomeSources, spawnPoint)) {
                    continue;
                }

                //Once we get here the seed should be valid at no point should it make it here if its not valid

                // Validate the seed after it has finished all loops
                outputSeed(worldSeed);
            }

        }

    }

    boolean featuresCanSpawn(Map<Feature, List<Feature.Data<?>>> featurePossibleSpawn, BiomeSources biomeSources, ChunkRand rand) {

        List<Feature> spawnedFeatures = new ArrayList<>();
        for (HashMap.Entry<Feature, List<Feature.Data<?>>> feature : featurePossibleSpawn.entrySet()) {
            Feature curFeature = feature.getKey();

            //If we are a regionStructure or a Decorator
            if (curFeature instanceof RegionStructure || curFeature instanceof Decorator) {
                //TODO check Stronghold vs regionstructure with data
                for (Feature.Data<?> data : feature.getValue()) {

                    if (!curFeature.canSpawn(data, biomeSources.getOverworldBiomeSource()) && !curFeature.canSpawn(data, biomeSources.getNetherBiomeSource()) && !curFeature.canSpawn(data, biomeSources.getEndBiomeSource()))
                        continue;

                    //TODO For now this is only checking if 1 structure exists, need to bring back multi searching
                    spawnedFeatures.add(feature.getKey());
                    break;
                }
            }

            //If we are a stronghold. This is a special catch case
            if (feature.getKey() instanceof Stronghold) {
                Stronghold stronghold;
                stronghold = (Stronghold) curFeature;
                int c = 3;
                CPos[] cposes = new CPos[0];
                while (true) {
                    if (c > stronghold.getCount()) break;
                    cposes = stronghold.getStarts(biomeSources.getOverworldBiomeSource(), c, rand);
                    if (cposes[cposes.length - 1].distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > model.getSearchRadius() >> 4)
                        break;
                    spawnedFeatures.add(stronghold);
                    c += stronghold.getSpread();
                }
            }
            //TODO if all structures are found we can break out faster (same as biomes) need to add the check
        }

        //If we missed even one feature from our list the seed is invalid and start a new seed
        if (spawnedFeatures.size() != Main.defaultModel.getFeatureList().size()) {
            return false;
        }
        return true;
    }

    //TODO Do I want to be passing the model in for testing?
    boolean biomeSearch(BiomeSources biomeSources, BPos spawnPoint) {
        BiomeList_Model foundBiomes = new BiomeList_Model();
        BiomeSetList_Model foundBiomeSets = new BiomeSetList_Model();


        ///////////// Biome Models ////////////////
        BiomeList_Model bModel = Main.defaultModel.getBiomeList();
        BiomeSetList_Model bsModel = Main.defaultModel.getBiomeSetList();

        //If no biomes were selected aka only structure searching, kickout before even checking, because the seed is valid
        if (Main.defaultModel.isAllBiomeEmpty()) {
            return true;
        }

        ///////////// Checking Biomes ////////////////
        for (int x = -Main.defaultModel.getSearchRadius() + spawnPoint.getX(); x < Main.defaultModel.getSearchRadius() + spawnPoint.getX(); x += Main.defaultModel.getIncrementer()) {
            for (int z = -Main.defaultModel.getSearchRadius() + spawnPoint.getZ(); z < Main.defaultModel.getSearchRadius() + spawnPoint.getZ(); z += Main.defaultModel.getIncrementer()) {

                //Takes all 3 world source objects (Overworld, Nether and End) and gets the biome at X and Z location for each world individually
                List<Biome> foundInWorlds = Arrays.asList(biomeSources.getOverworldBiomeSource().getBiome(x, 0, z), biomeSources.getNetherBiomeSource().getBiome(x, 0, z), biomeSources.getEndBiomeSource().getBiome(x, 0, z));

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
                    if (includedSet.containsAny(foundInWorlds) && !foundBiomeSets.getIncludedBiomeSet().contains(includedSet)) {

                        foundBiomeSets.addBiomeSetList(includedSet, BiomeListType.INCLUDED);
                        break;
                    }
                }

                //Returning if we found and excluded biome
                if (!Collections.disjoint(foundInWorlds, bModel.getExcludedBiomes())) {
                    return false;
                }

                //Removing found biomes from included biomes
                //bModel.getIncludedBiomes().removeAll(foundInWorlds);

                //Todo write my own containsAny function
                foundInWorlds.forEach(biome -> {
                    if (Main.defaultModel.getBiomeList().getIncludedBiomes().contains(biome) && !foundBiomes.getIncludedBiomes().contains(biome)) {
                        foundBiomes.addBiome(biome, BiomeListType.INCLUDED);
                    }
                });

                if (validateBiome(foundBiomes, foundBiomeSets)) {
                    return true;
                }
            }
        }

        return validateBiome(foundBiomes, foundBiomeSets);
    }

    boolean validateBiome(BiomeList_Model foundBiomes, BiomeSetList_Model foundBiomeSets) {
        if (foundBiomes.getExcludedBiomes().isEmpty() &&
                foundBiomes.getIncludedBiomes().size() == Main.defaultModel.getBiomeList().getIncludedBiomes().size() &&
                foundBiomeSets.getIncludedBiomeSet().size() == model.getBiomeSetList().getIncludedBiomeSet().size()) {
            return true;
        }

        return false;
    }


    Result<PassType, HashMap<Feature, List<Feature.Data<?>>>> featureSearch(List<Feature> featureList, BPos origin, long seed, ChunkRand rand) {
        Result<PassType, HashMap<Feature, List<Feature.Data<?>>>> data = new Result<>();

        //This is a list of the features that were found. This should match the model of features you want to find once we finish the for loop
        HashMap<Feature, List<Feature.Data<?>>> foundFeatures = new HashMap<>();
        for (Feature feature : featureList) {

            // Checks if we are looking at a structure or a decorator

            if (feature instanceof RegionStructure) {
                RegionStructure structure = (RegionStructure) feature;

                //Get the lower and upperbound of the chunks possible
                List<Feature.Data<?>> possibleChunks = new ArrayList<>();

                int chunkInRegion = structure.getSpacing();
                int regionSize = chunkInRegion * 16;
                SpiralIterator<RPos> spiralIterator = new SpiralIterator<>(
                        new RPos(origin.toRegionPos(regionSize).getX(), origin.toRegionPos(regionSize).getZ(), regionSize),
                        new BPos(-model.getSearchRadius(), 0, -model.getSearchRadius()).toRegionPos(regionSize), new BPos(model.getSearchRadius(), 0, model.getSearchRadius()).toRegionPos(regionSize),
                        1, (x, y, z) -> new RPos(x, z, regionSize)
                );
                spiralIterator.forEach(rPos -> {
                    CPos cpos = structure.getInRegion(seed, rPos.getX(), rPos.getZ(), rand);
                    //TODO pass back data instead

                    if (cpos == null || cpos.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > model.getSearchRadius() >> 4) {
                        return;
                    }
                    Feature.Data<?> featureData = new RegionStructure.Data<>(structure, cpos.getX(), cpos.getY());
                    //The structure can spawn here need to check against biomes
                    possibleChunks.add(featureData);

                });

                //At this point if we found no possible structures, break back to the structure seed and start again
                if (possibleChunks.isEmpty()) break;
                foundFeatures.put(feature, possibleChunks);
            }
            if (feature instanceof Stronghold) {

                foundFeatures.put(feature, new ArrayList<>());
            }

        }

        //At this point if the features we found are not the same size and the features we want to find break and go to the next seed
        //TODO this does not take into considerations the amount of a structure we want to find
        if (foundFeatures.size() != featureList.size()) {
            data.set(PassType.FAIL, foundFeatures);
            return data;
        }

        //Now that we know the features we want can exist in the structure seed we need to check the biome seed
        data.set(PassType.SUCCESS, foundFeatures);
        return data;
    }

    BPos getSpawnPoint(long worldSeed, OverworldBiomeSource overworldBiomeSource) {

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

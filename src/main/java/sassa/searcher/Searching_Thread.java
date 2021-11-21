package sassa.searcher;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.Decorator;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.Igloo;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcfeature.structure.Village;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Main;
import sassa.enums.BiomeListType;
import sassa.enums.PassType;
import sassa.models.*;
import sassa.models.features.NERuinedPortal;
import sassa.models.features.OWRuinedPortal;
import sassa.util.BiomeSources;
import sassa.util.Result;
import sassa.util.StructureHelper;

import java.io.IOException;
import java.util.*;
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

        ////////////// Init Values Creation ////////////////

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
            //TODO if the featurelist is empty we shouldn't even do structure seed searching... no point
            Result<PassType, HashMap<Feature_Model, List<CPos>>> checkingFeatures = featureSearch(featureList, BPos.ORIGIN, structureSeed, rand);
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

    public boolean featuresCanSpawn(Map<Feature_Model, List<CPos>> featurePossibleSpawn, BiomeSources biomeSources, ChunkRand rand) {

        List<Feature> spawnedFeatures = new ArrayList<>();
        for (HashMap.Entry<Feature_Model, List<CPos>> feature : featurePossibleSpawn.entrySet()) {
            Feature curFeature = feature.getKey().getFeature();

            //If we are a regionStructure or a Decorator
            if (curFeature instanceof RegionStructure || curFeature instanceof Decorator) {
                RegionStructure<?, ?> regionStructure = (RegionStructure) curFeature;
                int spawnable = 0;
                for (CPos cpos : feature.getValue()) {
                    //First check if the structure can spawn
                    if (!regionStructure.canSpawn(cpos, biomeSources.getOverworldBiomeSource()) && !regionStructure.canSpawn(cpos, biomeSources.getNetherBiomeSource()) && !regionStructure.canSpawn(cpos, biomeSources.getEndBiomeSource()))
                        continue;
                    //Ruined Portals can spawn but sometimes wont generate so we need a catch case for it
                    if (regionStructure instanceof OWRuinedPortal) {
                        OWRuinedPortal portal = (OWRuinedPortal) regionStructure;
                        if (!portal.canGenerate(cpos, biomeSources.getOverworldTerrainGenerator()))
                            continue;
                    }

                    if (regionStructure instanceof NERuinedPortal) {
                        NERuinedPortal portal = (NERuinedPortal) regionStructure;
                        if (!portal.canGenerate(cpos, biomeSources.getNetherTerrainGenerator()))
                            continue;
                    }

                    //TODO zombie villages work but if you look for villages and zombie villages, zombie villages also count as normal villages
                    if (feature.getKey().getFeatureAsString().equals("ZombieVillage")) {
                        Village village = (Village) feature.getKey().getFeature();
                        if (!village.isZombieVillage(biomeSources.getWorldSeed(), cpos, rand)) continue;
                    }
                    //TODO same problem as zombie villages
                    if (feature.getKey().getFeatureAsString().equals("IglooBasement")) {
                        Igloo igloo = (Igloo) feature.getKey().getFeature();
                        if (!igloo.hasBasement(biomeSources.getWorldSeed(), cpos, rand)) continue;
                    }

                    spawnable++;
                    if (spawnable >= feature.getKey().getAmount()) {
                        spawnedFeatures.add(curFeature);
                        break;
                    }
                }
            }

            //If we are a stronghold. This is a special catch case
            //TODO make strongholds work with multiple amounts
            if (curFeature instanceof Stronghold) {
                Stronghold stronghold;
                stronghold = (Stronghold) curFeature;
                int c = 3;
                CPos[] cposes;
                while (true) {
                    if (c > stronghold.getCount()) break;
                    cposes = stronghold.getStarts(biomeSources.getOverworldBiomeSource(), c, rand);
                    if (cposes[cposes.length - 1].distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > model.getSearchRadius() >> 4)
                        break;
                    spawnedFeatures.add(stronghold);
                    c += stronghold.getSpread();
                }
            }
            //if all structures are found we can break out faster (same as biomes)
            if (spawnedFeatures.size() == this.model.getFeatureList().size()) {
                return true;
            }
        }

        //If we missed even one feature from our list the seed is invalid and start a new seed
        if (spawnedFeatures.size() != this.model.getFeatureList().size()) {
            return false;
        }
        return true;
    }

    //TODO Do I want to be passing the model in for testing?
    public boolean biomeSearch(BiomeSources biomeSources, BPos spawnPoint) {
        BiomeList_Model foundBiomes = new BiomeList_Model();
        BiomeSetList_Model foundBiomeSets = new BiomeSetList_Model();


        ///////////// Biome Models ////////////////
        BiomeList_Model bModel = this.model.getBiomeList();
        BiomeSetList_Model bsModel = this.model.getBiomeSetList();

        //If no biomes were selected aka only structure searching, kickout before even checking, because the seed is valid
        if (this.model.isAllBiomeEmpty()) {
            return true;
        }

        ///////////// Checking Biomes ////////////////
        for (int x = -this.model.getSearchRadius() + spawnPoint.getX(); x < this.model.getSearchRadius() + spawnPoint.getX(); x += this.model.getIncrementer()) {
            for (int z = -this.model.getSearchRadius() + spawnPoint.getZ(); z < this.model.getSearchRadius() + spawnPoint.getZ(); z += this.model.getIncrementer()) {

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
                    if (this.model.getBiomeList().getIncludedBiomes().contains(biome) && !foundBiomes.getIncludedBiomes().contains(biome)) {
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
                foundBiomes.getIncludedBiomes().size() == this.model.getBiomeList().getIncludedBiomes().size() &&
                foundBiomeSets.getIncludedBiomeSet().size() == this.model.getBiomeSetList().getIncludedBiomeSet().size()) {
            return true;
        }

        return false;
    }


    public Result<PassType, HashMap<Feature_Model, List<CPos>>> featureSearch(List<Feature_Model> featureList, BPos origin, long seed, ChunkRand rand) {
        Result<PassType, HashMap<Feature_Model, List<CPos>>> data = new Result<>();

        //This is a list of the features that were found. This should match the model of features you want to find once we finish the for loop
        HashMap<Feature_Model, List<CPos>> foundFeatures = new HashMap<>();
        for (Feature_Model featureModel : featureList) {
            Feature feature = featureModel.getFeature();
            // Checks if we are looking at a structure or a decorator

            if (feature instanceof RegionStructure) {
                RegionStructure structure = (RegionStructure) feature;

                //Get the lower and upperbound of the chunks possible
                List<CPos> possibleChunks = new ArrayList<>();

                int chunkInRegion = structure.getSpacing();
                int regionSize = chunkInRegion * 16;
                RPos regionOrigin = origin.toRegionPos(regionSize);
                RPos lowerBound = new BPos(-this.model.getSearchRadius(), 0, -this.model.getSearchRadius()).toRegionPos(regionSize);
                RPos upperBound = new BPos(this.model.getSearchRadius(), 0, this.model.getSearchRadius()).toRegionPos(regionSize);
                StructureHelper.SpiralIterator spiralIterator = new StructureHelper.SpiralIterator(
                        regionOrigin,
                        lowerBound,
                        upperBound
                );
                spiralIterator.forEach(rPos -> {
                    CPos cpos = structure.getInRegion(seed, rPos.getX(), rPos.getZ(), rand);

                    if (cpos == null || cpos.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > this.model.getSearchRadius() >> 4) {
                        return;
                    }

                    //The structure can spawn here need to check against biomes
                    possibleChunks.add(cpos);

                });

                //At this point if we found no possible structures, break back to the structure seed and start again
                if (possibleChunks.isEmpty()) break;

                //Check if there are enough possible chunk positions for the structure to spawn. (ex. if we want 2 structures and only 1 location
                //is possible for it to spawn then we know the seed isn't valid)
                if (possibleChunks.size() < featureModel.getAmount()) break;
                foundFeatures.put(featureModel, possibleChunks);
            }

            if (feature instanceof Decorator) {

            }

            if (feature instanceof Stronghold) {

                foundFeatures.put(featureModel, new ArrayList<>());
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

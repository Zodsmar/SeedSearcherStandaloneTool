package sassa.searcher;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.Decorator;
import com.seedfinding.mcfeature.structure.Igloo;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcfeature.structure.Village;
import sassa.enums.PassType;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.models.features.NERuinedPortal;
import sassa.models.features.OWRuinedPortal;
import sassa.util.BiomeSources;
import sassa.util.Result;
import sassa.util.StructureHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feature_Searcher {
    private Searcher_Model model;

    public Feature_Searcher(Searcher_Model model) {
        this.model = model;
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

            if (feature instanceof Stronghold) {

                foundFeatures.put(featureModel, new ArrayList<>());
            }

        }

        //At this point if the features we found are not the same size and the features we want to find break and go to the next seed
        if (foundFeatures.size() != featureList.size()) {
            data.set(PassType.FAIL, foundFeatures);
            return data;
        }

        //Now that we know the features we want can exist in the structure seed we need to check the biome seed
        data.set(PassType.SUCCESS, foundFeatures);
        return data;
    }
}

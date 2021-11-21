package sassa.searcher;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.util.pos.BPos;
import sassa.enums.BiomeListType;
import sassa.models.BiomeList_Model;
import sassa.models.BiomeSetList_Model;
import sassa.models.BiomeSet_Model;
import sassa.models.Searcher_Model;
import sassa.util.BiomeSources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Biome_Searcher {
    private Searcher_Model model;

    Biome_Searcher(Searcher_Model model) {
        this.model = model;
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

}

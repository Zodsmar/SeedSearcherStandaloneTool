package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sassa.searcher.biomeSearcher.findBiomeFromSource;

public class searcher {

    /**
     * This should be the fastest method by far starting with structures first then biomes
     * @param searchSize - radius from 0, 0
     * @param sList - structure list (ex. MANSION)
     * @param bList - biome list (ex. Biome.JUNGLE)
     * @param dimension - "OVERWORLD", "NETHER", "END"
     * @param incrementer - the amount of blocks to skip for biome searching
     */
    public static void searchRandomly(int searchSize, ArrayList<RegionStructure> sList, ArrayList<Biome> bList, String dimension, int incrementer) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        int rejectedSeeds = 0;

        Map<RegionStructure, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++) {
            for(RegionStructure searchStructure : sList){
                System.out.println(searchStructure);
                RegionStructure.Data<?> lowerBound = searchStructure.at(-searchSize >> 4, -searchSize >> 4);
                RegionStructure.Data<?> upperBound = searchStructure.at(searchSize >> 4, searchSize >> 4);
                List<CPos> foundStructures = new ArrayList<>();
                for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
                    for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                        CPos struct = searchStructure.getInRegion(structureSeed, regionX, regionZ, rand);
                        //12 chunks is 192 blocks.
                        if (struct != null && struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) <= searchSize >> 4){
                            foundStructures.add(struct);
                            structures.put(searchStructure, foundStructures);
                        }

                    }
                }
            }
            if(structures.size() != sList.size()){
                rejectedSeeds++;
                continue;
            }

            for(long upperBits = 0; upperBits < 1L << 16; upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;

                BiomeSource source = getBiomeSource(dimension, worldSeed);

                Map<RegionStructure, Integer> foundList = new HashMap<>();
                for(Map.Entry<RegionStructure, List<CPos>> structure1 : structures.entrySet()){
                    RegionStructure key = structure1.getKey();
                    List<CPos> value = structure1.getValue();
                    for (CPos struct : value) {
                        if (key.canSpawn(struct.getX(), struct.getZ(), source)) {
                            //System.out.println("Found world seed " + worldSeed + " with structure seed " + structureSeed);
                            //System.out.println("The structure is at (" + struct.getX() * 16 + ", " + struct.getZ() * 16 + ")");
                            int sCount = foundList.containsKey(key) ? foundList.get(key) : 0;
                            foundList.put(key, sCount + 1);
                        }
                    }
                }



                if(foundList.size() == sList.size()){
                    boolean allBiomesFound = findBiomeFromSource(searchSize, bList, source, incrementer);
                    if(allBiomesFound){
                        System.out.println("Found world seed " + worldSeed);
                        for(Map.Entry<RegionStructure, Integer> counting : foundList.entrySet()) {
                            System.out.println(counting.getKey() + " : " + counting.getValue());
                        }
                        System.out.println(structures);
                        return;
                    }
                }
                rejectedSeeds++;
                System.out.println(rejectedSeeds);
            }
        }

    }
    public static BiomeSource getBiomeSource(String dimension, long worldSeed) {
        BiomeSource source = null;

        switch(dimension){
            case "OVERWORLD":
                source = new OverworldBiomeSource(MCVersion.v1_15, worldSeed);
                break;
            case "NETHER":
                source = new NetherBiomeSource(MCVersion.v1_15, worldSeed);
                break;
            case "END":
                source = new EndBiomeSource(MCVersion.v1_15, worldSeed);
                break;
            default:
                System.out.println("USE OVERWORLD, NETHER, OR END");
                break;
        }

        return source;
    }
}

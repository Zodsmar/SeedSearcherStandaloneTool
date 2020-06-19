package sassa.searcher;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class structureSearcher {

    public static int findStructureRegion(int searchSize, int spacing){
        // 32 is the strucuture region size
        return (int) Math.ceil(searchSize / (float)(spacing * 16));
    }

    public static int findChunksToBlocks(int searchSize){
        return (int) Math.floor(searchSize / 16);
    }

    /**
     * Finds all Overworld Structures except Strongholds and Mineshafts
     * @param searchSize
     * @param worldSeed
     * @param structure
     * @param dimension
     */

    public static void findStructure(int searchSize, long worldSeed, RegionStructure structure, String dimension) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        BiomeSource source = null;
        switch(dimension){
            case "OVERWORLD":
                source = new OverworldBiomeSource(MCVersion.v1_15, worldSeed).build();
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

        int regionSize = findStructureRegion(searchSize, structure.getSpacing());
        int chunkToBlocks = findChunksToBlocks(searchSize);
       // System.out.println(regionSize + " " + chunkToBlocks + " " + structure.getSpacing());


        List<CPos> structures = new ArrayList<>();

        //Each village region is 32x32 chunks.
        for(int regionX = -regionSize; regionX < regionSize; regionX++) {
            for(int regionZ = -regionSize; regionZ < regionSize; regionZ++) {
                CPos struct = structure.getInRegion(worldSeed, regionX, regionZ, rand);
                //12 chunks is 192 blocks.
                if (struct != null && struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) <= chunkToBlocks) structures.add(struct);
            }
        }

        int count = 0;
        for(CPos struct: structures) {
            if(structure.canSpawn(struct.getX(), struct.getZ(), source)) {
                //System.out.println("Found world seed " + worldSeed + " with structure seed " + structureSeed);
                //System.out.println("The structure is at (" + struct.getX() * 16 + ", " + struct.getZ() * 16 + ")");
                count++;
            }
        }
        System.out.println(structure.toString() + ": "+ count);
    }

    // Fairly slow....
    public static void findMineshaft(int searchSize, long worldSeed, Mineshaft mineshaft){
        ChunkRand rand = new ChunkRand();
        int count = 0;
        for(int i = 0; i < findChunksToBlocks(searchSize); i++){
            for(int j = 0; j < findChunksToBlocks(searchSize); j++){
                Mineshaft.Data<?> mineshaftData = mineshaft.at(i, j);
                if(mineshaftData.testStart(worldSeed, rand)){
                    OverworldBiomeSource source = new OverworldBiomeSource(MCVersion.v1_15, worldSeed).build();
                    if(mineshaftData.testBiome(source)){
                        count++;
                    }
                }
            }
        }
        System.out.println("Mineshaft: " + count);

    }

    public static void findStructureRandomly(int searchSize, ArrayList<RegionStructure> list, String dimension) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        int rejectedSeeds = 0;

        Map<RegionStructure, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++) {
            for(RegionStructure searchStructure : list){
                System.out.println(searchStructure);
                int regionSize = findStructureRegion(searchSize, searchStructure.getSpacing());
                int chunkToBlocks = findChunksToBlocks(searchSize);
                List<CPos> foundStructures = new ArrayList<>();
                for (int regionX = -regionSize; regionX < regionSize; regionX++) {
                    for (int regionZ = -regionSize; regionZ < regionSize; regionZ++) {
                        CPos struct = searchStructure.getInRegion(structureSeed, regionX, regionZ, rand);
                        //12 chunks is 192 blocks.
                        if (struct != null && struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) <= chunkToBlocks){
                            foundStructures.add(struct);
                            structures.put(searchStructure, foundStructures);
                        }

                    }
                }
            }
            if(structures.size() != list.size()){
                rejectedSeeds++;
                continue;
            }

            for(long upperBits = 0; upperBits < 1L << 16; upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;

                BiomeSource source = null;
                switch (dimension) {
                    case "OVERWORLD":
                        source = new OverworldBiomeSource(MCVersion.v1_15, worldSeed).build();
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

                if(foundList.size() == list.size()){
                    System.out.println("Found world seed " + worldSeed + " with structure seed " + structureSeed);
                    for(Map.Entry<RegionStructure, Integer> counting : foundList.entrySet()) {
                        System.out.println(counting.getKey() + " : " + counting.getValue());
                    }
                    System.out.println(structures);
                    return;
                }
                rejectedSeeds++;
                //System.out.println(rejectedSeeds);
            }
        }

    }

}



/*
    SISTER SEED (Much faster for seed searching)
    for(long upperBits = 0; upperBits < 1L << 16; upperBits++) {
                long worldSeed = (upperBits << 48) | structureSeed;

    Converting to a structure Seed
    for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++) {
 */
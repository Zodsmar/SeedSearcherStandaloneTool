package sassa.searcher;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.util.*;

public class StructureSearcher {

    /**
     * Finds all Overworld Structures except Strongholds and Mineshafts
     * @param searchSize
     * @param worldSeed
     * @param structure
     * @param dimension
     */
    public static void findStructure(int searchSize, long worldSeed, RegionStructure<?, ?> structure, String dimension) {
        ChunkRand rand = new ChunkRand();
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        RegionStructure.Data<?> lowerBound = structure.at(-searchSize >> 4, -searchSize >> 4);
        RegionStructure.Data<?> upperBound = structure.at(searchSize >> 4, searchSize >> 4);

        int count = 0;

        for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
            for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                CPos struct = structure.getInRegion(worldSeed, regionX, regionZ, rand);
                if(struct == null)continue;
                if(struct.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > searchSize >> 4)continue;
                if(!structure.canSpawn(struct.getX(), struct.getZ(), source))continue;

                //System.out.println("Found world seed " + worldSeed + " with structure seed " + structureSeed);
                System.out.println("The structure is at (" + struct.getX() * 16 + ", " + struct.getZ() * 16 + ")");
                count++;
            }
        }

        System.out.println(structure.toString() + ": "+ count);
    }

    // Fairly slow....
    public static void findMineshaft(int searchSize, long worldSeed, Mineshaft mineshaft) {
        ChunkRand rand = new ChunkRand();
        int count = 0;

        for(int chunkX = -searchSize >> 4; chunkX < searchSize >> 4; chunkX++){
            for(int chunkZ = -searchSize >> 4; chunkZ < searchSize >> 4; chunkZ++){
                Mineshaft.Data<?> mineshaftData = mineshaft.at(chunkX, chunkZ);
                if(!mineshaftData.testStart(worldSeed, rand))continue;
                OverworldBiomeSource source = new OverworldBiomeSource(MCVersion.v1_15, worldSeed);
                if(!mineshaftData.testBiome(source))continue;
                count++;
            }
        }

        System.out.println("Mineshaft: " + count);
    }

    public static void findStructureRandomly(int searchSize, Collection<RegionStructure<?, ?>> list, String dimension, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        long rejectedSeeds = 0;

        Map<RegionStructure<?, ?>, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++, structures.clear()) {
            for(RegionStructure<?, ?> searchStructure: list) {
                RegionStructure.Data<?> lowerBound = searchStructure.at(-searchSize >> 4, -searchSize >> 4);
                RegionStructure.Data<?> upperBound = searchStructure.at(searchSize >> 4, searchSize >> 4);

                List<CPos> foundStructures = new ArrayList<>();

                for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
                    for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                        CPos struct = searchStructure.getInRegion(structureSeed, regionX, regionZ, rand);
                        if(struct == null)continue;
                        if(struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) > searchSize >> 4)continue;
                        foundStructures.add(struct);
                    }
                }

                if(foundStructures.isEmpty())break;
                structures.put(searchStructure, foundStructures);
            }

            if(structures.size() != list.size()) {
                rejectedSeeds += 1L << biomePrecision;
                continue;
            }

            System.out.println("Found structure seed " + structureSeed + ", checking biomes...");

            for(long upperBits = 0; upperBits < 1L << biomePrecision; upperBits++, rejectedSeeds++) {
                long worldSeed = (upperBits << 48) | structureSeed;

                BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

                int structureCount = 0;

                for(Map.Entry<RegionStructure<?, ?>, List<CPos>> e : structures.entrySet()) {
                    RegionStructure<?, ?> structure = e.getKey();
                    List<CPos> starts = e.getValue();

                    for(CPos start : starts) {
                        if(!structure.canSpawn(start.getX(), start.getZ(), source))continue;
                        structureCount++;
                        break;
                    }
                }

                if(structureCount != list.size())continue;

                System.out.format("Found world seed %d with structure seed %d (rejected %d)\n", worldSeed, structureSeed, rejectedSeeds);
                return;
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
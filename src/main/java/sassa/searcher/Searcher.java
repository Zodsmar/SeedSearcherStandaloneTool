package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;
import sassa.gui.Variables;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class Searcher {

    public static void searchRandomly(int searchSize, long startSeedStructure, Collection<StructureProvider> sList, Collection<StructureProvider> soList, Collection<Biome> bList, Collection<Biome> boList, Collection<Biome.Category> cList, Collection<Biome.Category> coList, String dimension, int incrementer, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        int totalStructures = sList.size();

        Map<StructureProvider, List<CPos>> structures = new HashMap<>();
        sList = sList.stream().distinct().collect(Collectors.toList());
        for(long structureSeed = startSeedStructure; structureSeed < 1L << 48; structureSeed++, structures.clear()) {
            for(StructureProvider searchProvider: sList) {

                if(fxmlController.running == false  || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) <= Variables.acceptedWorlds()){
                    return;
                }

                RegionStructure<?,?> searchStructure = searchProvider.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion());
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
                if (foundStructures.size() < searchProvider.getMinimumValue()) break;
                if(foundStructures.isEmpty())break;
                structures.put(searchProvider, foundStructures);
            }

            //System.out.println(structures.size() + " " + sList.size());

            if(structures.size() != sList.size()) {
                Variables.checkWorld(1L << biomePrecision);
                continue;
            }

            //System.out.println("Found structure seed " + structureSeed + ", checking biomes...");

            for(long upperBits = 0; upperBits < 1L << biomePrecision; upperBits++, Variables.checkWorld(1)) {
                long worldSeed = (upperBits << 48) | structureSeed;

                if(fxmlController.running == false  || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) <= Variables.acceptedWorlds()){
                    return;
                }

                BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

                int structureCount = 0;

                for(Map.Entry<StructureProvider, List<CPos>> e : structures.entrySet()) {
                    StructureProvider structure = e.getKey();
                    List<CPos> starts = e.getValue();

                    RegionStructure<?,?> searchStructure = structure.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion());

                    for(CPos start : starts) {
                        if(!searchStructure.canSpawn(start.getX(), start.getZ(), source))continue;
                        structureCount++;
                        if(structureCount >= structure.getMinimumValue()){
                            break;
                        }
                    }
                }
                if(structureCount != totalStructures)continue;

                if (soList.size() != 0) {
                    ArrayList<StructureProvider> so = new ArrayList<>(soList);
                    ArrayList<StructureProvider> allStructuresOutFound = StructureSearcher.findStructureEx(searchSize, worldSeed, so);
                    if (allStructuresOutFound.size() != 0) continue;
                }

                if(cList.size() != 0){
                    ArrayList<Biome.Category> ci = new ArrayList<>(cList);
                    ArrayList<Biome.Category> allCategoriesFound = BiomeSearcher.findBiomeFromCategory(searchSize, worldSeed, ci, incrementer);
                    if(allCategoriesFound.size() != 0)continue;
                }
                if(coList.size() != 0){
                    ArrayList<Biome.Category> co = new ArrayList<>(coList);
                    ArrayList<Biome.Category> allCategoriesOUTFound = BiomeSearcher.findBiomeFromCategory(searchSize, worldSeed, co, incrementer);
                    if(allCategoriesOUTFound.size() != 0)continue;
                }
                if(bList.size() != 0){
                    ArrayList<Biome> bi = new ArrayList<>(bList);
                    ArrayList<Biome> allBiomesFound = BiomeSearcher.findBiome(searchSize, worldSeed, bi, incrementer);
                    if(allBiomesFound.size() != 0)continue;
                }
                if(boList.size() != 0){
                    ArrayList<Biome> bo = new ArrayList<>(boList);
                    ArrayList<Biome> allBiomesOUTFound = BiomeSearcher.findBiome(searchSize, worldSeed, bo, incrementer);
                    if(allBiomesOUTFound.size() != 0)continue;
                }

                Util util = new Util();
                if(Singleton.getInstance().getShadowMode().isSelected()){
                    util.console(String.valueOf(worldSeed) + " (Shadow: " + WorldSeed.getShadowSeed(worldSeed) + " )");
                } else {
                    util.console(String.valueOf(worldSeed));
                }
                Variables.acceptWorld();
                Variables.minOneCheckWorld();


            }
            if(fxmlController.running == false || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) <= Variables.acceptedWorlds()){
                return;
            }
        }
    }

    /**
     * This should be the fastest method by far starting with structures first then biomes
     * @param searchSize - radius from 0, 0
     * @param sList - structure list (ex. MANSION)
     * @param bList - biome list (ex. Biome.JUNGLE)
     * @param dimension - "OVERWORLD", "NETHER", "END"
     * @param incrementer - the amount of blocks to skip for biome searching
     */
    public static void searchRandomly_old(int searchSize, Collection<RegionStructure<?, ?>> sList, Collection<Biome> bList, String dimension, int incrementer, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        int rejectedSeeds = 0;

        Map<RegionStructure<?, ?>, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++, structures.clear()) {
            for(RegionStructure<?, ?> searchStructure: sList) {
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

            if(structures.size() != sList.size()) {
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

                if(structureCount != sList.size())continue;

                boolean allBiomesFound = BiomeSearcher.findBiomeFromSource(searchSize, bList, source, incrementer);
                if(!allBiomesFound)continue;

                System.out.format("Found world seed %d with structure seed %d (rejected %d)\n", worldSeed, structureSeed, rejectedSeeds);
                return;
            }
        }

    }
    public static BiomeSource getBiomeSource(String dimension, long worldSeed) {
        BiomeSource source = null;

        switch(dimension){
            case "OVERWORLD":
                source = new OverworldBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            case "NETHER":
                source = new NetherBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            case "END":
                source = new EndBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            default:
                System.out.println("USE OVERWORLD, NETHER, OR END");
                break;
        }

        return source;
    }

    public static BiomeSource getBiomeSourceForLargeWorlds(String dimension, long worldSeed) {
        BiomeSource source = null;

        switch(dimension){
            case "OVERWORLD":
                source = new OverworldBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed, 6 ,4);
                break;
            case "NETHER":
                source = new NetherBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            case "END":
                source = new EndBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            default:
                System.out.println("USE OVERWORLD, NETHER, OR END");
                break;
        }

        return source;
    }
}

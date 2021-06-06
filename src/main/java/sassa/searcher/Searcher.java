package sassa.searcher;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.math.Vec3i;
import sassa.gui.Variables;
import sassa.gui.fxmlController;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Searcher {

    public static void searchRandomly(int searchSize, long startSeedStructure, long endSeedStructure, Collection<StructureProvider> sList, Collection<StructureProvider> soList, Collection<Biome> bList, Collection<Biome> boList, Collection<Biome.Category> cList, Collection<Biome.Category> coList, Dimension dimension, int incrementer, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ThreadLocalRandom threadLocalRandomizer = ThreadLocalRandom.current();
        ChunkRand rand = new ChunkRand();
        int totalStructures = sList.size();

//        if(searchSize < 256) {
//            searchSize += 256;
//        }

        Map<StructureProvider, List<CPos>> structures = new HashMap<>();
        sList = sList.stream().distinct().collect(Collectors.toList());
        for(long structureSeedIncrementer = startSeedStructure; structureSeedIncrementer < endSeedStructure; structureSeedIncrementer++, structures.clear()) {
            long structureSeed = threadLocalRandomizer.nextLong(startSeedStructure, endSeedStructure);
            for(StructureProvider searchProvider: sList) {

                if(fxmlController.running == false  || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) < Variables.acceptedWorlds()){
                    return;
                }

                RegionStructure<?,?> searchStructure = searchProvider.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion());
                RegionStructure.Data<?> lowerBound;
                RegionStructure.Data<?> upperBound;
                if(Singleton.getInstance().getSpawnPoint().isSelected()){
                   lowerBound = searchStructure.at(-searchSize + Integer.parseInt(Singleton.getInstance().getXCoordSpawn().getText()) >> 4, -searchSize + Integer.parseInt(Singleton.getInstance().getZCoordSpawn().getText()) >> 4);
                   upperBound = searchStructure.at(searchSize + Integer.parseInt(Singleton.getInstance().getXCoordSpawn().getText()) >> 4, searchSize + Integer.parseInt(Singleton.getInstance().getZCoordSpawn().getText()) >> 4);
                } else {
                    lowerBound = searchStructure.at(-searchSize >> 4, -searchSize >> 4);
                    upperBound = searchStructure.at(searchSize >> 4, searchSize >> 4);
                }


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

                if(fxmlController.running == false  || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) < Variables.acceptedWorlds()){
                    return;
                }



                int structureCount = 0;
                boolean validSpawn = true;

                for(Map.Entry<StructureProvider, List<CPos>> e : structures.entrySet()) {
                    StructureProvider structure = e.getKey();
                    List<CPos> starts = e.getValue();
                    BiomeSource source = Searcher.getBiomeSource(e.getKey().getDimension(), worldSeed);
                    if(Singleton.getInstance().getSpawnPoint().isSelected() && source.getDimension() == Dimension.OVERWORLD){
                        if(!checkSpawnPoint(source)){
                            validSpawn = false;
                            break;
                        }
                    }

                    RegionStructure<?,?> searchStructure = structure.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion());
                    for(CPos start : starts) {
                        if(!searchStructure.canSpawn(start.getX(), start.getZ(), source))continue;
                        System.out.println(  searchStructure.getName() + start.getX());
                        System.out.println(start.getZ());
                        structureCount++;
                        if(structureCount >= structure.getMinimumValue()){
                            break;
                        }
                    }
                }
                if(validSpawn == false) continue;
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
                if(fxmlController.running == true){
                    if(Singleton.getInstance().getShadowMode().isSelected()){
                        util.console(String.valueOf(worldSeed) + " (Shadow: " + WorldSeed.getShadowSeed(worldSeed) + " )");
                    } else {
                        util.console(String.valueOf(worldSeed));
                    }
                }
                Variables.acceptWorld();
                Variables.minOneCheckWorld();


            }
            if(fxmlController.running == false || Long.parseLong(Singleton.getInstance().getSeedCount().getText()) < Variables.acceptedWorlds()){
                return;
            }
        }
    }

    public static BiomeSource getBiomeSource(Dimension dimension, long worldSeed) {
        BiomeSource source = null;

        switch(dimension){
            case OVERWORLD:
                if(Singleton.getInstance().getWorldType().getValue() == "LARGE BIOMES"){
                    source = new OverworldBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed, 6 ,4);
                } else {
                    source = new OverworldBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                }
                break;
            case NETHER:
                source = new NetherBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            case END:
                source = new EndBiomeSource(Singleton.getInstance().getMinecraftVersion(), worldSeed);
                break;
            default:
                System.out.println("USE OVERWORLD, NETHER, OR END");
                break;
        }

        return source;
    }

    public static boolean checkSpawnPoint(BiomeSource source) {
        //if(source.getDimension() == Dimension.OVERWORLD) {
        OverworldBiomeSource oSource = (OverworldBiomeSource) source;
        BPos spawn = oSource.getSpawnPoint();
        int x = Integer.parseInt(Singleton.getInstance().getXCoordSpawn().getText());
        int z = Integer.parseInt(Singleton.getInstance().getZCoordSpawn().getText());
        int margin = Integer.parseInt(Singleton.getInstance().getMarginOfError().getText());
        int xM = (x + (x + margin)) / 2;
        int zM = (z + (z + margin)) / 2;

        if ((Math.abs(spawn.getX() - xM) <= (Math.abs(x - xM))) && (Math.abs(spawn.getZ() - zM) <= (Math.abs(z - zM)))) {
            return true;
        }
    //}
        return false;
    }
}

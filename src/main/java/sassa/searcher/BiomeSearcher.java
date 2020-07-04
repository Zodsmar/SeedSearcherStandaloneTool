package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import sassa.util.Singleton;

import java.util.ArrayList;
import java.util.Collection;

public class BiomeSearcher {

    public static ArrayList<Biome> findBiome(int searchSize, long worldSeed, Collection<Biome> biomeToFind, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome> biomesToFindCopy = new ArrayList<>(biomeToFind);
        //BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);
        //BiomeSource source = Searcher.getBiomeSource("OVERWORLD", worldSeed);
        BiomeSource source = Searcher.getBiomeSource("OVERWORLD", worldSeed);
        BiomeSource source1 = Searcher.getBiomeSource("NETHER", worldSeed);
        BiomeSource source2 = Searcher.getBiomeSource("END", worldSeed);


        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                biomesToFindCopy.remove(source.getBiome(i, 0, j));
                biomesToFindCopy.remove(source1.getBiome(i, 0, j));
                biomesToFindCopy.remove(source2.getBiome(i, 0, j));



                if(biomesToFindCopy.isEmpty()) {
                    //System.out.format("Found world seed %d (Shadow %d)\n", worldSeed, WorldSeed.getShadowSeed(worldSeed));
                    return biomesToFindCopy;
                }
            }
        }

        return biomesToFindCopy;
    }

    public static ArrayList<Biome> findBiomeEx(int searchSize, long worldSeed, Collection<Biome> biomeToFind, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome> biomesToFindCopy = new ArrayList<>(biomeToFind);
        //BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);
        BiomeSource source = Searcher.getBiomeSource("OVERWORLD", worldSeed);
        BiomeSource source1 = Searcher.getBiomeSource("NETHER", worldSeed);
        BiomeSource source2 = Searcher.getBiomeSource("END", worldSeed);


        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                if(biomesToFindCopy.contains(source.getBiome(i, 0, j)) || biomesToFindCopy.contains(source1.getBiome(i, 0, j)) || biomesToFindCopy.contains(source2.getBiome(i, 0, j))){
                    return biomesToFindCopy;
                }
                //biomesToFindCopy.remove(source1.getBiome(i, 0, j));
                //biomesToFindCopy.remove(source2.getBiome(i, 0, j));
            }
        }

        return new ArrayList<>();
    }

    public static ArrayList<Biome.Category> findBiomeFromCategory(int searchSize, long worldSeed, Collection<Biome.Category> biomeToFind, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome.Category> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = Searcher.getBiomeSource("OVERWORLD", worldSeed);
        BiomeSource source1 = Searcher.getBiomeSource("NETHER", worldSeed);
        BiomeSource source2 = Searcher.getBiomeSource("END", worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                biomesToFindCopy.remove(source.getBiome(i, 0, j).getCategory());
                biomesToFindCopy.remove(source1.getBiome(i, 0, j).getCategory());
                biomesToFindCopy.remove(source2.getBiome(i, 0, j).getCategory());

                if(biomesToFindCopy.isEmpty()) {
                    return biomesToFindCopy;
                }
            }
        }

        return biomesToFindCopy;
    }

    public static ArrayList<Biome.Category> findBiomeFromCategoryEx(int searchSize, long worldSeed, Collection<Biome.Category> biomeToFind, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome.Category> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = Searcher.getBiomeSource("OVERWORLD", worldSeed);
        BiomeSource source1 = Searcher.getBiomeSource("NETHER", worldSeed);
        BiomeSource source2 = Searcher.getBiomeSource("END", worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                if(biomesToFindCopy.contains(source.getBiome(i, 0, j).getCategory()) || biomesToFindCopy.contains(source1.getBiome(i, 0, j).getCategory())|| biomesToFindCopy.contains(source2.getBiome(i, 0, j).getCategory())){
                    return biomesToFindCopy;
                }
            }
        }

        return new ArrayList<>();
    }

}

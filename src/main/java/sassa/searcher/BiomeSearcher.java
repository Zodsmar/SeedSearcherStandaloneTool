package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.seedutils.mc.seed.WorldSeed;

import java.util.ArrayList;
import java.util.Collection;

public class BiomeSearcher {

    public static boolean findBiome(int searchSize, long worldSeed, Biome biomeToFind, String dimension, int incrementer) {
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                if(source.getBiome(i, 0, j) == biomeToFind) {
                    System.out.format("Found world seed %d (Shadow %d)\n", worldSeed, WorldSeed.getShadowSeed(worldSeed));
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean findBiome(int searchSize, long worldSeed, Collection<Biome> biomeToFind, String dimension, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                biomesToFindCopy.remove(source.getBiome(i, 0, j));

                if(biomesToFindCopy.isEmpty()) {
                    System.out.format("Found world seed %d (Shadow %d)\n", worldSeed, WorldSeed.getShadowSeed(worldSeed));
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean findBiomeFromSource(int searchSize, Collection<Biome> biomeToFind, BiomeSource source, int incrementer) {
        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                biomeToFind.remove(source.getBiome(i, 0, j));

                if(biomeToFind.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean findBiomeFromCategory(int searchSize, long worldSeed, Collection<Biome.Category> biomeToFind, String dimension, int incrementer) {
        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome.Category> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
            for(int j = -searchSize; j < searchSize; j += incrementer) {
                biomesToFindCopy.remove(source.getBiome(i, 0, j).getCategory());

                if(biomesToFindCopy.isEmpty()) {
                    System.out.format("Found world seed %d (Shadow %d)\n", worldSeed, WorldSeed.getShadowSeed(worldSeed));
                    return true;
                }
            }
        }

        return false;
    }

}

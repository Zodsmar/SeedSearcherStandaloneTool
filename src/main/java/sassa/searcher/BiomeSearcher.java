package sassa.searcher;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class BiomeSearcher {

    public static boolean findBiome(int searchSize, long worldSeed, Biome biomeToFind, String dimension, int incrementer){

        BiomeSource source = getBiomeSource(dimension, worldSeed);

        boolean found = false;
        for(int i = -searchSize; i < searchSize; i += incrementer){
            for(int j = -searchSize; j < searchSize; j += incrementer){
                if(source.getBiome(i, 0, j) == biomeToFind){
                    System.out.println("Found world seed " + worldSeed);
                    return true;
                }

            }
        }
        return false;

    }
    public static boolean findBiome(int searchSize, long worldSeed, ArrayList<Biome> biomeToFind, String dimension, int incrementer){

        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = getBiomeSource(dimension, worldSeed);

        boolean found = false;
        for(int i = -searchSize; i < searchSize; i += incrementer){
            for(int j = -searchSize; j < searchSize; j += incrementer){
                if(biomesToFindCopy.contains(source.getBiome(i, 0, j))){
                    biomesToFindCopy.remove(source.getBiome(i, 0, j));
                }

            }
        }
        if(biomesToFindCopy.isEmpty()){
            //System.out.println("Found all Biomes");
            System.out.println("Found world seed " + worldSeed);
            return true;
        }
        else {
            //System.out.println("Didn't find all Biomes");
            return false;
        }

    }

    public static boolean findBiomeFromSource(int searchSize, ArrayList<Biome> biomeToFind, BiomeSource source, int incrementer){
        for(int i = -searchSize; i < searchSize; i += incrementer){
            for(int j = -searchSize; j < searchSize; j += incrementer){
                if(biomeToFind.contains(source.getBiome(i, 0, j))){
                    biomeToFind.remove(source.getBiome(i, 0, j));
                    if(biomeToFind.isEmpty()){
                        //System.out.println("Found all Biomes");
                        return true;
                    }
                }
            }
        }

        //System.out.println("Didn't find all Biomes");
        return false;


    }

    public static boolean findBiomeFromCategory(int searchSize, long worldSeed, ArrayList<Biome.Category> biomeToFind, String dimension, int incrementer){

        // Since I'm deleting out of the array to make sure we are checking everytime properly I am shallow copying the array
        ArrayList<Biome.Category> biomesToFindCopy = new ArrayList<>(biomeToFind);
        BiomeSource source = getBiomeSource(dimension, worldSeed);


        for(int i = -searchSize; i < searchSize; i += incrementer){
            for(int j = -searchSize; j < searchSize; j += incrementer){
                //System.out.println(biomesToFindCopy.contains(source.getBiome(i, 0, j)));
                if(biomesToFindCopy.contains(source.getBiome(i, 0, j).getCategory())){
                    System.out.println(source.getBiome(i, 0, j).getName() + " " + biomesToFindCopy.size());
                   biomesToFindCopy.remove(source.getBiome(i,0,j).getCategory());
                }

            }
        }
        //System.out.println(biomesToFindCopy.size());
        if(biomesToFindCopy.isEmpty()){
            //System.out.println("Found all Biomes");
            System.out.println("Found world seed " + worldSeed);
            return true;
        }
        else {
            //System.out.println("Didn't find all Biomes");
            return false;
        }

    }


    public static ArrayList<Biome> buildBiomeListFromCategory(ArrayList<Biome.Category> category){
        ArrayList<Biome> biomes = new ArrayList<>();
        for(Biome.Category cat : category){
            Iterator regIt = Biome.REGISTRY.entrySet().iterator();
            while(regIt.hasNext()){
                Map.Entry mapElement = (Map.Entry)regIt.next();
                Biome b = (Biome) mapElement.getValue();
                if(b.getCategory() == cat) {
                    biomes.add(b);
                }
            }
        }

        return biomes;
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

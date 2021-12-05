package sassa.models;

import com.seedfinding.mcbiome.biome.Biome;
import sassa.enums.BiomeListType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BiomeList_Model implements Cloneable, Serializable {
    private List<Biome> includedBiomes;
    private List<Biome> excludedBiomes;


    public BiomeList_Model() {
        this.includedBiomes = new ArrayList<>();
        this.excludedBiomes = new ArrayList<>();
    }

    public BiomeList_Model(List<Biome> includedBiomes, List<Biome> excludedBiomes) {
        this.includedBiomes = includedBiomes;
        this.excludedBiomes = excludedBiomes;
    }

    public BiomeList_Model addBiome(Biome biome, BiomeListType type) {
        if (type == BiomeListType.INCLUDED) {
            includedBiomes.add(biome);
            excludedBiomes.remove(biome);
        } else if (type == BiomeListType.EXCLUDED) {
            includedBiomes.remove(biome);
            excludedBiomes.add(biome);
        }
        return null;
    }

    public boolean isEmpty() {
        if (includedBiomes.isEmpty() && excludedBiomes.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BiomeList_Model biomeList_model;
        try {
            biomeList_model = (BiomeList_Model) super.clone();
            if (includedBiomes != null)
                biomeList_model.includedBiomes = new ArrayList<>(includedBiomes);
            if (excludedBiomes != null)
                biomeList_model.excludedBiomes = new ArrayList<>(excludedBiomes);


        } catch (CloneNotSupportedException e) { // this should never happen
            System.out.println("CloneNotSupportedException thrown " + e);
            return null;
        }
        return biomeList_model;
    }

    public void addBiomes(List<Biome> biomes, BiomeListType type) {
        biomes.forEach(biome -> addBiome(biome, type));
    }

    public List<Biome> getIncludedBiomes() {
        return includedBiomes;
    }


    public List<Biome> getExcludedBiomes() {
        return excludedBiomes;
    }

    public void setIncludedBiomes(List<Biome> includedBiomes) {
        this.includedBiomes = includedBiomes;
    }

    public void setExcludedBiomes(List<Biome> excludedBiomes) {
        this.excludedBiomes = excludedBiomes;
    }


    public void removeBiome(Biome curBiome) {
        includedBiomes.remove(curBiome);
        excludedBiomes.remove(curBiome);
    }

}

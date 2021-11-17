package sassa.models;

import com.seedfinding.mcbiome.biome.Biome;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class BiomeSet_Model implements Serializable {

    private String name;
    private List<Biome> biomes;

    public BiomeSet_Model(String name, List<Biome> biomes) {
        this.name = name;
        this.biomes = biomes;
    }

    public boolean contains(Biome biome) {
        return biomes.contains(biome);
    }

    public boolean containsAny(List<Biome> biomes) {
        return !Collections.disjoint(biomes, this.biomes);
    }

    public String getName() {
        return name;
    }

    public void setName(String setName) {
        this.name = setName;
    }

    public List<Biome> getBiomes() {
        return biomes;
    }

    public void setBiomes(List<Biome> biomes) {
        this.biomes = biomes;
    }
}

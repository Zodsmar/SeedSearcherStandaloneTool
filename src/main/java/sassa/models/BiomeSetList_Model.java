package sassa.models;

import sassa.enums.BiomeListType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BiomeSetList_Model implements Cloneable, Serializable {
    private List<BiomeSet_Model> includedBiomeSet;
    private List<BiomeSet_Model> excludedBiomeSet;

    public BiomeSetList_Model() {
        this.includedBiomeSet = new ArrayList<>();
        this.excludedBiomeSet = new ArrayList<>();
    }

    public BiomeSetList_Model(List<BiomeSet_Model> includedBiomeSet, List<BiomeSet_Model> excludedBiomeSet) {
        this.includedBiomeSet = includedBiomeSet;
        this.excludedBiomeSet = excludedBiomeSet;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BiomeSetList_Model biomeSetList_model;
        try {
            biomeSetList_model = (BiomeSetList_Model) super.clone();
            if (includedBiomeSet != null)
                biomeSetList_model.includedBiomeSet = new ArrayList<>(includedBiomeSet);
            if (excludedBiomeSet != null)
                biomeSetList_model.excludedBiomeSet = new ArrayList<>(excludedBiomeSet);
        } catch (CloneNotSupportedException e) { // this should never happen
            System.out.println("CloneNotSupportedException thrown " + e);
            return null;
        }
        return biomeSetList_model;
    }

    public boolean isEmpty() {
        if (includedBiomeSet.isEmpty() && excludedBiomeSet.isEmpty()) {
            return true;
        }
        return false;
    }

    public void addBiomeSetList(BiomeSet_Model biomeSet, BiomeListType type) {
        if (type == BiomeListType.INCLUDED) {
            includedBiomeSet.add(biomeSet);
            excludedBiomeSet.remove(biomeSet);
        } else if (type == BiomeListType.EXCLUDED) {
            includedBiomeSet.remove(biomeSet);
            excludedBiomeSet.add(biomeSet);
        }
    }

    public List<BiomeSet_Model> getIncludedBiomeSet() {
        return includedBiomeSet;
    }

    public void setIncludedBiomeSet(List<BiomeSet_Model> includedBiomeSet) {
        this.includedBiomeSet = includedBiomeSet;
    }

    public List<BiomeSet_Model> getExcludedBiomeSet() {
        return excludedBiomeSet;
    }

    public void setExcludedBiomeSet(List<BiomeSet_Model> excludedBiomeSet) {
        this.excludedBiomeSet = excludedBiomeSet;
    }
}

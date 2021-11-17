package sassa.models;


import com.seedfinding.mccore.version.MCVersion;
import sassa.enums.SearchType;
import sassa.enums.SpawnType;
import sassa.enums.WorldType;

import java.io.Serializable;


public class Searcher_Model implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private String configName;
    private int seedsToFind;
    private int searchRadius;
    private int incrementer;
    private int biomePrecision;
    private SearchType searchType;
    private MCVersion selectedVersion;
    private WorldType worldType;
    private int threadsToUse;
    private SpawnType spawnType;

    private BiomeList_Model biomeList;


    private BiomeSetList_Model biomeSetList;
    //List<Structures> includedFeatures;
    //List<Features> excludedFeatures;

    //Defaults for configs
    public Searcher_Model() {
        this.configName = "default";
        this.seedsToFind = 5;
        this.searchRadius = 10;
        this.incrementer = 1;
        this.biomePrecision = 16;
        this.searchType = SearchType.RANDOM_SEARCH;
        this.selectedVersion = MCVersion.latest();
        this.worldType = WorldType.DEFAULT;
        this.threadsToUse = 3;
        this.spawnType = SpawnType.ZERO_ZERO;
        this.biomeList = new BiomeList_Model();
        this.biomeSetList = new BiomeSetList_Model();
    }

    public Searcher_Model(String configName, int seedsToFind, int searchRadius, int incrementer, int biomePrecision, SearchType searchType, MCVersion selectedVersion, WorldType worldType, int threadsToUse, boolean shouldSearchAroundSpawnPoint, BiomeList_Model biomeList, BiomeSetList_Model biomeSetList) {
        this.configName = configName;
        this.seedsToFind = seedsToFind;
        this.searchRadius = searchRadius;
        this.incrementer = incrementer;
        this.biomePrecision = biomePrecision;
        this.searchType = searchType;
        this.selectedVersion = selectedVersion;
        this.worldType = worldType;
        this.threadsToUse = threadsToUse;
        this.spawnType = spawnType;
        this.biomeList = biomeList;
        this.biomeSetList = biomeSetList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Searcher_Model searcher_model;
        try {
            searcher_model = (Searcher_Model) super.clone();
            if (biomeList != null)
                searcher_model.biomeList = (BiomeList_Model) biomeList.clone();
            if (biomeSetList != null)
                searcher_model.biomeSetList = (BiomeSetList_Model) biomeSetList.clone();
        } catch (CloneNotSupportedException e) { // this should never happen
            System.out.println("CloneNotSupportedException thrown " + e);
            return null;
        }
        return searcher_model;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public int getSeedsToFind() {
        return seedsToFind;
    }

    public void setSeedsToFind(int seedsToFind) {
        this.seedsToFind = seedsToFind;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    public int getIncrementer() {
        return incrementer;
    }

    public void setIncrementer(int incrementer) {
        this.incrementer = incrementer;
    }

    public int getBiomePrecision() {
        return biomePrecision;
    }

    public void setBiomePrecision(int biomePrecision) {
        this.biomePrecision = biomePrecision;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public MCVersion getSelectedVersion() {
        return selectedVersion;
    }

    public void setSelectedVersion(MCVersion selectedVersion) {
        this.selectedVersion = selectedVersion;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public void setWorldType(WorldType worldType) {
        this.worldType = worldType;
    }

    public int getThreadsToUse() {
        return threadsToUse;
    }

    public void setThreadsToUse(int threadsToUse) {
        this.threadsToUse = threadsToUse;
    }

    public SpawnType getSpawnType() {
        return spawnType;
    }

    public void setSpawnType(SpawnType spawnType) {
        this.spawnType = spawnType;
    }

    public BiomeList_Model getBiomeList() {
        return biomeList;
    }

    public void setBiomeList(BiomeList_Model biomeList) {
        this.biomeList = biomeList;
    }

    public BiomeSetList_Model getBiomeSetList() {
        return biomeSetList;
    }

    public void setBiomeSetList(BiomeSetList_Model biomeSetList) {
        this.biomeSetList = biomeSetList;
    }

}

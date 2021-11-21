package sassa.models;


import com.seedfinding.mccore.version.MCVersion;
import sassa.models.features.Feature_Registry;

import java.util.ArrayList;
import java.util.List;

public class FeatureList_Model {

    List<Feature_Model> featureList;

    public FeatureList_Model(List<Feature_Model> featureList) {
        this.featureList = featureList;
    }

    public FeatureList_Model() {
        this.featureList = new ArrayList<>();
    }

    public void addFeature(Feature_Model feature) {
        featureList.add(feature);
    }

    public boolean isEmpty() {
        if (featureList.isEmpty()) {
            return true;
        }
        return false;
    }

    public void addFeatures(List<Feature_Model> features) {
        features.forEach(featureFactory -> addFeature(featureFactory));
    }

    public List<Feature_Model> getCreatedFeatureListFromVersion(MCVersion version) {
        List<Feature_Model> createdFeatures = new ArrayList<>();
        this.featureList.forEach(feature -> {
            Feature_Registry.FeatureFactory<?> featureFactory = Feature_Registry.REGISTRY.get(feature.getFeatureAsString());
            createdFeatures.add(new Feature_Model(featureFactory.create(version), feature.getFeatureAsString(), feature.getAmount()));
        });
        return createdFeatures;
    }

    public List<Feature_Model> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Feature_Model> featureList) {
        this.featureList = featureList;
    }


}



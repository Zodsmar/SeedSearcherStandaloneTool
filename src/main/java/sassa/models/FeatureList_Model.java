package sassa.models;


import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.Feature;
import sassa.models.features.Feature_Registry;

import java.util.ArrayList;
import java.util.List;

public class FeatureList_Model {

    List<String> featureList;

    public FeatureList_Model(List<String> featureList) {
        this.featureList = featureList;
    }

    public FeatureList_Model() {
        this.featureList = new ArrayList<>();
    }

    public void addFeature(String feature) {
        featureList.add(feature);
    }

    public void addFeatures(List<String> features) {
        features.forEach(featureFactory -> addFeature(featureFactory));
    }

    public List<Feature> getCreatedFeatureListFromVersion(MCVersion version) {
        List<Feature> createdFeatures = new ArrayList<>();
        this.featureList.forEach(feature -> {
            Feature_Registry.FeatureFactory<?> featureFactory = Feature_Registry.REGISTRY.get(feature);
            createdFeatures.add(featureFactory.create(version));
        });
        return createdFeatures;
    }

    public List<String> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }


}



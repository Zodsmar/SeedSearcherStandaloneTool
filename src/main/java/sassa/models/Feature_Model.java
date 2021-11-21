package sassa.models;

import com.seedfinding.mcfeature.Feature;

public class Feature_Model {
    private int amount;
    private String featureAsString;
    private Feature feature;

    public Feature_Model(String featureAsString) {
        this.featureAsString = featureAsString;
        this.amount = 1;
    }

    public Feature_Model(String featureAsString, int amount) {
        this.featureAsString = featureAsString;
        if (amount < 1) {
            amount = 1;
        }
        this.amount = amount;
    }

    public Feature_Model(Feature feature, String featureAsString, int amount) {
        this.feature = feature;
        this.featureAsString = featureAsString;
        this.amount = amount;
    }

    public Feature getFeature() {
        return feature;
    }

    public int getAmount() {
        return amount;
    }

    public String getFeatureAsString() {
        return featureAsString;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void getFeatureAsString(String featureAsString) {
        this.featureAsString = featureAsString;
    }
}

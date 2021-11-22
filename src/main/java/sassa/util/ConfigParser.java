package sassa.util;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.version.MCVersion;
import sassa.enums.SearchType;
import sassa.enums.SpawnType;
import sassa.enums.WorldType;
import sassa.models.BiomeSet_Model;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigParser {

    public void WriteConfigFile(Searcher_Model model) {
        Properties prop = new Properties();

        try {
            //set the properties value
            prop.setProperty("name", model.getConfigName());
            prop.setProperty("mcVersion", model.getSelectedVersion().toString());
            prop.setProperty("amountOfSeedsToFind", String.valueOf(model.getSeedsToFind()));
            prop.setProperty("searchRadius", String.valueOf(model.getSearchRadius()));
            prop.setProperty("searchType", model.getSearchType().toString());
            prop.setProperty("worldType", model.getWorldType().toString());
            prop.setProperty("incrementer", String.valueOf(model.getIncrementer()));
            prop.setProperty("biomePrecision", String.valueOf(model.getBiomePrecision()));
            prop.setProperty("threadsToUse", String.valueOf(model.getThreadsToUse()));
            prop.setProperty("spawnType", String.valueOf(model.getSpawnType().toString()));
            prop.setProperty("biomeListIncluded", createBiomesAsStringList(model.getBiomeList().getIncludedBiomes()));
            prop.setProperty("biomeListExcluded", createBiomesAsStringList(model.getBiomeList().getExcludedBiomes()));
            prop.setProperty("biomeSetListIncluded", createBiomeSetAsStringList(model.getBiomeSetList().getIncludedBiomeSet()));
            prop.setProperty("biomeSetListExcluded", createBiomeSetAsStringList(model.getBiomeSetList().getExcludedBiomeSet()));
            prop.setProperty("includedFeatureList", createFeaturesAsStringList(model.getIncludedFeatures().getFeatureList()));
            prop.setProperty("startRange", String.valueOf(model.getStartRange()));
            prop.setProperty("endRange", String.valueOf(model.getEndRange()));
            prop.setProperty("setSeedFile", model.getSeedFile());
            //save properties to project root folder
            prop.store(new FileOutputStream(model.getConfigName() + ".sassa"), "Sassa Config");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Searcher_Model ReadConfigFile(String configFileName) {

        Searcher_Model searcher_model = new Searcher_Model();
        File configFile = new File(configFileName + ".sassa");

        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);


            searcher_model.setConfigName(props.getProperty("name"));
            searcher_model.setSelectedVersion(MCVersion.fromString(props.getProperty("mcVersion")));
            searcher_model.setSeedsToFind(Integer.parseInt(props.getProperty("amountOfSeedsToFind")));
            searcher_model.setSearchRadius(Integer.parseInt(props.getProperty("searchRadius")));
            searcher_model.setSearchType(SearchType.valueOf(props.getProperty("searchType")));
            searcher_model.setWorldType(WorldType.valueOf(props.getProperty("worldType")));
            searcher_model.setIncrementer(Integer.parseInt(props.getProperty("incrementer")));
            searcher_model.setBiomePrecision(Integer.parseInt(props.getProperty("biomePrecision")));
            searcher_model.setThreadsToUse(Integer.parseInt(props.getProperty("threadsToUse")));
            searcher_model.setSpawnType(SpawnType.valueOf(props.getProperty("spawnType")));
            searcher_model.getBiomeList().setIncludedBiomes(readStringListAsBiomes(props.getProperty("biomeListIncluded")));
            searcher_model.getBiomeList().setExcludedBiomes(readStringListAsBiomes(props.getProperty("biomeListExcluded")));
            searcher_model.getBiomeSetList().setIncludedBiomeSet(readStringListAsBiomeSet(props.getProperty("biomeSetListIncluded")));
            searcher_model.getBiomeSetList().setExcludedBiomeSet(readStringListAsBiomeSet(props.getProperty("biomeSetListExcluded")));
            searcher_model.getIncludedFeatures().setFeatureList(readStringListAsFeatures(props.getProperty("includedFeatureList")));
            searcher_model.setStartRange(Long.parseLong(props.getProperty("startRange")));
            searcher_model.setEndRange(Long.parseLong(props.getProperty("endRange")));
            searcher_model.setSeedFile(props.getProperty("setSeedFile"));

            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

        return searcher_model;
    }

    String createBiomeSetAsStringList(List<BiomeSet_Model> biomeSet) {
        String biomeSetListAsString = "";
        for (BiomeSet_Model b : biomeSet
        ) {
            biomeSetListAsString += b.getName() + "," + createBiomesAsStringList(b.getBiomes()) + ";";
        }
        System.out.println(biomeSetListAsString);
        return biomeSetListAsString;
    }

    String createBiomesAsStringList(List<Biome> biomeList) {
        String biomesListAsString = "";
        for (Biome b : biomeList) {
            biomesListAsString += b.getId() + ",";
        }
        return biomesListAsString;
    }

    List<BiomeSet_Model> readStringListAsBiomeSet(String biomeSet) {
        List<BiomeSet_Model> biomeSetListAsString = new ArrayList<>();
        String[] individualBiomeSet = biomeSet.split(";");
        for (String b : individualBiomeSet
        ) {
            if (!b.isEmpty()) {
                String[] individualBiomes = b.split(",", 2);

                BiomeSet_Model bsModel = new BiomeSet_Model(individualBiomes[0], readStringListAsBiomes(individualBiomes[1]));
                biomeSetListAsString.add(bsModel);
            }

        }
        System.out.println(biomeSetListAsString);
        return biomeSetListAsString;
    }

    List<Biome> readStringListAsBiomes(String biomeList) {
        List<Biome> biomesListAsString = new ArrayList<>();
        String[] individualBiomes = biomeList.split(",");
        for (String b : individualBiomes) {
            if (!b.isEmpty()) {
                biomesListAsString.add(Biomes.REGISTRY.get(Integer.parseInt(b)));
            }

        }
        return biomesListAsString;
    }

    String createFeaturesAsStringList(List<Feature_Model> featureList) {
        String featureListAsString = "";
        for (Feature_Model f : featureList) {
            if (f.getAmount() != 1) {
                featureListAsString += f.getFeatureAsString() + "," + f.getAmount() + ";";
            } else {
                featureListAsString += f.getFeatureAsString() + ";";
            }

        }
        return featureListAsString;
    }

    List<Feature_Model> readStringListAsFeatures(String featureList) {
        List<Feature_Model> stringListAsFeature = new ArrayList<>();
        String[] individualFeatures = featureList.split(";");
        for (String f : individualFeatures) {
            if (!f.isEmpty()) {
                String[] splitForAmount = f.split(",");
                if (splitForAmount.length == 1) {
                    stringListAsFeature.add(new Feature_Model(splitForAmount[0]));
                } else {
                    stringListAsFeature.add(new Feature_Model(splitForAmount[0], Integer.parseInt(splitForAmount[1])));
                }
            }

        }
        return stringListAsFeature;
    }
}

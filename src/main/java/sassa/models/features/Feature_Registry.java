package sassa.models.features;

import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.Decorator;
import com.seedfinding.mcfeature.structure.*;

import java.util.HashMap;
import java.util.Map;

public class Feature_Registry {

    public static final Map<String, FeatureFactory<?>> REGISTRY = new HashMap<>();

    public static final String BASTIONREMNANT = register(BastionRemnant.class.getSimpleName(), BastionRemnant::new);
    public static final String BURIEDTREASURE = register(BuriedTreasure.class.getSimpleName(), BuriedTreasure::new);
    public static final String DESERTPYRAMID = register(DesertPyramid.class.getSimpleName(), DesertPyramid::new);
    public static final String ENDCITY = register(EndCity.class.getSimpleName(), EndCity::new);
    public static final String FORTRESS = register(Fortress.class.getSimpleName(), Fortress::new);
    public static final String IGLOO = register(Igloo.class.getSimpleName(), Igloo::new);
    public static final String JUNGLEPYRAMID = register(JunglePyramid.class.getSimpleName(), JunglePyramid::new);
    public static final String MANSION = register(Mansion.class.getSimpleName(), Mansion::new);
    public static final String MINESHAFT = register(Mineshaft.class.getSimpleName(), Mineshaft::new);
    public static final String MONUMENT = register(Monument.class.getSimpleName(), Monument::new);
    public static final String NETHERFOSSIL = register(NetherFossil.class.getSimpleName(), NetherFossil::new);
    public static final String OCEANRUIN = register(OceanRuin.class.getSimpleName(), OceanRuin::new);
    public static final String PILLAGEROUTPOST = register(PillagerOutpost.class.getSimpleName(), PillagerOutpost::new);
    public static final String SHIPWRECK = register(Shipwreck.class.getSimpleName(), Shipwreck::new);
    public static final String SWAMPHUT = register(SwampHut.class.getSimpleName(), SwampHut::new);
    public static final String VILLAGE = register(Village.class.getSimpleName(), Village::new);
    public static final String STRONGHOLD = register(Stronghold.class.getSimpleName(), Stronghold::new);

    //These are subclasses of RuinedPortal to know which one was selected and being searched
    public static final String OWRUINEDPORTAL = register(OWRuinedPortal.class.getSimpleName(), OWRuinedPortal::new);
    public static final String NERUINEDPORTAL = register(NERuinedPortal.class.getSimpleName(), NERuinedPortal::new);

    public static void test() {
        FeatureFactory<?> featureFactory = REGISTRY.get(Feature_Registry.BASTIONREMNANT);
        Feature feature = featureFactory.create(MCVersion.latest());
        Structure<?, ?> structure = (Structure<?, ?>) feature;
        if (feature instanceof Decorator<?, ?>) {

        }
        //structure.canSpawn();
        Decorator<?, ?> decorator = (Decorator<?, ?>) feature;
        //decorator.canSpawn();

        RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>) feature;
        Village village = (Village) regionStructure;
        feature.getValidDimension();
    }

    public static <T extends Feature<?, ?>> String register(String clazz, FeatureFactory<T> factory) {
        REGISTRY.put(clazz, factory);
        return clazz;
    }

    @FunctionalInterface
    public interface FeatureFactory<T extends Feature<?, ?>> {
        T create(MCVersion version);

    }
}

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

        //feature.canSpawn();
        //decorator.canSpawn();
        Stronghold stronghold = (Stronghold) feature;


        RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>) feature;
        Village village = (Village) regionStructure;

        feature.getValidDimension();
//
//        List<CPos> possibleChunks = new ArrayList<>();
//        BPos center = new BPos(0, 0, 0); //This is origin (0,0)
//        int chunkInRegion = regionStructure.getSpacing();
//        int regionSize = chunkInRegion * 16;
//        SpiralIterator<RPos> spiralIterator = new SpiralIterator<>(
//                new RPos(center.toRegionPos(regionSize).getX(), center.toRegionPos(regionSize).getZ(), regionSize),
//                new BPos(-searchRadius.x, 0, -searchRadius.z).toRegionPos(regionSize), new BPos(searchRadius.x, 0, searchRadius.z).toRegionPos(regionSize),
//                1, (x, y, z) -> new RPos(x, z, regionSize)
//        );
//        spiralIterator.forEach(rPos -> {
//            CPos cpos = structure.getInRegion(terrainGenerator.getWorldSeed(), rPos.getX(), rPos.getZ(), chunkRand);
//            if (cpos == null) {
//                //do something
//            }
//            // TODO Why is this line needed
//            if (struct.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > model.getSearchRadius() >> 4) {
//                //do something
//            }
//
//            //The structure can spawn here need to check against biomes
//            possibleChunks.add(struct);
//        });
//
//        regionStructure.getInRegion(seed, RPos.getX(), RPos.getZ(), chunkRand);

//        public static void desertPyramid(MCVersion version, long worldSeed) {
//            // For optimization we ask you to create the chunkRand, this should be done per thread
//            ChunkRand rand = new ChunkRand();
//            // Create my structure
//            DesertPyramid desertPyramid = new DesertPyramid(version);
//            // Create my factory
//            Generator.GeneratorFactory<?> generatorFactory = Generators.get(desertPyramid.getClass());
//            assert generatorFactory != null;
//            // Create my generator
//            Generator structureGenerator = generatorFactory.create(version);
//            assert structureGenerator instanceof DesertPyramidGenerator;
//            // Generate my biome source
//            BiomeSource source = BiomeSource.of(Dimension.OVERWORLD, version, worldSeed);
//            // Generate my TerrainGenerator
//            TerrainGenerator terrainGenerator = TerrainGenerator.of(Dimension.OVERWORLD, source);
//
//            // Choose a valid chunk position for my structure
//            // here we chose 24 and 49 as the region coordinates, remember region are structure dependant
//            // so use structure#getSpacing() to change base
//            // getInRegion guarantee that in that region that structure canStart
//            CPos pos = desertPyramid.getInRegion(worldSeed, 24, 49, rand);
//            assertTrue(pos.toRegionPos(desertPyramid.getSpacing()).equals(new RPos(24, 49, desertPyramid.getSpacing())));
//            // Alternatively you can get the data at a specific chunk position, however you will need to check canStart then
//            // We don't recommend this as you usually never know the chunk position beforehand...
//            @SuppressWarnings("unchecked")
//            RegionStructure.Data<DesertPyramid> data = (RegionStructure.Data<DesertPyramid>)desertPyramid.at(782, 1584);
//            assertTrue(desertPyramid.canStart(data, worldSeed, rand));
//
//            // Verify that this chunk position is a valid spot to spawn
//            assertTrue(desertPyramid.canSpawn(pos, source));
//            // Alternatively if you had a data
//            assertTrue(desertPyramid.canSpawn(data, source));
//
//            // Verify that this chunk position is a valid spot to generate terrain wise
//            // (not all structure have this check, desert pyramid doesn't need it for instance)
//            assertTrue(desertPyramid.canGenerate(pos, terrainGenerator));
//            // Alternatively if you had a data
//            assertTrue(desertPyramid.canGenerate(data, terrainGenerator));
//
//            // Generate the chest position for that structure at that valid chunk position (rand is optional but encouraged)
//            assertTrue(structureGenerator.generate(terrainGenerator, pos, rand));
//            // Alternatively if you had a data
//            assertTrue(structureGenerator.generate(terrainGenerator, data.chunkX, data.chunkZ, rand));
//
//            // You can chest the chest positions here
//            System.out.println(structureGenerator.getChestsPos());
//            assertTrue(structureGenerator.getChestsPos().size() == 4);
//
//            // Get the loot that generated in those chest positions (indexed allow to create a chest as in Minecraft with randomized slots and EMPTY_ITEM)
//            List<ChestContent> chests = desertPyramid.getLoot(worldSeed, structureGenerator, rand, false);
//            // the result is a hashmap with each possible type of chest (there could be multiple instance of that type of chest)
//            // with a list of chest content attached to each.
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

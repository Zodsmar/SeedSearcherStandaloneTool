long worldSeed = 4320562085990449695L;
        int searchRadius = 1000;
        int incrementer = 50;

        long startTime = System.nanoTime();

        ArrayList<Biome> biomesToFind = new ArrayList<>();
        biomesToFind.add(Biome.DEEP_WARM_OCEAN);
//        biomesToFind.add(Biome.OCEAN);
//        biomesToFind.add(Biome.FOREST);
//        biomesToFind.add(Biome.FLOWER_FOREST);
        //biomesToFind.add(Biome.MUSHROOM_FIELDS);

        ArrayList<Biome.Category> cat = new ArrayList<>();
        cat.add(Biome.Category.FOREST);
        cat.add(Biome.Category.ICY);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = biomeSearcher.findBiomeFromCategory(searchRadius, new Random().nextLong(), cat, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            //System.out.println(count++);
//        } while(!b);


        ArrayList<RegionStructure<?, ?>> structuresToFind = new ArrayList<>();
        structuresToFind.add(VILLAGE);
        structuresToFind.add(MONUMENT);
        structuresToFind.add(DESERT_PYRAMID);
        structuresToFind.add(PILLAGER_OUTPOST);
//        structuresToFind.add(IGLOO);
        structuresToFind.add(SWAMP_HUT);
        structuresToFind.add(MANSION);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = BiomeSearcher.findBiome(searchRadius, new Random().nextLong(), Biome.DEEP_WARM_OCEAN, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            System.out.println(count++);
//        } while(!b);

        //Searcher.searchRandomly(searchRadius, structuresToFind, biomesToFind, "OVERWORLD", incrementer, 16);
        //biomeSearcher.findBiome(searchRadius, worldSeed, Biome.PLAINS, "OVERWORLD", incrementer);
//
        //structureSearcher.findStructure(searchRadius, worldSeed, VILLAGE, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MONUMENT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, DESERT_PYRAMID, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, PILLAGER_OUTPOST, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, IGLOO, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, SWAMP_HUT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MANSION, "OVERWORLD");



        //StructureSearcher.findStructureRandomly(searchRadius, structuresToFind, "OVERWORLD", 16);

        //structureSearcher.findStructure(searchRadius, worldSeed, FORTRESS, "NETHER");
        //structureSearcher.findStructure(searchRadius, worldSeed, END_CITY, "END");
        //structureSearcher.findMineshaft(searchRadius, worldSeed, MINESHAFT);
        long elapsedTime = System.nanoTime() - startTime;

        System.out.println(elapsedTime/1000000 + "/ms");


//structureSearcher.findMineshaft(1024, 4320562085990449695L, MCVersion.v1_15, Mineshaft.Type.EITHER);

public static void findStructureRandomly(int searchSize, Collection<RegionStructure<?, ?>> list, String dimension, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        long rejectedSeeds = 0;

        Map<RegionStructure<?, ?>, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++, structures.clear()) {
        for(RegionStructure<?, ?> searchStructure: list) {
        RegionStructure.Data<?> lowerBound = searchStructure.at(-searchSize >> 4, -searchSize >> 4);
        RegionStructure.Data<?> upperBound = searchStructure.at(searchSize >> 4, searchSize >> 4);

        List<CPos> foundStructures = new ArrayList<>();

        for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
        for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
        CPos struct = searchStructure.getInRegion(structureSeed, regionX, regionZ, rand);
        if(struct == null)continue;
        if(struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) > searchSize >> 4)continue;
        foundStructures.add(struct);
        }
        }

        if(foundStructures.isEmpty())break;
        structures.put(searchStructure, foundStructures);
        }

        if(structures.size() != list.size()) {
        rejectedSeeds += 1L << biomePrecision;
        continue;
        }

        System.out.println("Found structure seed " + structureSeed + ", checking biomes...");

        for(long upperBits = 0; upperBits < 1L << biomePrecision; upperBits++, rejectedSeeds++) {
        long worldSeed = (upperBits << 48) | structureSeed;

        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        int structureCount = 0;

        for(Map.Entry<RegionStructure<?, ?>, List<CPos>> e : structures.entrySet()) {
        RegionStructure<?, ?> structure = e.getKey();
        List<CPos> starts = e.getValue();

        for(CPos start : starts) {
        if(!structure.canSpawn(start.getX(), start.getZ(), source))continue;
        structureCount++;
        break;
        }
        }
        if(structureCount != list.size())continue;

        System.out.format("Found world seed %d with structure seed %d (rejected %d)\n", worldSeed, structureSeed, rejectedSeeds);
        return;
        }
        }
        }


/**
 * Finds all Overworld Structures except Strongholds and Mineshafts
 * @param searchSize
 * @param worldSeed
 * @param structure
 * @param dimension
 */
public static void findStructureSingle(int searchSize, long worldSeed, RegionStructure<?, ?> structure, String dimension) {
        ChunkRand rand = new ChunkRand();
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        RegionStructure.Data<?> lowerBound = structure.at(-searchSize >> 4, -searchSize >> 4);
        RegionStructure.Data<?> upperBound = structure.at(searchSize >> 4, searchSize >> 4);

        int count = 0;

        for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
        for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
        CPos struct = structure.getInRegion(worldSeed, regionX, regionZ, rand);
        if(struct == null)continue;
        if(struct.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > searchSize >> 4)continue;
        if(!structure.canSpawn(struct.getX(), struct.getZ(), source))continue;

        //System.out.println("Found world seed " + worldSeed + " with structure seed " + structureSeed);
        //System.out.println("The structure is at (" + struct.getX() * 16 + ", " + struct.getZ() * 16 + ")");
        count++;
        }
        }

        System.out.println(structure.toString() + ": "+ count);
        }

/**
 * This should be the fastest method by far starting with structures first then biomes
 * @param searchSize - radius from 0, 0
 * @param sList - structure list (ex. MANSION)
 * @param bList - biome list (ex. Biome.JUNGLE)
 * @param dimension - "OVERWORLD", "NETHER", "END"
 * @param incrementer - the amount of blocks to skip for biome searching
 */
public static void searchRandomly_old(int searchSize, Collection<RegionStructure<?, ?>> sList, Collection<Biome> bList, String dimension, int incrementer, int biomePrecision) {
        Vec3i origin = new Vec3i(0, 0,0);
        ChunkRand rand = new ChunkRand();
        int rejectedSeeds = 0;

        Map<RegionStructure<?, ?>, List<CPos>> structures = new HashMap<>();

        for(long structureSeed = 0; structureSeed < 1L << 48; structureSeed++, structures.clear()) {
        for(RegionStructure<?, ?> searchStructure: sList) {
        RegionStructure.Data<?> lowerBound = searchStructure.at(-searchSize >> 4, -searchSize >> 4);
        RegionStructure.Data<?> upperBound = searchStructure.at(searchSize >> 4, searchSize >> 4);

        List<CPos> foundStructures = new ArrayList<>();

        for(int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
        for(int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
        CPos struct = searchStructure.getInRegion(structureSeed, regionX, regionZ, rand);
        if(struct == null)continue;
        if(struct.distanceTo(origin, DistanceMetric.CHEBYSHEV) > searchSize >> 4)continue;
        foundStructures.add(struct);
        }
        }

        if(foundStructures.isEmpty())break;
        structures.put(searchStructure, foundStructures);
        }

        if(structures.size() != sList.size()) {
        rejectedSeeds += 1L << biomePrecision;
        continue;
        }

        System.out.println("Found structure seed " + structureSeed + ", checking biomes...");

        for(long upperBits = 0; upperBits < 1L << biomePrecision; upperBits++, rejectedSeeds++) {
        long worldSeed = (upperBits << 48) | structureSeed;

        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        int structureCount = 0;

        for(Map.Entry<RegionStructure<?, ?>, List<CPos>> e : structures.entrySet()) {
        RegionStructure<?, ?> structure = e.getKey();
        List<CPos> starts = e.getValue();

        for(CPos start : starts) {
        if(!structure.canSpawn(start.getX(), start.getZ(), source))continue;
        structureCount++;
        break;
        }
        }

        if(structureCount != sList.size())continue;

        boolean allBiomesFound = BiomeSearcher.findBiomeFromSource(searchSize, bList, source, incrementer);
        if(!allBiomesFound)continue;

        System.out.format("Found world seed %d with structure seed %d (rejected %d)\n", worldSeed, structureSeed, rejectedSeeds);
        return;
        }
        }

        }

public static boolean findBiomeFromSource(int searchSize, Collection<Biome> biomeToFind, BiomeSource source, int incrementer) {
        for(int i = -searchSize; i < searchSize; i += incrementer) {
        for(int j = -searchSize; j < searchSize; j += incrementer) {
        biomeToFind.remove(source.getBiome(i, 0, j));

        if(biomeToFind.isEmpty()) {
        return true;
        }
        }
        }

        return false;
        }

public static boolean findBiomeSingle(int searchSize, long worldSeed, Biome biomeToFind, String dimension, int incrementer) {
        BiomeSource source = Searcher.getBiomeSource(dimension, worldSeed);

        for(int i = -searchSize; i < searchSize; i += incrementer) {
        for(int j = -searchSize; j < searchSize; j += incrementer) {
        if(source.getBiome(i, 0, j) == biomeToFind) {
        System.out.format("Found world seed %d (Shadow %d), position of Biome X, Z: %d, %d\n", worldSeed, WorldSeed.getShadowSeed(worldSeed), i, j);
        return true;
        }

        }
        }

        return false;
        }

public static ArrayList<RegionStructure<?,?>> getStructuresFromUI(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        ArrayList<RegionStructure<?,?>> structuresList = new ArrayList<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
        Iterator regIt = Structures.STRUCTURE.entrySet().iterator();
        while(regIt.hasNext()){
        Map.Entry mapElement = (Map.Entry)regIt.next();
        String name = (String) mapElement.getKey();
        StructureProvider s = (StructureProvider) mapElement.getValue();
        if(name == checkedTexts.get(i)){
        structuresList.add(s.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion()));
        System.out.println(name);
        }
        }
        }
        return structuresList;
        }


// Returns Map with Dimensions
public static HashMap<String, RegionStructure<?,?>> getStructuresFromUIWithDim(GridPane pane, String inORex){
        List<String> checkedTexts = comboBoxManager(pane, inORex);
        HashMap<String, RegionStructure<?,?>> structuresList = new HashMap<>();
        for (int i = 0; i < checkedTexts.size(); i++) {
        Iterator regIt = Structures.STRUCTURE.entrySet().iterator();
        while(regIt.hasNext()){
        Map.Entry mapElement = (Map.Entry)regIt.next();
        String name = (String) mapElement.getKey();
        StructureProvider s = (StructureProvider) mapElement.getValue();
        if(name == checkedTexts.get(i)){
        structuresList.put(s.getDimension(), s.getStructureSupplier().create(Singleton.getInstance().getMinecraftVersion()));
        }
        }
        }
        return structuresList;
        }

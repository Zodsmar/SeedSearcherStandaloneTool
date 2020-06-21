package sassa.old;

import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.parsing.FormatException;
import javafx.application.Platform;
import org.json.simple.parser.ParseException;
import sassa.gui.Variables;
import sassa.gui.guiCollector;
import sassa.util.Singleton;
import sassa.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

public class Searcher_old implements Runnable {

    private WorldBuilder mWorldBuilder;

    private MinecraftInterface minecraftInterface;

    /**
     * The radius of each quadrant of the search area.
     * <p>
     * The value of this field is greater than {@code 0}.
     */
    private int searchRadius;

    /**
     * The number of matching worlds to discover.
     * <p>
     * The value of this field is greater than or equal to {@code 0}.
     */
    private int maximumMatchingWorldsCount;

    private long minSeed;
    private long maxSeed;

    private boolean RANDOM_SEEDS;
    private boolean BEDROCK;
    private boolean quitImmediate;

    public static long currentSeedCheck = 0L;

    guiCollector guiCollector = new guiCollector();
    static Util util = new Util();
    static Singleton singleton = Singleton.getInstance();

    public Searcher_old(String minecraftVersion, int searchRadius, int maximumMatchingWorldsCount, long minSeed, long maxSeed, boolean randSeed, boolean bedrock)
            throws IOException, FormatException, MinecraftInterfaceCreationException {
        this.mWorldBuilder = WorldBuilder.createSilentPlayerless();

        MinecraftInstallation minecraftInstallation;

        String pathToDirectory = singleton.getMCPath().getText();
        if (pathToDirectory == null ||
                pathToDirectory.trim().equals(""))
        {
            minecraftInstallation = MinecraftInstallation.newLocalMinecraftInstallation();
        } else {
            minecraftInstallation = MinecraftInstallation.newLocalMinecraftInstallation(new File(pathToDirectory));
        }
        LauncherProfile launcherProfile = null;
        try{
            launcherProfile = minecraftInstallation.newLauncherProfile(minecraftVersion);
        } catch (FileNotFoundException e) {
            util.console("No install directory found for Minecraft version " + minecraftVersion + "!");
            throw e;
        }
        this.minecraftInterface = MinecraftInterfaces.fromLocalProfile(launcherProfile);
        this.searchRadius = searchRadius;
        this.maximumMatchingWorldsCount = maximumMatchingWorldsCount;
        this.minSeed = minSeed;
        this.maxSeed = maxSeed;
        this.currentSeedCheck = minSeed;
        this.RANDOM_SEEDS = randSeed;
        this.BEDROCK = bedrock;
    }

    /**
     * Creates a random, default world using the default (empty) generator
     * options.
     */
    World createWorld() throws MinecraftInterfaceException {
        Consumer<World> onDispose = world -> {};
        long seedNum = (new Random()).nextLong();
        if(BEDROCK){
            seedNum = (new Random()).nextInt();
            if(seedNum < 0){
                seedNum += 4294967296L;
            }
        }
        if (RANDOM_SEEDS) {
            WorldOptions worldOptions = new WorldOptions(WorldSeed.fromUserInput("" + seedNum), util.getWorldType(singleton.getWorldType().getValue().toString()));
            return this.mWorldBuilder.from(this.minecraftInterface, onDispose, worldOptions);
        } else {
            WorldOptions worldOptions = new WorldOptions(WorldSeed.fromUserInput("" + this.currentSeedCheck), util.getWorldType(singleton.getWorldType().getValue().toString()));
            Variables.updateCurrentSeed(this.currentSeedCheck);
            this.currentSeedCheck++;
            return this.mWorldBuilder.from(this.minecraftInterface, onDispose, worldOptions);
        }
    }

    /**
     * Searches for matching worlds, and prints the seed of each matching world
     * to the given output stream.
     *
     * @throws MinecraftInterfaceCreationException
     * @throws FormatException
     * @throws IOException
     * @throws InterruptedException
     */
    Biome[] biomes = {}; boolean searchBiomes = true;
    Biome[] rejectedBiomes = {}; boolean searchRejectedBiomes = true;
    HashMap<Biome, String> biomeSets = new HashMap<>(); boolean searchBiomeSets = true;
    HashMap<Biome, String> rejectedBiomeSets = new HashMap<>(); boolean searchRejectedBiomesSets = true;
    StructureSearcher_old.Type[] structures = {}; boolean searchStructures = true;
    StructureSearcher_old.Type[] rejectedStructures = {}; boolean searchRejectedStructures = true;

    void search() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException, ParseException {
        Variables.reset();
        util.console("Creating search lists...");

        biomes = guiCollector.getBiomesFromArrayList(Singleton.getInstance().getBiomesGridPane(),"Include");
        rejectedBiomes = guiCollector.getBiomesFromArrayList(Singleton.getInstance().getBiomesGridPane(),"Exclude");
        searchBiomes = guiCollector.checkIfBiomesSelected(biomes, searchBiomes);
        searchRejectedBiomes = guiCollector.checkIfBiomesSelected(rejectedBiomes, searchRejectedBiomes);
        structures = guiCollector.getStructuresFromArrayList(Singleton.getInstance().getStructureGridPane(), "Include");
        searchStructures = guiCollector.checkIfStructuresSelected(structures, searchStructures);
        rejectedStructures = guiCollector.getStructuresFromArrayList(Singleton.getInstance().getStructureGridPane(), "Exclude");
        searchRejectedStructures = guiCollector.checkIfStructuresSelected(rejectedStructures, searchRejectedStructures);
        biomeSets = guiCollector.getBiomesSetsFromHashMap(Singleton.getInstance().getBiomeSetsGridPane(), "Include");
        searchBiomeSets = guiCollector.checkIfBiomeSetsSelected(biomeSets, searchBiomeSets);
        rejectedBiomeSets = guiCollector.getBiomesSetsFromHashMap(Singleton.getInstance().getBiomeSetsGridPane(), "Exclude");
        searchRejectedBiomesSets = guiCollector.checkIfBiomeSetsSelected(rejectedBiomeSets, searchRejectedBiomesSets);

        if (!searchBiomes && !searchRejectedBiomes && !searchBiomeSets && !searchRejectedBiomesSets && !searchStructures && !searchRejectedStructures) {
            util.console("\nNo biomes/structures are selected or rejected!\nPlease select some before starting!\nSearch has been cancelled.\nRecommend you clear the console!\n");
            return;
        }

        if (biomes.length > 0 || !biomeSets.isEmpty()) {
            util.console("Included Biomes:");
            for (Biome biome : biomes) {
                util.console("\t" + biome.getName());
            }
            for (Biome biome : biomeSets.keySet()) {
                util.console("\t" + biome.getName());
            }
        }
        if (rejectedBiomes.length > 0 || !rejectedBiomeSets.isEmpty()) {
            util.console("Excluded Biomes:");
            for (Biome biome : rejectedBiomes) {
                util.console("\t" + biome.getName());
            }
            for (Biome biome : rejectedBiomeSets.keySet()) {
                util.console("\t" + biome.getName());
            }
        }
        if (structures.length > 0) {
            util.console("Included Structures:");
            for (StructureSearcher_old.Type structure : structures) {
                util.console("\t" + structure);
            }
        }
        if (rejectedStructures.length > 0) {
            util.console("Excluded Structures:");
            for (StructureSearcher_old.Type structure : rejectedStructures) {
                util.console("\t" + structure);
            }
        }
        fxmlController_old controller = singleton.getController();
        while (Variables.acceptedWorlds() < this.maximumMatchingWorldsCount && controller.isRunning() && this.currentSeedCheck <= this.maxSeed && !quitImmediate) {
            boolean paused = false;
            if (controller != null)
                paused = controller.isPaused();
            if (!paused) {
                World world;
                Variables.checkWorld();
                try {
                    world = createWorld();
                } catch (MinecraftInterfaceException e) {
                    // TODO log
                    continue;
                }
                boolean isWorldAccepted;
                try {
                    isWorldAccepted = accept(world);
                } catch (MinecraftInterfaceException | UnknownBiomeIndexException | ParseException e) {
                    // Biome data for the world could not be obtained.
                    // Biome data included an unknown biome code.
                    // TODO log
                    continue;
                }
                if (!isWorldAccepted) {
                    continue;
                }
                System.out.println("Valid Seed: " + world.getWorldSeed().getLong());

                util.console((Variables.acceptedWorlds() + 1) + ": " + world.getWorldSeed().getLong() + " (rejected " + Variables.worldsSinceAccepted() + ")");
                Variables.acceptWorld();
            }
        }

        util.console("Finished Search!");
        Platform.runLater(() -> {
            try {
                singleton.getController().stop();
            } catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException e) {
                e.printStackTrace();
            }
        });

    }

    private boolean accept(World world) throws InterruptedException, FormatException, UnknownBiomeIndexException, MinecraftInterfaceException, ParseException, MinecraftInterfaceCreationException, IOException {
        // Look from 0,0 to increase search speed
        CoordinatesInWorld searchCenter = CoordinatesInWorld.origin();

        // Check structures within the area
        if (!StructureSearcher_old.accept(world, minecraftInterface, searchCenter, searchRadius + 256, structures, rejectedStructures)) return false;

        // Set the real spawnpoint
        searchCenter = world.getSpawnWorldIcon().getCoordinates();

        if (searchCenter == null) {
            // The world spawn could not be determined, default back to 0,0
            searchCenter = CoordinatesInWorld.origin();
        }

        // Check structures within the area
        if (!StructureSearcher_old.accept(world, minecraftInterface, searchCenter, searchRadius, structures, rejectedStructures)) return false;
        // Check biomes within the area
        if (!BiomeSearcher_old.accept(world, minecraftInterface, searchCenter, searchRadius, biomes, biomeSets, rejectedBiomes, rejectedBiomeSets)) return false;

        return true;
    }

    /**
     * Searches for matching worlds, and prints the seed of each matching world
     * to the standard output stream.
     */

    public void run() {
        try {
            search();
        } catch (ParseException | InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException e) {
            e.printStackTrace();
        }

    }

    static {
        // By default, AMIDST logs to the standard output stream and to an
        // in-memory buffer.
        // Turn off logging to the standard output stream.
        // Otherwise, the desired output could be overwhelmed by noise.
        AmidstLogger.removeListener("console");
        // Turn off logging to the in-memory buffer.
        // Otherwise, the JVM could run out of heap space when checking many
        // seeds.
        AmidstLogger.removeListener("master");
        // TODO add file logging?
    }
}

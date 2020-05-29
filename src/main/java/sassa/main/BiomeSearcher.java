package sassa.main;

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
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.parsing.FormatException;
import javafx.application.Platform;
import org.json.simple.parser.ParseException;
import sassa.gui.guiCollector;
import sassa.util.Singleton;
import sassa.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * A service that searches for worlds that match specific criteria.
 *
 * @author scudobuio, Zodsmar, YourCoalAlt
 */
public class BiomeSearcher implements Runnable {

	private WorldBuilder mWorldBuilder;

	private MinecraftInterface mMinecraftInterface;

	/**
	 * The width of each quadrant of the search area.
	 * <p>
	 * The value of this field is greater than {@code 0}.
	 */
	private int mSearchQuadrantWidth;

	/**
	 * The height of each quadrant of the search area.
	 * <p>
	 * The value of this field is greater than {@code 0}.
	 */
	private int mSearchQuadrantHeight;

	/**
	 * The number of matching worlds to discover.
	 * <p>
	 * The value of this field is greater than or equal to {@code 0}.
	 */
	private int mMaximumMatchingWorldsCount;

	private long mMinSeed;
	private long mMaxSeed;

	private boolean RANDOM_SEEDS;
	private boolean BEDROCK;
	private boolean quitImmediate;

	public static long currentSeedCheck = 0L;

    guiCollector guiCollector = new guiCollector();
    static Util util = new Util();
    static Singleton singleton = Singleton.getInstance();

	public BiomeSearcher(String minecraftVersion,
			int searchQuadrantWidth, int searchQuadrantHeight, int maximumMatchingWorldsCount, long minSeed, long maxSeed, boolean randSeed, boolean bedrock)
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
		this.mMinecraftInterface = MinecraftInterfaces.fromLocalProfile(launcherProfile);
		this.mSearchQuadrantWidth = searchQuadrantWidth;
		this.mSearchQuadrantHeight = searchQuadrantHeight;
		this.mMaximumMatchingWorldsCount = maximumMatchingWorldsCount;
		this.mMinSeed = minSeed;
		this.mMaxSeed = maxSeed;
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
			return this.mWorldBuilder.from(this.mMinecraftInterface, onDispose, worldOptions);
		} else {
			WorldOptions worldOptions = new WorldOptions(WorldSeed.fromUserInput("" + this.currentSeedCheck), util.getWorldType(singleton.getWorldType().getValue().toString()));
			this.currentSeedCheck++;
			return this.mWorldBuilder.from(this.mMinecraftInterface, onDispose, worldOptions);
		}
	}

	Set<Biome> getBiomes(long nwCornerX, long nwCornerY, int width, int height) throws MinecraftInterfaceException, UnknownBiomeIndexException {
		Set<Biome> biomes = new HashSet<>();
		int[] biomeCodes = this.mMinecraftInterface.getBiomeData(
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerX)),
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerY)),
				width / 4,
				height / 4,
				true // useQuarterResolution
		);
		for(int code: biomeCodes){
			try{
				biomes.add(Biome.getByIndex(code));
			} catch (UnknownBiomeIndexException e) {
				System.out.println("No biome found: " + code + "!");
			}
		}
		return biomes;
	}

	/**
	 * Determines whether to accept a world.
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
	StructureSearcher.Type[] structures = {}; boolean searchStructures = true;
	StructureSearcher.Type[] rejectedStructures = {}; boolean searchRejectedStructures = true;
	//, Biome.forest, Biome.desert, Biome.birchForest, Biome.plains

	boolean accept(World world) throws MinecraftInterfaceException, InterruptedException,
			IOException, FormatException, MinecraftInterfaceCreationException, ParseException, UnknownBiomeIndexException {
		//! This returns the actual spawnpoint or should... but it doesn't it is off. Double checking
		//! in amidst and it is incorrect I created the world to see if this was correct and amidst was off
		//! this is incorrect and amidst is... no idea why...
		// TODO: Look into this (probably will fix the structures being off too...)
		CoordinatesInWorld searchCenter = world.getSpawnWorldIcon().getCoordinates();

		//? Trying to figure out the spawn locations I think there is another step required to actually get it this gets closer ish...
		// searchCenter.getXRelativeToFragment();
		// searchCenter.getYRelativeToFragment();


		// ! This returns [0, 0] everytime
		//CoordinatesInWorld searchCenter = CoordinatesInWorld.origin();

		if (searchCenter == null) {
			// The world spawn could not be determined.
			return false;
		}

		long searchCenterX = searchCenter.getX();
		long searchCenterY = searchCenter.getY();

		Set<StructureSearcher.Type> undiscoveredStructures = new HashSet<>(Arrays.asList(structures));
		// Only search if list not empty
		if (!undiscoveredStructures.isEmpty()) {
			List<StructureSearcher.Type> foundStructures = StructureSearcher.hasStructures(
					undiscoveredStructures,
					world,
					searchCenterX - this.mSearchQuadrantHeight,
					searchCenterY - this.mSearchQuadrantWidth,
					this.mSearchQuadrantHeight * 2,
					this.mSearchQuadrantWidth * 2);
			for (StructureSearcher.Type struct : foundStructures) {
				undiscoveredStructures.remove(struct);
			}

			// Check if any included structures have not been found, if so seed is rejected
			if (!undiscoveredStructures.isEmpty()) {
				return false;
			}
		}

		Set<StructureSearcher.Type> undiscoveredRejectedStructures = new HashSet<>(Arrays.asList(rejectedStructures));
		// Only search if list not empty
		if (!undiscoveredRejectedStructures.isEmpty()) {
			List<StructureSearcher.Type> foundRejectedStructures = StructureSearcher.hasStructures(
					undiscoveredRejectedStructures,
					world,
					searchCenterX - this.mSearchQuadrantHeight,
					searchCenterY - this.mSearchQuadrantWidth,
					this.mSearchQuadrantHeight * 2,
					this.mSearchQuadrantWidth * 2);
			for (StructureSearcher.Type struct : foundRejectedStructures) {
				// Check if any excluded structures have been found, if so seed is rejected
				if(undiscoveredRejectedStructures.contains(struct)){
					return false;
				}
			}
		}

		Set<Biome> undiscoveredBiomes = new HashSet<>(Arrays.asList(biomes));
		Set<Biome> undiscoveredRejectedBiomes = new HashSet<>(Arrays.asList(rejectedBiomes));
		HashMap<Biome, String> undiscoveredBiomeSets = new HashMap<>(biomeSets);
		HashMap<Biome, String> undiscoveredRejectedBiomeSets = new HashMap<>(rejectedBiomeSets);
		// Only search if lists are not empty
		if (!undiscoveredBiomes.isEmpty() || !undiscoveredRejectedBiomes.isEmpty() || !undiscoveredBiomeSets.isEmpty() || !undiscoveredRejectedBiomeSets.isEmpty()) {
			Set<Biome> biomes = getBiomes(
					searchCenterX - this.mSearchQuadrantWidth,
					searchCenterY - this.mSearchQuadrantHeight,
					2 * this.mSearchQuadrantWidth,
					2 * this.mSearchQuadrantHeight);

			for (Biome biome: biomes) {

				// Remove from included biomes list
				undiscoveredBiomes.remove(biome);

				// Check if any excluded biomes have been found, if so seed is rejected
				if (undiscoveredRejectedBiomes.contains(biome)) {
					return false;
				}

				// Remove from included biome sets list
				if (undiscoveredBiomeSets.containsKey(biome)) {
					String setValue = undiscoveredBiomeSets.get(biome);
					// Get the iterator over the HashMap
					undiscoveredBiomeSets.entrySet()
							.removeIf(
									entry -> (setValue.equals(entry.getValue())));
				}

				// Check if any excluded biome sets have been found, if so seed is rejected
				if (undiscoveredRejectedBiomeSets.containsKey(biome)) {
					return false;
				}
			}
		}

		if (undiscoveredBiomes.isEmpty() && undiscoveredBiomeSets.isEmpty() && undiscoveredStructures.isEmpty()) {
			return true;
		}

//		System.out.println(undiscoveredStructures);
//		System.out.println(undiscoveredBiomes);
//		System.out.println(undiscoveredBiomeSets);
		return false;
	}

	/**
	 * Updates the progress output for a world that has been rejected.
	 *
	 * @param rejectedWorldsCount the number of worlds that have been rejected
	 *            since the last world was accepted
	 */



	static void updateRejectedWorldsProgress(int rejectedWorldsCount) {
        singleton.getCRejSeed().setText(""+rejectedWorldsCount);
		singleton.getTRejSeed().setText(""+totalRejectedSeedCount);
		singleton.getSequenceSeed().setText("" + currentSeedCheck);
	}

	/**
	 * Updates the progress output for a world that has been accepted.
	 *
	 * @param rejectedWorldsCount the number of worlds that have been rejected
	 *            since the last world was accepted
	 * @param acceptedWorldsCount the number of worlds that have been accepted,
	 *            including the given world
	 * @param acceptedWorld the world that has been accepted
	 */
	static void updateAcceptedWorldsProgress(
			int rejectedWorldsCount,
			int acceptedWorldsCount,
			World acceptedWorld) {
		if (rejectedWorldsCount / (1 << 4) > 0) {
			// An incomplete line of dots was printed.
			//Util.console("");
		}
		util.console(
				acceptedWorldsCount + ": " + acceptedWorld.getWorldSeed().getLong() + " (rejected "
						+ rejectedWorldsCount + ")");
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
	public static int totalRejectedSeedCount = 0;
	void search() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException, ParseException {
		int rejectedWorldsCount = 0;
		int acceptedWorldsCount = 0;
        totalRejectedSeedCount = 0;
        singleton.getSequenceSeed().setText("" + 0);

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
		rejectedBiomeSets = guiCollector.getBiomesSetsFromHashMap(Singleton.getInstance().getBiomeSetsGridPane(), "Include");
		searchRejectedBiomesSets = guiCollector.checkIfBiomeSetsSelected(rejectedBiomeSets, searchRejectedBiomesSets);

		if (!searchBiomes && !searchRejectedBiomes && !searchBiomeSets && !searchRejectedBiomesSets && !searchStructures && !searchRejectedStructures) {
			util.console("\nNo biomes/structures are selected or rejected!\nPlease select some before starting!\nSearch has been cancelled.\nRecommend you clear the console!\n");
			return;
		}

		while (acceptedWorldsCount < this.mMaximumMatchingWorldsCount && singleton.getController().isRunning() && this.currentSeedCheck <= this.mMaxSeed && !quitImmediate) {
			if (!singleton.getController().isPaused()) {
				World world;
				try {
					world = createWorld();
				} catch (MinecraftInterfaceException e) {
					// TODO log
					rejectedWorldsCount++;
					totalRejectedSeedCount++;
					updateRejectedWorldsProgress(rejectedWorldsCount);
					continue;
				}
				boolean isWorldAccepted;
				try {
					isWorldAccepted = accept(world);
				} catch (MinecraftInterfaceException | UnknownBiomeIndexException | ParseException e) {
					// Biome data for the world could not be obtained.
					// Biome data included an unknown biome code.
					// TODO log
					rejectedWorldsCount++;
					totalRejectedSeedCount++;
					updateRejectedWorldsProgress(rejectedWorldsCount);
					continue;
				}
				if (!isWorldAccepted) {
					rejectedWorldsCount++;
					totalRejectedSeedCount++;
					updateRejectedWorldsProgress(rejectedWorldsCount);
					continue;
				}
				System.out.println("Valid Seed: " + world.getWorldSeed().getLong());
				acceptedWorldsCount++;
				updateAcceptedWorldsProgress(rejectedWorldsCount, acceptedWorldsCount, world);
				rejectedWorldsCount = 0;
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

package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;

import Util.Util;
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
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;
import amidst.parsing.FormatException;
import gui.GUI;
import main.StructureSearcher.Type;

/**
 * A service that searches for worlds that match specific criteria.
 *
 * @author scudobuio, Zodsmar, YourCoalAlt
 */
public class BiomeSearcher implements Runnable {

	private WorldBuilder mWorldBuilder;

	private MinecraftInterface mMinecraftInterface;

	public static enum SearchCenterKind {
		// World origin.
		// Always (0, 0).
		ORIGIN,

		// Northwest corner of the 1:1 map that contains the world origin.
		// Always (-64, -64).
		MAP_ORIGIN,

		// World spawn.
		SPAWN,

		// Center point of the spawn chunks.
		// Always the corner of four chunks, the middle side of two chunks, or
		// the center of a chunk.
		SPAWN_CHUNKS;
	}

	/**
	 * The specification of the center of the search area.
	 */
	public SearchCenterKind mSearchCenterKind;

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

	public BiomeSearcher( String minecraftVersion, SearchCenterKind searchCenterKind,
			int searchQuadrantWidth, int searchQuadrantHeight, int maximumMatchingWorldsCount)
			throws IOException, FormatException, MinecraftInterfaceCreationException {
		this.mWorldBuilder = WorldBuilder.createSilentPlayerless();
		final MinecraftInstallation minecraftInstallation = MinecraftInstallation.newLocalMinecraftInstallation();
		LauncherProfile launcherProfile = null;
		try{
			launcherProfile = minecraftInstallation.newLauncherProfile(minecraftVersion);
		} catch (FileNotFoundException e) {
			Util.console("No install directory found for Minecraft version " + minecraftVersion + "!");
			throw e;
		}
		this.mMinecraftInterface = MinecraftInterfaces.fromLocalProfile(launcherProfile);
		this.mSearchCenterKind = searchCenterKind;
		this.mSearchQuadrantWidth = searchQuadrantWidth;
		this.mSearchQuadrantHeight = searchQuadrantHeight;
		this.mMaximumMatchingWorldsCount = maximumMatchingWorldsCount;
	}

	/**
	 * Creates a random, default world using the default (empty) generator
	 * options.
	 */
	World createWorld() throws MinecraftInterfaceException {
		// MainGUI.timeElapsed.setText("Time Elapsed: " +
		// Util.getElapsedTimeHoursMinutesFromMilliseconds(elapsedTime =
		// System.currentTimeMillis() - startTime));
		Consumer<World> onDispose = world -> {
		};
		WorldOptions worldOptions = new WorldOptions(WorldSeed.random(), WorldType.DEFAULT);
		return this.mWorldBuilder.from(this.mMinecraftInterface, onDispose, worldOptions);
	}

	static final CoordinatesInWorld CIW_ORIGIN = CoordinatesInWorld.from(0L, 0L);
	static final CoordinatesInWorld CIW_MAP_ORIGIN = CoordinatesInWorld.from(-64L, -64L);

	/**
	 * Gets the coordinates of the world spawn.
	 * <p>
	 * The determination of the world spawn is a best effort. Sometimes, the
	 * world spawn cannot be determined, or the world spawn is determined
	 * incorrectly. Consequently, the world spawn should be verified in game.
	 *
	 * @return {@code null}, if the coordinates of the world spawn could not be
	 *         determined
	 */
	static CoordinatesInWorld getSpawn(World world) {
		// This oracle should be equivalent to the (inaccessible) oracle wrapped
		// by world.getSpawnProducer().
		WorldSpawnOracle worldSpawnOracle = new HeuristicWorldSpawnOracle(
				world.getWorldSeed().getLong(),
				world.getBiomeDataOracle(),
				world.getVersionFeatures().getValidBiomesForStructure_Spawn());
		return worldSpawnOracle.get();
	}

	/**
	 * Rounds a coordinate of the world spawn toward the center of the spawn
	 * chunks.
	 */
	static long getSpawnChunksCenterCoordinate(long spawnCoordinate) {
		int spawnCoordinateRelativeToChunk = (int) (spawnCoordinate & 0xFL);
		switch (spawnCoordinateRelativeToChunk) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7: {
			// Round down toward the nw corner.
			return (spawnCoordinate & ~0xCL);
		}
		case 8: {
			// The coordinate of the world spawn is in the exact middle of a
			// chunk.
			return spawnCoordinate;
		}
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15: {
			// Round up toward the se corner.
			return (spawnCoordinate & ~0xCL) + 16;
		}
		default: {
			throw new AssertionError(spawnCoordinateRelativeToChunk);
		}
		}
	}

	/**
	 * Gets the coordinates of the center point of the spawn chunks.
	 * <p>
	 * The determination of the world spawn is a best effort. Sometimes, the
	 * world spawn cannot be determined, or the world spawn is determined
	 * incorrectly. Consequently, the world spawn should be verified in game.
	 *
	 * @return {@code null}, if the coordinates of the world spawn could not be
	 *         determined
	 */
	static CoordinatesInWorld getSpawnChunksCenter(World world) {
		CoordinatesInWorld spawn = getSpawn(world);
		if (spawn == null) {
			// The world spawn could not be determined.
			return null;
		}
		long spawnX = spawn.getX();
		long spawnChunksCenterX = getSpawnChunksCenterCoordinate(spawnX);
		long spawnY = spawn.getY();
		long spawnChunksCenterY = getSpawnChunksCenterCoordinate(spawnY);
		return CoordinatesInWorld.from(spawnChunksCenterX, spawnChunksCenterY);
	}
	/**
	 * Determines the coordinates for the center of the search area.
	 *
	 * @return {@code null}, if the coordinates of the world spawn could not be
	 *         determined
	 */
	CoordinatesInWorld getSearchCenter(World world) {
		switch (this.mSearchCenterKind) {
		case ORIGIN: {
			return CIW_ORIGIN;
		}
		case MAP_ORIGIN: {
			return CIW_MAP_ORIGIN;
		}
		case SPAWN: {
			return getSpawn(world);
		}
		case SPAWN_CHUNKS: {
			return getSpawnChunksCenter(world);
		}
		default: {
			throw new AssertionError(this.mSearchCenterKind);
		}
		}
	}

	int[] getBiomeCodes(long nwCornerX, long nwCornerY, int width, int height) throws MinecraftInterfaceException {
		return this.mMinecraftInterface.getBiomeData(
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerX)),
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerY)),
				width / 4,
				height / 4,
				true // useQuarterResolution
		);
	}

	/*
	 * OLD SEARCH static final Biome[] SEARCH_BIOMES = { Biome.icePlainsSpikes,
	 * Biome.mushroomIsland, Biome.megaTaiga, Biome.mesa, Biome.savanna,
	 * Biome.warmOcean, Biome.extremeHills, Biome.desert, Biome.jungle,
	 * Biome.roofedForest, Biome.birchForest };
	 */

	/**
	 * Determines whether to accept a world.
	 * 
	 * @throws MinecraftInterfaceCreationException
	 * @throws FormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */

	Biome[] biomes = {};
	Biome[] rejectedBiomes = {};
	StructureSearcher.Type[] structures = {};
	//, Biome.forest, Biome.desert, Biome.birchForest, Biome.plains

	boolean accept(World world) throws MinecraftInterfaceException, UnknownBiomeIndexException, InterruptedException,
			IOException, FormatException, MinecraftInterfaceCreationException {
		CoordinatesInWorld searchCenter = getSearchCenter(world);
		if (searchCenter == null) {
			// The world spawn could not be determined.
			return false;
		}
		long searchCenterX = searchCenter.getX();
		long searchCenterY = searchCenter.getY();
		int[] biomeCodes = getBiomeCodes(
				searchCenterX - this.mSearchQuadrantWidth,
				searchCenterY - this.mSearchQuadrantHeight,
				2 * this.mSearchQuadrantWidth,
				2 * this.mSearchQuadrantHeight);
		int biomeCodesCount = biomeCodes.length;
		
		//System.out.println(biomeCodesCount);
		boolean RejectedBiomes = false;
		if (biomes.length == 0) {
			Util.console("Creating Biomes from list...");
			biomes = GUI.manageCheckedCheckboxes();
			if (rejectedBiomes.length == 0 && GUI.excludeBiome.isSelected()) {
				rejectedBiomes = GUI.manageCheckedCheckboxesRejected();
				RejectedBiomes = true;
			}
		}
		
		if (Main.DEV_MODE) {
			@SuppressWarnings("unused")
			boolean hasStructures = false;
			if (GUI.findStructures.isSelected()) {
				Util.console("Creating Structures from list...");
				structures = GUI.manageCheckedCheckboxesFindStructures();
				
				List<WorldIcon> foundStructures = new ArrayList<WorldIcon>();
				for (Type type : structures) {
					if (type.equals(Type.OCEAN_MONUMENT)) {
						foundStructures.addAll(
								StructureSearcher.findOceanMounments(
										world,
										searchCenterX - this.mSearchQuadrantWidth,
										searchCenterY - this.mSearchQuadrantHeight));
					}
				}
				
				
				if (foundStructures.size() > 0) {
					Util.console("found monument");
					hasStructures = true;
				} else {
					Util.console("no monument :(");
				}
			}	
		}
		
		// Start with a set of all biomes to find.
		Set<Biome> undiscoveredBiomes = new HashSet<>(Arrays.asList(biomes));
		Set<Biome> undiscoveredRejectedBiomes = new HashSet<>(Arrays.asList(rejectedBiomes));
		for (int biomeCodeIndex = 0; biomeCodeIndex < biomeCodesCount; biomeCodeIndex++) {
			if (undiscoveredBiomes.remove(Biome.getByIndex(biomeCodes[biomeCodeIndex]))) {
				// A new biome has been found.
				// Determine whether this was the last biome to find.
			}
			
			// In theory this should return false if the world contains a specific biome
			if(undiscoveredRejectedBiomes.remove(Biome.getByIndex(biomeCodes[biomeCodeIndex]))) {
				//Works except for ocean. No idea why
				return false; // Adding this makes excluded biomes not be resulted anymore. DO NOT REMOVE UNLESS YOU HAVE A FIX FOR THIS
			}
		}
		
		if (undiscoveredBiomes.isEmpty()
				&& (undiscoveredRejectedBiomes.size() != 0 || !RejectedBiomes)) {
	//			&& (GUI.findStructures.isSelected() && hasStructures)) {
			return true;
		}
			return false;

	}

	/**
	 * Updates the progress output for a world that has been rejected.
	 *
	 * @param rejectedWorldsCount the number of worlds that have been rejected
	 *            since the last world was accepted
	 */
	
	

	static void updateRejectedWorldsProgress(int rejectedWorldsCount) {
		GUI.seedCount.setText("Current Rejected Seed Count: " + rejectedWorldsCount);
		GUI.totalSeedCount.setText("Total Rejected Seed Count: " + totalRejectedSeedCount);
		if (rejectedWorldsCount % (1 << 6) == 0) {
			// Print a dot, in order to give a sense of progress.
			// Each dot represents 2^4 worlds that have been rejected.
			Util.consoleNoLine(".");
			if (rejectedWorldsCount % (1 << 16) == 0) {
				// Print a newline, in order to complete a line of dots (so that
				// the line doesn't get too long).
				// Each complete line of dots represents 2^10 (~1000) worlds
				// that have been rejected.
				Util.console("");
			}
		}
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
			Util.console("");
		}
		Util.console(
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
	void search() throws InterruptedException,IOException, FormatException, MinecraftInterfaceCreationException {
		int rejectedWorldsCount = 0;
		int acceptedWorldsCount = 0;
		
		while (acceptedWorldsCount < this.mMaximumMatchingWorldsCount && GUI.running) {
			if (!GUI.paused) {
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
				} catch (MinecraftInterfaceException | UnknownBiomeIndexException e) {
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
				acceptedWorldsCount++;
				updateAcceptedWorldsProgress(rejectedWorldsCount, acceptedWorldsCount, world);
				rejectedWorldsCount = 0;
			}
			
			//Literally without pause doesn't work....
			System.out.print("");
		}
		
		GUI.stop();
		Util.console("Finished Search!");
	}

	/**
	 * Searches for matching worlds, and prints the seed of each matching world
	 * to the standard output stream.
	 */
	@Override
	public void run() {
		//Util.printingSetup();
		try {
			search();
		} catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException e) {
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
	/*
	 * public static void main(String ... args) throws IOException,
	 * FormatException, MinecraftInterfaceCreationException { // Execution
	 * options. // Hard-coded for now, but they could be specified as
	 * command-line arguments. MainGUI gui = new MainGUI(); String
	 * minecraftVersionId = "1.13"; SearchCenterKind searchCenterKind =
	 * SearchCenterKind.ORIGIN; int searchQuadrantWidth = 2048; int
	 * searchQuadrantHeight = 2048; int maximumMatchingWorldsCount = 10; //
	 * Execute. new BiomeSearcher(minecraftVersionId, searchCenterKind,
	 * searchQuadrantWidth, searchQuadrantHeight,
	 * maximumMatchingWorldsCount).run();
	 * 
	 * }
	 */
}

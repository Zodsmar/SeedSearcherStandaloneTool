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
import sassa.gui.Variables;
import sassa.gui.fxmlController;
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
public class BiomeSearcher {
	static Set<Biome> getBiomes(MinecraftInterface minecraftInterface, long nwCornerX, long nwCornerY, int radius) throws MinecraftInterfaceException, UnknownBiomeIndexException {
		Set<Biome> biomes = new HashSet<>();
		int[] biomeCodes = minecraftInterface.getBiomeData(
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerX)),
				(int) (Resolution.QUARTER.convertFromWorldToThis(nwCornerY)),
				radius / 4,
				radius / 4,
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

	public static boolean accept(World world, MinecraftInterface minecraftInterface, CoordinatesInWorld center, int SearchRadius, Biome[] biomes, HashMap<Biome, String> biomeSets, Biome[] rejectedBiomes, HashMap<Biome, String> rejectedBiomeSets) throws MinecraftInterfaceException, InterruptedException,
			IOException, FormatException, MinecraftInterfaceCreationException, ParseException, UnknownBiomeIndexException {
		long searchCenterX = center.getX();
		long searchCenterY = center.getY();

		Set<Biome> undiscoveredBiomes = new HashSet<>(Arrays.asList(biomes));
		Set<Biome> undiscoveredRejectedBiomes = new HashSet<>(Arrays.asList(rejectedBiomes));
		HashMap<Biome, String> undiscoveredBiomeSets = new HashMap<>(biomeSets);
		HashMap<Biome, String> undiscoveredRejectedBiomeSets = new HashMap<>(rejectedBiomeSets);
		// Only search if lists are not empty
		if (!undiscoveredBiomes.isEmpty() || !undiscoveredRejectedBiomes.isEmpty() || !undiscoveredBiomeSets.isEmpty() || !undiscoveredRejectedBiomeSets.isEmpty()) {
			Set<Biome> biomeData = getBiomes(
					minecraftInterface,
					searchCenterX - SearchRadius,
					searchCenterY - SearchRadius,
					2 * SearchRadius);

			for (Biome biome: biomeData) {

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

		if (undiscoveredBiomes.isEmpty() && undiscoveredBiomeSets.isEmpty()) {
			return true;
		}

//		System.out.println(undiscoveredStructures);
//		System.out.println(undiscoveredBiomes);
//		System.out.println(undiscoveredBiomeSets);
		return false;
	}
}

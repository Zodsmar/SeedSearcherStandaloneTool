package sassa.main;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureSearcher {
	
	public boolean structureFound = false;
	
	public enum Type {
		MINESHAFT, OCEAN_RUINS, OCEAN_FEATURES, VILLAGE, STRONGHOLD, MANSION, OCEAN_MONUMENT, SLIME_CHUNK, BIOME_DATA, PILLAGER_OUTPOST, DESERT_TEMPLE, JUNGLE_TEMPLE,
		BURIED_TREASURE, SHIPWRECK, WITCH_HUT, IGLOO
	}

	public static List<WorldIcon> findVillageFeatures(World world, CoordinatesInWorld coords) {
		return world.getVillageProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findPillagerOutpost(World world, CoordinatesInWorld coords){
		List<WorldIcon> villageFeatures = findVillageFeatures(world, coords);
		List<WorldIcon> pillager_outpost = new ArrayList<WorldIcon>();
		for (WorldIcon feature : villageFeatures) {
			if (feature.getName().toUpperCase().equals("PILLAGER OUTPOST")) {
				pillager_outpost.add(feature);
			}
		}
		return pillager_outpost;
	}

	public static List<WorldIcon> findVillage(World world, CoordinatesInWorld coords){
		List<WorldIcon> villageFeatures = findVillageFeatures(world, coords);
		List<WorldIcon> village = new ArrayList<WorldIcon>();
		for (WorldIcon feature : villageFeatures) {
			if (feature.getName().toUpperCase().equals("VILLAGE")) {
				village.add(feature);
			}
		}
		return village;
	}

	public static List<WorldIcon> findStronghold(World world, CoordinatesInWorld coords) {
		return world.getStrongholdProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findMansion(World world, CoordinatesInWorld coords) {
		return world.getWoodlandMansionProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findMineshafts(World world, CoordinatesInWorld coords) {
		return world.getMineshaftProducer().getAt(coords, null);
	}
	
	public static List<WorldIcon> findOceanRuins(World world, CoordinatesInWorld coords) {
		List<WorldIcon> ocean_features = findOceanFeatures(world, coords);
		List<WorldIcon> ocean_ruins = new ArrayList<WorldIcon>();
		for (WorldIcon feature : ocean_features) {
			if (feature.getName().toUpperCase().equals("OCEAN RUINS")) {
				ocean_ruins.add(feature);
			}
		}
		return ocean_ruins;
	}

	public static List<WorldIcon> findBuriedTreasure(World world, CoordinatesInWorld coords) {
		List<WorldIcon> ocean_features = findOceanFeatures(world, coords);
		List<WorldIcon> buried_treasure = new ArrayList<WorldIcon>();
		for (WorldIcon feature : ocean_features) {
			if (feature.getName().toUpperCase().equals("BURIED TREASURE")) {
				buried_treasure.add(feature);
			}
		}
		return buried_treasure;
	}

	public static List<WorldIcon> findShipwreck(World world, CoordinatesInWorld coords) {
		List<WorldIcon> ocean_features = findOceanFeatures(world, coords);
		List<WorldIcon> shipwreck = new ArrayList<WorldIcon>();
		for (WorldIcon feature : ocean_features) {
			if (feature.getName().toUpperCase().equals("SHIPWRECK")) {
				shipwreck.add(feature);
			}
		}
		return shipwreck;
	}
	
	public static List<WorldIcon> findOceanFeatures(World world, CoordinatesInWorld coords) {
		return world.getOceanFeaturesProducer().getAt(coords, null);
	}
	
	public static List<WorldIcon> findOceanMounments(World world, CoordinatesInWorld coords) {
		return world.getOceanMonumentProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findTempleFeatures(World world, CoordinatesInWorld coords) {
		return world.getTempleProducer().getAt(coords, null);
	}
	public static List<WorldIcon> findDesertTemple(World world, CoordinatesInWorld coords) {
		List<WorldIcon> templeFeatures = findTempleFeatures(world, coords);
		List<WorldIcon> desert_temple = new ArrayList<WorldIcon>();
		for (WorldIcon feature : templeFeatures) {
			if (feature.getName().toUpperCase().equals("DESERT TEMPLE")) {
				desert_temple.add(feature);
			}
		}
		return desert_temple;
	}

	public static List<WorldIcon> findJungleTemple(World world, CoordinatesInWorld coords) {
		List<WorldIcon> templeFeatures = findTempleFeatures(world, coords);
		List<WorldIcon> jungle_temple = new ArrayList<WorldIcon>();
		for (WorldIcon feature : templeFeatures) {
			if (feature.getName().toUpperCase().equals("JUNGLE TEMPLE")) {
				jungle_temple.add(feature);
			}
		}
		return jungle_temple;
	}

	public static List<WorldIcon> findWitchHut(World world, CoordinatesInWorld coords) {
		List<WorldIcon> templeFeatures = findTempleFeatures(world, coords);
		List<WorldIcon> witch_hut = new ArrayList<WorldIcon>();
		for (WorldIcon feature : templeFeatures) {
			if (feature.getName().toUpperCase().equals("WITCH HUT")) {
				witch_hut.add(feature);
			}
		}
		return witch_hut;
	}

	public static List<WorldIcon> findIgloo(World world, CoordinatesInWorld coords) {
		List<WorldIcon> templeFeatures = findTempleFeatures(world, coords);
		List<WorldIcon> igloo = new ArrayList<WorldIcon>();
		for (WorldIcon feature : templeFeatures) {
			if (feature.getName().toUpperCase().equals("IGLOO")) {
				igloo.add(feature);
			}
		}
		return igloo;
	}
	
	private static CoordinatesInWorld coords(long nwCornerX, long nwCornerY) {
		return CoordinatesInWorld.from(nwCornerX, nwCornerY);
	}

	public static List<Type> hasStructures(Set<Type> structures, World world, long nwCornerX, long nwCornerY, int distX, int distY) {
        List<Type> foundStructures = new ArrayList();
        int multiplierX = 0;
        int multiplierY = 0;
		while ((nwCornerX + (multiplierX * 512)) < (nwCornerX + distX) && !structures.isEmpty()) {
			while ((nwCornerY + (multiplierY * 512)) < (nwCornerY + distY) && !structures.isEmpty()) {
				CoordinatesInWorld coords = coords(nwCornerX + (multiplierX * 512), nwCornerY + (multiplierY * 512));
				System.out.println("Searching coords: " + coords);
				for (Type type : structures) {
					if (type.equals(Type.MINESHAFT)) {
						List<WorldIcon> mineshafts = StructureSearcher.findMineshafts(
								world,
								coords);
						if (mineshafts.size() >= 1 && (nwCornerX + distX) > mineshafts.get(0).getCoordinates().getX() && (nwCornerY + distY) > mineshafts.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.OCEAN_RUINS)) {
						List<WorldIcon> ocean_ruins = StructureSearcher.findOceanRuins(
								world,
								coords);
						if (ocean_ruins.size() >= 1 && (nwCornerX + distX) > ocean_ruins.get(0).getCoordinates().getX() && (nwCornerY + distY) > ocean_ruins.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.OCEAN_FEATURES)) {
						List<WorldIcon> ocean_features = StructureSearcher.findOceanFeatures(
								world,
								coords);
						if (ocean_features.size() >= 1 && (nwCornerX + distX) > ocean_features.get(0).getCoordinates().getX() && (nwCornerY + distY) > ocean_features.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.OCEAN_MONUMENT)) {
						List<WorldIcon> ocean_monuments = StructureSearcher.findOceanMounments(
								world,
								coords);
						if (ocean_monuments.size() >= 1 && (nwCornerX + distX) > ocean_monuments.get(0).getCoordinates().getX() && (nwCornerY + distY) > ocean_monuments.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.SHIPWRECK)) {
						List<WorldIcon> shipwreck = StructureSearcher.findShipwreck(
								world,
								coords);
						if (shipwreck.size() >= 1 && (nwCornerX + distX) > shipwreck.get(0).getCoordinates().getX() && (nwCornerY + distY) > shipwreck.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.BURIED_TREASURE)) {
						List<WorldIcon> buriedTreasure = StructureSearcher.findBuriedTreasure(
								world,
								coords);
						if (buriedTreasure.size() >= 1 && (nwCornerX + distX) > buriedTreasure.get(0).getCoordinates().getX() && (nwCornerY + distY) > buriedTreasure.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.MANSION)) {
						List<WorldIcon> mansion = StructureSearcher.findMansion(
								world,
								coords);
						if (mansion.size() >= 1 && (nwCornerX + distX) > mansion.get(0).getCoordinates().getX() && (nwCornerY + distY) > mansion.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.STRONGHOLD)) {
						List<WorldIcon> stronghold = StructureSearcher.findStronghold(
								world,
								coords);
						if (stronghold.size() >= 1 && (nwCornerX + distX) > stronghold.get(0).getCoordinates().getX() && (nwCornerY + distY) > stronghold.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.VILLAGE)) {
						List<WorldIcon> village = StructureSearcher.findVillage(
								world,
								coords);
						if (village.size() >= 1 && (nwCornerX + distX) > village.get(0).getCoordinates().getX() && (nwCornerY + distY) > village.get(0).getCoordinates().getY()) {
							foundStructures.add(type);;
						}
					} else if (type.equals(Type.PILLAGER_OUTPOST)) {
						List<WorldIcon> pillagerOutpost = StructureSearcher.findPillagerOutpost(
								world,
								coords);
						if (pillagerOutpost.size() >= 1 && (nwCornerX + distX) > pillagerOutpost.get(0).getCoordinates().getX() && (nwCornerY + distY) > pillagerOutpost.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.DESERT_TEMPLE)) {
						List<WorldIcon> deserttemple = StructureSearcher.findDesertTemple(
								world,
								coords);
						if (deserttemple.size() >= 1 && (nwCornerX + distX) > deserttemple.get(0).getCoordinates().getX() && (nwCornerY + distY) > deserttemple.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.JUNGLE_TEMPLE)) {
						List<WorldIcon> jungleTemple = StructureSearcher.findJungleTemple(
								world,
								coords);
						if (jungleTemple.size() >= 1 && (nwCornerX + distX) > jungleTemple.get(0).getCoordinates().getX() && (nwCornerY + distY) > jungleTemple.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.WITCH_HUT)) {
						List<WorldIcon> witchhut = StructureSearcher.findWitchHut(
								world,
								coords);
						if (witchhut.size() >= 1 && (nwCornerX + distX) > witchhut.get(0).getCoordinates().getX() && (nwCornerY + distY) > witchhut.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					} else if (type.equals(Type.IGLOO)) {
						List<WorldIcon> igloo = StructureSearcher.findIgloo(
								world,
								coords);
						if (igloo.size() >= 1 && (nwCornerX + distX) > igloo.get(0).getCoordinates().getX() && (nwCornerY + distY) > igloo.get(0).getCoordinates().getY()) {
							foundStructures.add(type);
						}
					}
				}
				// Remove any structures we have already found, reduce unneeded lookups
				structures.removeAll(foundStructures);
				multiplierY++;
			}
			multiplierY=0;
			multiplierX++;
		}

		return foundStructures;
	}
	
}

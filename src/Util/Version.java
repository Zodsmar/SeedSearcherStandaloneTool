package Util;

import java.util.HashMap;
import java.util.Map;

import gui.GUI;

public class Version {
	
	public static final String V1_13_2 = "1.13.2";
	public static final String V1_14_2 = "1.14.2";
	
	private static Map<String, Integer> versions = new HashMap<String, Integer>();
	
	public static void registerSupportedVersions() {
		versions.put("1.13.2", 132);
		versions.put("1.14.2", 142);
	}
	
	public static boolean isOrGreaterThanSelectedVersion(String version) {
		int selected = versions.get(GUI.minecraftVersion);
		int wanted = versions.get(version);
		if (wanted >= selected) return true;
		else return false;
	}
	
	public static boolean isOrGreaterThanSelectedVersion(int version) {
		int selected = versions.get(GUI.minecraftVersion);
		if (version >= selected) return true;
		else return false;
	}
	
}

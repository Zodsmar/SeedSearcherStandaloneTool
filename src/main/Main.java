package main;

import java.io.IOException;

import Util.Version;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import gui.GUI;

public class Main {
	
	public static final int BACK_FRAME_WIDTH = 924;
	public static final int BACK_FRAME_HEIGHT = 515;
	
	public static final int CONSOLE_WIDTH = 240;
	public static final int CONSOLE_HEIGHT = BACK_FRAME_HEIGHT - 100;
	
	public static final int FRAME_WITHOUT_CONSOLE_WIDTH = BACK_FRAME_WIDTH - CONSOLE_WIDTH;
	public static final int FRAME_WITHOUT_CONSOLE_HEIGHT = BACK_FRAME_HEIGHT - CONSOLE_HEIGHT;
	
	public static final int FRAME_SCROLL_BAR_WIDTH = (BACK_FRAME_WIDTH - CONSOLE_WIDTH) - 18;
	public static final int FRAME_SCROLL_BAR_HEIGHT = BACK_FRAME_HEIGHT - 93;
	
	public static final String VERSION = "v0.3.1";
	public static boolean DEV_MODE = false;
	/*
	 Quick test of git connection through VS Code

	 Dev mode currently gives access to:
	 	- Structures
	 	- New box selection for included/excluded biomes
	 */
	
	public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException {
		Version.registerSupportedVersions();
		new GUI().startSeedSearcher();
	}
	
}

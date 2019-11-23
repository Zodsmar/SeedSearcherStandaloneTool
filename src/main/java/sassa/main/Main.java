package sassa.main;

import sassa.util.Version;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import sassa.gui.GUI;

import java.awt.*;
import java.io.IOException;

public class Main {


//	public static final int BACK_FRAME_WIDTH = 924;
//	public static final int BACK_FRAME_HEIGHT = 515;
	
	public static final int BACK_FRAME_WIDTH = Math.max(924, (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.9));
	public static final int BACK_FRAME_HEIGHT = Math.max(515, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.8));
	
	public static final int CONSOLE_WIDTH = Math.max((int) (BACK_FRAME_WIDTH * 0.3), 240);
	public static final int CONSOLE_HEIGHT = (int) (BACK_FRAME_HEIGHT * 0.8);
	
	public static final int FRAME_WITHOUT_CONSOLE_WIDTH = BACK_FRAME_WIDTH - CONSOLE_WIDTH;
	public static final int FRAME_WITHOUT_CONSOLE_HEIGHT = BACK_FRAME_HEIGHT - CONSOLE_HEIGHT;
	
	public static final int FRAME_SCROLL_BAR_WIDTH = (BACK_FRAME_WIDTH - CONSOLE_WIDTH) - 18;
	public static final int FRAME_SCROLL_BAR_HEIGHT = BACK_FRAME_HEIGHT - 93;
	
	public static final String VERSION = "v0.4.1";
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

package sassa.main;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

	/* THIS IS THE ACTUAL MAIN CLASS
	Since javaFX is really weird I cannot have the Application in the main file so there is a second main which actually starts
	everything. This one is simply to connect them for building purposes.

	TODO: Better comments so people can understand will do that for v0.6.0
	*/

	public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException, ParseException {
		mainApp.main(args);
	}

}

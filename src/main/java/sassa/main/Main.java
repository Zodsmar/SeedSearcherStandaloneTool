package sassa.main;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

	/* THIS IS THE ACTUAL MAIN CLASS
	Since javaFX is really weird I cannot have the Application in the main file so there is a second main which actually starts
	everything. This one is simply to connect them for building purposes.

	TODO: Better comments so people can understand will do that for v0.7.0
	*/

    public static void main(String... args) throws IOException, ParseException {
        mainApp.main(args);
    }

}

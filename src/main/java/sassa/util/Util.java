package sassa.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sassa.gui.GUI;

public class Util {
	/**
	 * elapsed time in hours/minutes/seconds
	 * 
	 * @return String
	 */
	public static String getElapsedTimeHoursMinutesFromMilliseconds(long milliseconds) {
		String format = String.format("%%0%dd", 2);
		long elapsedTime = milliseconds / 1000;
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, (elapsedTime % 3600) / 60);
		String hours = String.format(format, elapsedTime / 3600);
		String time = hours + ":" + minutes + ":" + seconds;
		return time;
	}
/* Might use again later
	public static void printingSetup() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};

		class JTextFieldPrintStream extends PrintStream {
			public JTextFieldPrintStream(OutputStream out) {
				super(out);
			}

			@Override
			public void println(String x) {
				GUI.console.append(x + "\n");
				// seedCount.setText("Seed Count: " +
				// BiomeSearcher.getRejectedSeedCount());
			}
		}

		JTextFieldPrintStream print = new JTextFieldPrintStream(out);
		System.setOut(print);

	}
*/
	public static void console(String output) {

		StringSelection stringSelection = new StringSelection(output);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		GUI.console.append(output + "\n");
	}
	public static void consoleNoLine(String output) {
		GUI.console.append(output);
	}
	
	public static void consoleWipe() {
		GUI.console.setText("");
	}
	
	public static void setFontSize(JLabel hotBiomesTxt, int size) {
		Font font = hotBiomesTxt.getFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
		attributes.put(TextAttribute.SIZE, size);
		hotBiomesTxt.setFont(font.deriveFont(attributes));
	}
	
	public static void Underline(JLabel hotBiomesTxt) {
		Font font = hotBiomesTxt.getFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		hotBiomesTxt.setFont(font.deriveFont(attributes));
	}

	/**
	 * pass a url to open a web browser with that link
	 * @param url
	 */
	public static void openWebPage(String url){
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		catch (java.io.IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Grabs JSON file and puts it into a JSONObject
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject jsonParser(String fileName) throws IOException, ParseException {

		// TODO: Deep dive into parsing and figure out best way to parse everything and setup new GUI with JSON structures
		String file = "sassa/json/" + fileName;
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object obj = new JSONParser().parse(new FileReader(classLoader.getResource(file).getFile()));
		JSONObject jo = (JSONObject) obj;

		return jo;
	}

	public void generateBiomes() throws IOException, ParseException {

		String minecraftVersion = GUI.minecraftVersion;

		JSONObject jo = jsonParser("searchables.json");
		JSONObject biomes = (JSONObject) jo.get("Biomes");
		JSONObject any_v = (JSONObject) biomes.get("ANY_V");
		JSONArray hot_biomes = (JSONArray) any_v.get("Hot Biomes");
		System.out.println(hot_biomes);
		Map<String, Integer> versions = Version.getVersions();
		System.out.println(versions.get(minecraftVersion));
	}
}

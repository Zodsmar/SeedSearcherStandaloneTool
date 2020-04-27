package sassa.util;

import amidst.mojangapi.world.WorldType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Util {

	static TextArea console = Singleton.getInstance().getConsole();
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

//		StringSelection stringSelection = new StringSelection(output);
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		clipboard.setContents(stringSelection, null);
		console.appendText(output + "\n");

		if(Singleton.getInstance().getAutoSave().isSelected()){
            appendToFile(Singleton.getInstance().getOutputFile(), output);
        }
	}
	public void consoleNoLine(String output) {
		console.appendText(output);
	}
	
	public static void consoleWipe() {
		console.setText("");
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
		ClassLoader classLoader = getClass().getClassLoader();

		InputStream input = classLoader.getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		Object obj = new JSONParser().parse(reader);
		JSONObject jo = (JSONObject) obj;

		return jo;
	}

	/* Removing support for 1.6.4 because its a pain. If people really want it I can look into in the future
	"PREV1_6_4": {
      "Cold Biomes": [
        "Extreme Hills Edge"
      ]
    }
	 */


	public Object generateSearchLists(JSONObject obj, String searchName)  {
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, String> hashList = new HashMap<String, String>();
		String minecraftVersion = Singleton.getInstance().getMinecraftVersion();
		Map<String, Integer> versions = Version.getVersions();
		for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			//System.out.println(Integer.valueOf(versions.get(minecraftVersion)));
			if(Integer.valueOf(key) <= Integer.valueOf(versions.get(minecraftVersion))){
				JSONObject subGroup = (JSONObject) obj.get(key);
				for(Iterator iterator1 = subGroup.keySet().iterator(); iterator1.hasNext();){
					String key1 = (String) iterator1.next();
					JSONArray subGArray = (JSONArray)subGroup.get(key1);
					if(searchName == "Biome Sets"){
						list.add(key1);
					} else {
						for(int i = 0; i < subGArray.size(); i++){
                            if(searchName == "getBiomeSets") {
                                hashList.put((String)subGArray.get(i), key1);

                            } else {
                                list.add((String)subGArray.get(i));
                            }
						}
					}
				}
			}
		}
        if(searchName == "getBiomeSets") {
            return hashList;
        } else {
            return list;
        }
	}

	public Object createSearchLists(String searchName) throws IOException, ParseException{

		JSONObject jo = jsonParser("searchables.json");

		ArrayList<String> jObjList;
		HashMap<String, String> jObjMap;


        if(searchName == "getBiomeSets"){
            JSONObject jObj = (JSONObject) jo.get("Biome Sets");
            jObjMap = (HashMap) generateSearchLists(jObj, searchName);
            return jObjMap;
        } else {
            JSONObject jObj = (JSONObject) jo.get(searchName);
            jObjList = (ArrayList) generateSearchLists(jObj, searchName);
            return jObjList;
        }

	}

	public void chooseDirectory(Label display){
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
			display.setText(file.getName());
			Singleton.getInstance().setOutputFile(file);
		}
    }

    private static File createDefaultOutputFile(){
        File outputFile = new File("sassa_output.txt");
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Singleton.getInstance().setOutputFile(outputFile);
        return outputFile;
    }

    public static void appendToFile(File file, String text){
		System.out.println(file);
        FileWriter fr = null;
        try {
            // Below constructor argument decides whether to append or override
            if(file == null){
                file = createDefaultOutputFile();
            }
            fr = new FileWriter(file, true);
            fr.write(text + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public WorldType getWorldType(String worldTypeString){
		if(worldTypeString == "AMPLIFIED"){
			return WorldType.AMPLIFIED;
		} else if ( worldTypeString == "LARGE BIOMES"){
			return WorldType.LARGE_BIOMES;
		} else {
			return WorldType.DEFAULT;
		}
	}
}

package sassa.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

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
	
}

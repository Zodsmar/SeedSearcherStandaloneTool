package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import Util.Util;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import gui.GUI;

public class Main {
	
	private static final int DELAY = 0;
	static Timer timer;
	public static boolean running;
	public static boolean paused;
	private static long startTime;
	private static long elapsedTime = 0L;
	
	
	public static int searchQuadrantWidth = 2048;
	public static int searchQuadrantHeight = 2048;
	public static int maximumMatchingWorldsCount = 10;
	public static String minecraftVersionId = "1.13.2";
	GUI gui;
	static Thread t;
	static BiomeSearcher r;

	public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException {
		new Main().startSeedSearcher();

	}

	static BiomeSearcher createNewThread() throws IOException, FormatException, MinecraftInterfaceCreationException {
		BiomeSearcher.SearchCenterKind searchCenterKind = BiomeSearcher.SearchCenterKind.ORIGIN;


		r = new BiomeSearcher(
				GUI.versionId.getText(),
				searchCenterKind,
				Integer.parseInt(GUI.widthSearch.getText()),
				Integer.parseInt(GUI.heightSearch.getText()),
				Integer.parseInt(GUI.maxSeeds.getText()));
		// t = new Thread(r);
		return r;
	}

	void startSeedSearcher() throws IOException, FormatException, MinecraftInterfaceCreationException {
		GUI gui = new GUI();
		//gui.initialize();
		initTimer();
		Util.console("Please select Biomes first!");
		// Execute.
	}

	private void initTimer() {
		Action updateLabelAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDisplay();
			}
		};
		timer = new Timer(DELAY, updateLabelAction);
	}

	private static void updateDisplay() {
		if (!paused) {
			// String text = String.format("%02d:%02d:%02d:%02d",
			// this.hours, this.minutes, this.seconds, this.hundredths);
			// this.timeLabel.setText(text);

			GUI.timeElapsed.setText(
					"Time Elapsed: " + Util.getElapsedTimeHoursMinutesFromMilliseconds(
							elapsedTime = System.currentTimeMillis() - startTime));

		}
	}

	public static void toggleRunning()
			throws InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		if (running) {
			System.out.println("Shutting Down...");
			stop();
		} else {
			start();
		}

	}

	public static void start() throws IOException, FormatException, MinecraftInterfaceCreationException {
		t = new Thread(createNewThread());
		startTime = System.currentTimeMillis();
		running = true;
		t.start();
		timer.restart();
		GUI.btnStart.setText("Stop");
		BiomeSearcher.totalRejectedSeedCount = 0;

	}

	public static void stop()
			throws InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		GUI.btnStart.setText("Start");
		running = false;
		timer.stop();
		t.interrupt();
		t.join(1000);
		t = new Thread(createNewThread());
	}

	public static void togglePause() {
		paused = !paused;
		String text = (paused) ? "Unfreeze" : "Freeze";
		 long timeAtPause = 0;
		 
		if(paused) {
			timer.stop();
			timeAtPause = System.currentTimeMillis();
		} else {
			timer.start();
			
			//startTime = timeAtPause;
		}
		GUI.btnPause.setText(text);
		updateDisplay();
	}

	public static void reset()
			throws InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		if (paused) {
			togglePause();
		}
		if (running) {
			toggleRunning();
		}
		Util.consoleWipe();
		GUI.timeElapsed.setText("Time Elapsed: 00:00:00");
		startTime = System.currentTimeMillis();
		GUI.seedCount.setText("Rejected Seed Count: 0");
		GUI.totalSeedCount.setText("Total Rejected Seed Count: 0");
		BiomeSearcher.totalRejectedSeedCount = 0;

		updateDisplay();
	}

}

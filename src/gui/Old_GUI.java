package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import Util.Util;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import main.BiomeSearcher;
import main.BiomeSearcher.SearchCenterKind;

/**
 * A GUI to display info about seed searching
 *
 * @author Zodsmar
 */

public class Old_GUI {
	/*
	private JFrame frame = new JFrame();
	JButton reset;
	JButton start;
	JButton pause;

	ButtonListener listener = new ButtonListener();

	public static JLabel seedCount;
	public static JLabel timeElapsed;
	public static JTextArea output;

	private static final int WINDOW_WIDTH = 620;
	private static final int WINDOW_HEIGHT = 400;

	public void MainGUI() {

		reset = new JButton("Clear");
		reset.setPreferredSize(new Dimension(100, 50));
		reset.addActionListener(listener);

		start = new JButton("Start");
		start.setPreferredSize(new Dimension(100, 50));
		start.addActionListener(listener);

		pause = new JButton("Pause");
		pause.setPreferredSize(new Dimension(100, 50));
		pause.addActionListener(listener);

		seedCount = new JLabel("Rejected Seed Count: " + 0);
		timeElapsed = new JLabel("Time Elapsed: 00:00:00");

		JPanel panel = new JPanel();
		JPanel sidePanel = new JPanel();

		sidePanel.setLayout(new GridLayout(10, 0));
		sidePanel.setBorder(BorderFactory.createBevelBorder(0, Color.blue, Color.green));
		sidePanel.setPreferredSize(new Dimension((WINDOW_WIDTH - 20) / 3, WINDOW_HEIGHT));

		// panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		panel.setLayout(new GridLayout(1, 0));
		panel.setPreferredSize(new Dimension(((WINDOW_WIDTH - 20) / 3) * 2, WINDOW_HEIGHT));

		output = new JTextArea();
		JScrollPane console = new JScrollPane(output);
		output.setBorder(BorderFactory.createBevelBorder(0, Color.orange, Color.black));

		Dimension windowSize = new Dimension();
		windowSize.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		panel.add(console);
		sidePanel.add(reset);
		sidePanel.add(start);
		sidePanel.add(pause);
		sidePanel.add(seedCount);
		sidePanel.add(timeElapsed);

		frame.add(panel, BorderLayout.WEST);
		frame.add(sidePanel, BorderLayout.EAST);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Seed Searcher");
		frame.pack();

		frame.setSize(windowSize);
		frame.setVisible(true);
	}

	/*
	 * public void actionPerformed(ActionEvent e) { this.output.setText(""); }
	 */
	/*
	private static final int DELAY = 1;
	private Timer timer;
	public static boolean running;
	public static boolean paused;
	private long startTime;
	private long elapsedTime = 0L;

	private void initTimer() {
		Action updateLabelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updateDisplay();
			}
		};
		this.timer = new Timer(DELAY, updateLabelAction);
	}

	private void updateDisplay() {
		if (!paused) {
			// String text = String.format("%02d:%02d:%02d:%02d",
			// this.hours, this.minutes, this.seconds, this.hundredths);
			// this.timeLabel.setText(text);

			this.timeElapsed.setText(
					"Time Elapsed: " + Util.getElapsedTimeHoursMinutesFromMilliseconds(
							elapsedTime = System.currentTimeMillis() - startTime));
		}
	}

	private void toggleRunning()
			throws InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		if (this.running) {
			stop();
		} else {
			start();
		}

	}

	private void start() {
		startTime = System.currentTimeMillis();
		this.running = true;
		t.start();
		this.timer.restart();
		this.start.setText("Stop");
	}

	private void stop() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException {
		this.running = false;
		this.timer.stop();
		t.interrupt();
		t.join(1000);
		t = new Thread(createNewThread());
		this.start.setText("Start");
	}

	private void togglePause() {
		this.paused = !paused;
		String text = (paused) ? "Unfreeze" : "Freeze";
		//timer = (paused) ? timer.start() : timer.stop();
		this.pause.setText(text);
		updateDisplay();
	}

	private void reset()
			throws InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		if (this.paused)
			togglePause();
		if (this.running)
			toggleRunning();

		this.output.setText("");
		this.timeElapsed.setText("Time Elapsed: 00:00:00");
		startTime = System.currentTimeMillis();
		this.seedCount.setText("Rejected Seed Count: 0");

		updateDisplay();
	}
/*
	public static void main(String... args) throws IOException, FormatException, MinecraftInterfaceCreationException {
		new MainGUI().startSeedSearcher();

	}
*/
	/*
	BiomeSearcher createNewThread() throws IOException, FormatException, MinecraftInterfaceCreationException {
		String minecraftVersionId = "1.13";
		BiomeSearcher.SearchCenterKind searchCenterKind = BiomeSearcher.SearchCenterKind.ORIGIN;
		int searchQuadrantWidth = 2048;
		int searchQuadrantHeight = 2048;
		int maximumMatchingWorldsCount = 10;
		r = new BiomeSearcher(
				minecraftVersionId,
				searchCenterKind,
				searchQuadrantWidth,
				searchQuadrantHeight,
				maximumMatchingWorldsCount);
		// t = new Thread(r);
		
		return r;
	}

	Thread t;
	BiomeSearcher r;

	void startSeedSearcher() throws IOException, FormatException, MinecraftInterfaceCreationException {
		initTimer();
		MainGUI();
		t = new Thread(createNewThread());
		// Execute.
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == start) {
				try {
					toggleRunning();
				} catch (
						InterruptedException
						| IOException
						| FormatException
						| MinecraftInterfaceCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else if (e.getSource() == pause) {
				 togglePause();
			} else if (e.getSource() == reset) {

				try {
					reset();
				} catch (
						InterruptedException
						| IOException
						| FormatException
						| MinecraftInterfaceCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}
*/
}

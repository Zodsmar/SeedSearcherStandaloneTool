package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import Util.Util;
import Util.Version;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.parsing.FormatException;
import main.BiomeSearcher;
import main.Main;
import main.StructureSearcher;
import main.StructureSearcher.Type;
import java.awt.Font;
import javax.swing.JTextPane;

import gui.DevConsole;

public class GUI {
	
	private JFrame frmSeedTool = new JFrame();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * Launch the application.
	 */

	private static final int DELAY = 0;
	static Timer timer;
	public static boolean running;
	public static boolean paused;
	private static long pausedTime;
	@SuppressWarnings("unused")
	private static long startTime; // TODO use this in the future to tell user when they started
	private static long elapsedTime;
	
	static JButton btnClear;
	static JButton btnStart;
	static JButton btnPause;
	static JCheckBox chkboxDevMode;
	static JButton btnDevCon;
	
	public static JCheckBox findStructures;
	
	public static JPanel biomesPanel;
	
	public static JPanel structures;

	public static String[] biomeSelected;

	ButtonListener listener = new ButtonListener();

	public static JLabel seedCount;
	public static JLabel totalSeedCount;
	public static JLabel timeElapsed;
	public static JTextArea console;

	static Thread t;
	static boolean allowThreadToSearch = true;
	static BiomeSearcher r;
	private static JTextField widthSearch;
	private static JTextField heightSearch;
	private static JTextField maxSeeds;
	
	
	private static int searchQuadrantWidth = 512;
	private static int searchQuadrantHeight = 512;
	private static int maximumMatchingWorldsCount = 10;
	public static String minecraftVersion = Version.V1_14_3;
	String[] versions = {
		/*1.14.x*/	Version.V1_14_3, Version.V1_14,
		/*1.13.x*/	Version.V1_13_2, Version.V1_13_1, Version.V1_13,
		/*1.12.x*/	Version.V1_12_2, Version.V1_12,
		/*1.11.x*/	Version.V1_11_2, Version.V1_11,
		/*1.10.x*/	Version.V1_10_2,
		/*1.9.x*/	Version.V1_9_4, Version.V1_9_2,
		/*1.8.x*/	Version.V1_8_9, Version.V1_8_3, Version.V1_8_1, Version.V1_8,
		/*1.7.x*/	Version.V1_7_10,
		/*1.6.x*/	Version.V1_6_4};
	
	DefaultComboBoxModel<String> versionModel = new DefaultComboBoxModel<String>(versions);
	JComboBox<String> versionBox = new JComboBox<String>(versionModel);
	
	String[] include_exclude_txt = {"", "Include", "Exclude"};
	//DefaultComboBoxModel<String> include_exclude_txt = new DefaultComboBoxModel<String>(include_exclude_txt_1);

	static BiomeSearcher createNewThread() throws IOException, FormatException, MinecraftInterfaceCreationException {
		r = new BiomeSearcher(
				minecraftVersion,
				Integer.parseInt(widthSearch.getText()),
				Integer.parseInt(heightSearch.getText()),
				Integer.parseInt(maxSeeds.getText()));
		// t = new Thread(r);
		return r;
	}

	public void startSeedSearcher() throws IOException, FormatException, MinecraftInterfaceCreationException {
		initTimer();
		Util.console("Welcome to SeedTool!");
		Util.console("Please select at least one biome before searching!");
	}

	private void initTimer() {
		Action updateLabelAction = new AbstractAction() {
			private static final long serialVersionUID = 3920770968451095353L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDisplay();
			}
		};
		timer = new Timer(DELAY, updateLabelAction);
	}
	
	private static void updateDisplay() {
		if (!paused) {
			timeElapsed.setText("Time Elapsed: " + Util.getElapsedTimeHoursMinutesFromMilliseconds(System.currentTimeMillis() - elapsedTime));
		}
	}
	
	private static void toggleRunning() throws InterruptedException, IOException, FormatException,
				MinecraftInterfaceCreationException, UnknownBiomeIndexException {
		allowThreadToSearch = true;
		if (running) {
			System.out.println("Shutting Down...");
			stop();
		} else {
			manageCheckedCheckboxes();
			if (allowThreadToSearch) {
				start();
			} else {
				stop();
			}
		}

	}

	private static void start() throws IOException, FormatException, MinecraftInterfaceCreationException {
		btnStart.setText("Stop");
		widthSearch.setEditable(false);
		heightSearch.setEditable(false);
		maxSeeds.setEditable(false);
		startTime = System.currentTimeMillis();
		elapsedTime = System.currentTimeMillis();
		running = true;
		timer.restart();
		BiomeSearcher.totalRejectedSeedCount = 0;
		t = new Thread(createNewThread());
		t.start();
	}

	public static void stop() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException {
		widthSearch.setEditable(true);
		heightSearch.setEditable(true);
		maxSeeds.setEditable(true);
		btnStart.setText("Start");
		btnPause.setText("Pause");
		running = false;
		timer.stop();
		if (t != null) t.interrupt();
	}
	
	private static void togglePause() {
		if (!running) {
			Util.console("Cannot pause when you aren't running!");
		} else {
			paused = !paused;
			String text = (paused) ? "Click To Unpause" : "Click to Pause";
			 
			if (paused) {
				pausedTime = System.currentTimeMillis();
				timer.stop();
			} else {
				elapsedTime += System.currentTimeMillis() - pausedTime;
				timer.start();
				
				//startTime = timeAtPause;
			}
			btnPause.setText(text);
			updateDisplay();
		}
	}

	private static void reset() throws InterruptedException, IOException, FormatException,
			MinecraftInterfaceCreationException, UnknownBiomeIndexException {
		if (paused) {
			togglePause();
		}
		stop();
		Util.consoleWipe();
		timeElapsed.setText("Time Elapsed: 00:00:00");
		startTime = System.currentTimeMillis();
		pausedTime = 0;
		elapsedTime = System.currentTimeMillis();
		seedCount.setText("Rejected Seed Count: 0");
		totalSeedCount.setText("Total Rejected Seed Count: 0");
		BiomeSearcher.totalRejectedSeedCount = 0;

		updateDisplay();
	}

	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (Main.DEV_MODE) {
				if(e.getSource() == findStructures) {
					if (findStructures.isSelected()) {
						tabbedPane.setEnabledAt(3, true);
					} else {
						tabbedPane.setEnabledAt(3, false);
					}
				}
			}
			
			if (e.getSource() == chkboxDevMode) {
				Main.DEV_MODE = !Main.DEV_MODE;
				initialize();
			} else if (e.getSource() == btnStart) {
				try {
					toggleRunning();
				} catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
						UnknownBiomeIndexException e1) {
					e1.printStackTrace();
				}
			} else if (e.getSource() == btnPause) {
				togglePause();
			} else if (e.getSource() == btnClear) {
				try {
					reset();
				} catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
						UnknownBiomeIndexException e1) {
					e1.printStackTrace();
				}
			} else if (e.getSource() == versionBox) {
				JComboBox<String> combo = versionBox;
				String selected = (String) combo.getSelectedItem();
				minecraftVersion = selected;
				System.out.println("Version: "+minecraftVersion+":"+versionBox.getSelectedIndex());
				versionBox.setSelectedIndex(versionBox.getSelectedIndex());
				initialize();
			} else if (e.getSource() == btnDevCon) {
				DevConsole.showDevConsole();
			}
		}
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		versionBox.addActionListener(listener);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		if (frmSeedTool != null) frmSeedTool.getContentPane().removeAll();
		frmSeedTool.setTitle("Seed Tool "+Main.VERSION);
		frmSeedTool.setResizable(false);
		frmSeedTool.setBounds(100, 100, Main.BACK_FRAME_WIDTH, Main.BACK_FRAME_HEIGHT);
		frmSeedTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSeedTool.setVisible(true);
		frmSeedTool.getContentPane().setLayout(null);
		
		JPanel panel_console = new JPanel();
		panel_console.setBounds(0, 0, Main.CONSOLE_WIDTH, 25);
		frmSeedTool.getContentPane().add(panel_console);
		panel_console.setLayout(new BorderLayout(0, 0));
		
		JLabel consoleTxt = new JLabel("Console Output");
		consoleTxt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_console.add(consoleTxt, BorderLayout.CENTER);
		
		JScrollPane consoleScrollBar = new JScrollPane();
		consoleScrollBar.setBounds(0, 25, Main.CONSOLE_WIDTH, Main.CONSOLE_HEIGHT);
		frmSeedTool.getContentPane().add(consoleScrollBar);
		
		console = new JTextArea();
		console.setLineWrap(true);
		console.setEditable(false);
		consoleScrollBar.setViewportView(console);
		
		tabbedPane.removeAll();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(Main.CONSOLE_WIDTH, 0, Main.FRAME_WITHOUT_CONSOLE_WIDTH-14, Main.BACK_FRAME_HEIGHT-36);
		frmSeedTool.getContentPane().add(tabbedPane);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Info", null, panel_3, null);
		panel_3.setLayout(null);
		
		JLabel lblSeedTool = new JLabel("Welcome To Seed Tool");
		lblSeedTool.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSeedTool.setBounds(226, 11, 203, 16);
		panel_3.add(lblSeedTool);
		
		JLabel lblNewLabel = new JLabel("1. To get started please select a biome(s)");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setBounds(34, 64, 400, 14);
		panel_3.add(lblNewLabel);
		
		JLabel lblSetThe = new JLabel("2. Set the dimensions size to search (N/S, and E/W)");
		lblSetThe.setBounds(34, 86, 400, 14);
		panel_3.add(lblSetThe);
		
		JLabel lblSelectHow = new JLabel("3. Select how many seeds you would like to find before the program stops");
		lblSelectHow.setBounds(34, 111, 500, 14);
		panel_3.add(lblSelectHow);
		
		JLabel lblStartThe = new JLabel("4. Start the program and when a seed is found it will be output");
		lblStartThe.setBounds(34, 136, 400, 14);
		panel_3.add(lblStartThe);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Data", null, panel, null);
		panel.setLayout(null);
		
		seedCount = new JLabel("Current Rejected Seed Count: " + 0);
		seedCount.setBounds(10, 10, 250, 15);
		panel.add(seedCount);
		
		totalSeedCount = new JLabel("Total Rejected Seed Count: 0");
		totalSeedCount.setBounds(10, 30, 250, 15);
		panel.add(totalSeedCount);
		
		timeElapsed = new JLabel("Time Elapsed: 00:00:00");
		timeElapsed.setBounds(10, 325, 212, 14);
		panel.add(timeElapsed);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(listener);
		btnStart.setBounds(10, 350, 150, 25);
		panel.add(btnStart);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(listener);
		btnPause.setBounds(10, 385, 150, 25);
		panel.add(btnPause);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(listener);
		btnClear.setBounds(10, 420, 150, 25);
		panel.add(btnClear);
		
		JLabel lblDevMode = new JLabel("Dev Mode?");
		lblDevMode.setBounds(10, 290, 75, 20);
		panel.add(lblDevMode);

		btnDevCon = new JButton("Dev Console");
		btnDevCon.addActionListener(listener);
		btnDevCon.setBounds(10, 60, 150, 25);
		panel.add(btnDevCon);
		
		chkboxDevMode = new JCheckBox("");
		chkboxDevMode.addActionListener(listener);
		chkboxDevMode.setBounds(85, 290, 20, 20);
		if (Main.DEV_MODE) {
			chkboxDevMode.setText("True");
			chkboxDevMode.setSelected(true);
			Util.console("\n\nWARNING: dev features may be broken, or not work at all! Use at your own risk!\n\n");
		} else {
			chkboxDevMode.setText("False");
			chkboxDevMode.setSelected(false);
		}
		panel.add(chkboxDevMode);
		
		Container versionContainer = new Container();
		JLabel versionLabel = new JLabel("Minecraft Version:");
		versionContainer.setLayout(new FlowLayout());
		versionContainer.add(versionLabel);
		versionContainer.add(versionBox);
		versionContainer.setBounds(400, 380, 100, 100);
		panel.add(versionContainer);
		
		JLabel lblFindStructures = new JLabel("Find structures?");
		lblFindStructures.setBounds(400, 350, 125, 20);
		panel.add(lblFindStructures);
		
		if (Main.DEV_MODE) {
			findStructures = new JCheckBox();
			findStructures.setBounds(525, 350, 20, 20);
			findStructures.addActionListener(listener);
			panel.add(findStructures);
		} else {
			JLabel findStructuresTxt = new JLabel("Coming Soon");
			findStructuresTxt.setBounds(525, 350, 125, 20);
			panel.add(findStructuresTxt);
		}
		
		JLabel lblSearchWidth = new JLabel("Search Width (x):");
		lblSearchWidth.setBounds(175, 350, 200, 20);
		panel.add(lblSearchWidth);
		
		widthSearch = new JTextField();
		widthSearch.setText(""+searchQuadrantWidth);
		widthSearch.setBounds(285, 350, 85, 20);
		panel.add(widthSearch);
		widthSearch.setColumns(10);
		
		JLabel lblSearchHeight = new JLabel("Search Height (z):");
		lblSearchHeight.setBounds(175, 385, 200, 20);
		panel.add(lblSearchHeight);
		
		heightSearch = new JTextField();
		heightSearch.setText(""+searchQuadrantHeight);
		heightSearch.setBounds(285, 385, 85, 20);
		panel.add(heightSearch);
		heightSearch.setColumns(10);
		
		JLabel maxSeedsLabel = new JLabel("Seeds to Find:");
		maxSeedsLabel.setBounds(175, 420, 150, 20);
		panel.add(maxSeedsLabel);
		
		maxSeeds = new JTextField();
		maxSeeds.setText(""+maximumMatchingWorldsCount);
		maxSeeds.setBounds(285, 420, 85, 20);
		panel.add(maxSeeds);
		maxSeeds.setColumns(10);
		
		
		
		// Panel 1: Biomes
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Biomes", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblBiomeSelectionTxt = new JLabel("Biome Selection");
		Util.setFontSize(lblBiomeSelectionTxt, 24);
		lblBiomeSelectionTxt.setBounds(0, 0, (Main.BACK_FRAME_WIDTH-Main.CONSOLE_WIDTH), 33);
		lblBiomeSelectionTxt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblBiomeSelectionTxt);
		
		JScrollPane lblBiomeSelectionScroll = new JScrollPane();
		lblBiomeSelectionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		lblBiomeSelectionScroll.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
		lblBiomeSelectionScroll.getVerticalScrollBar().setUnitIncrement(10);
		panel_1.add(lblBiomeSelectionScroll);
		
				biomesPanel = new JPanel();
				lblBiomeSelectionScroll.setViewportView(biomesPanel);
				biomesPanel.setLayout(
						new FormLayout(
								new ColumnSpec[] {
										FormSpecs.DEFAULT_COLSPEC, // Col 1
										FormSpecs.DEFAULT_COLSPEC, // Col 2
										FormSpecs.DEFAULT_COLSPEC, // Col 3
										FormSpecs.RELATED_GAP_COLSPEC},
								new RowSpec[] {
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 2
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 4
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 6
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 8
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 10
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 12
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 14
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 16
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 18
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 20
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 22
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 24
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 26
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 28
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 30
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 32
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 34
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 36
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 38
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 40
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 42
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 44
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 46
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 48
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 50
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 52
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 54
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 56
										FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 58
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 60
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 62
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 64
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 66
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 68
										FormSpecs.RELATED_GAP_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC, // Row 70
										FormSpecs.RELATED_GAP_ROWSPEC,}));
				
				JLabel hotBiomesTxt = new JLabel("Hot Biomes");
				hotBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(hotBiomesTxt, 18);
				Util.Underline(hotBiomesTxt);
				biomesPanel.add(hotBiomesTxt, "2, 2");
				
				Container _2_4_container = new Container();
				_2_4_container.add(new JLabel("Desert"));
				_2_4_container.setLayout(new FlowLayout());
				_2_4_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_4_container, "1, 4");
				
				Container _4_4_container = new Container();
				_4_4_container.add(new JLabel("Desert Hills"));
				_4_4_container.setLayout(new FlowLayout());
				_4_4_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_4_container, "2, 4");
				
				Container _6_4_container = new Container();
				_6_4_container.add(new JLabel("Desert M"));
				_6_4_container.setLayout(new FlowLayout());
				_6_4_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_4_container, "3, 4");
				
				JLabel in_lushBiomesTxt = new JLabel("Lush Biomes");
				in_lushBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(in_lushBiomesTxt, 18);
				Util.Underline(in_lushBiomesTxt);
				biomesPanel.add(in_lushBiomesTxt, "2, 14");
				
				Container _2_16_container = new Container();
				_2_16_container.add(new JLabel("Plains"));
				_2_16_container.setLayout(new FlowLayout());
				_2_16_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_16_container, "1, 16");
				
				Container _6_16_container = new Container();
				_6_16_container.add(new JLabel("Forest"));
				_6_16_container.setLayout(new FlowLayout());
				_6_16_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_16_container, "3, 16");
				
				Container _2_18_container = new Container();
				_2_18_container.add(new JLabel("Forest Hills"));
				_2_18_container.setLayout(new FlowLayout());
				_2_18_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_18_container, "1, 18");
				
				Container _6_22_container = new Container();
				_6_22_container.add(new JLabel("Swampland"));
				_6_22_container.setLayout(new FlowLayout());
				_6_22_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_22_container, "3, 22");
				
				Container _2_24_container = new Container();
				_2_24_container.add(new JLabel("Swampland M"));
				_2_24_container.setLayout(new FlowLayout());
				_2_24_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_24_container, "1, 24");
				
				Container _4_24_container = new Container();
				_4_24_container.add(new JLabel("Jungle"));
				_4_24_container.setLayout(new FlowLayout());
				_4_24_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_24_container, "2, 24");
				
				Container _6_24_container = new Container();
				_6_24_container.add(new JLabel("Jungle Hills"));
				_6_24_container.setLayout(new FlowLayout());
				_6_24_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_24_container, "3, 24");
				
				Container _2_26_container = new Container();
				_2_26_container.add(new JLabel("Jungle Edge"));
				_2_26_container.setLayout(new FlowLayout());
				_2_26_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_26_container, "1, 26");
				
				Container _4_26_container = new Container();
				_4_26_container.add(new JLabel("Jungle M"));
				_4_26_container.setLayout(new FlowLayout());
				_4_26_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_26_container, "2, 26");
				
				Container _6_26_container = new Container();
				_6_26_container.add(new JLabel("Jungle Edge M"));
				_6_26_container.setLayout(new FlowLayout());
				_6_26_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_26_container, "3, 26");
				
				JLabel in_coldBiomesTxt = new JLabel("Cold Biomes");
				in_coldBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(in_coldBiomesTxt, 18);
				Util.Underline(in_coldBiomesTxt);
				biomesPanel.add(in_coldBiomesTxt, "2, 30");
				
				Container _2_32_container = new Container();
				_2_32_container.add(new JLabel("Extreme Hills"));
				_2_32_container.setLayout(new FlowLayout());
				_2_32_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_32_container, "1, 32");
				
				JLabel in_snowyBiomesTxt = new JLabel("Snowy Biomes");
				in_snowyBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(in_snowyBiomesTxt, 18);
				Util.Underline(in_snowyBiomesTxt);
				biomesPanel.add(in_snowyBiomesTxt, "2, 40");
				
				Container _2_42_container = new Container();
				_2_42_container.add(new JLabel("Cold Taiga"));
				_2_42_container.setLayout(new FlowLayout());
				_2_42_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_42_container, "1, 42");
				
				Container _4_42_container = new Container();
				_4_42_container.add(new JLabel("Cold Taiga Hills"));
				_4_42_container.setLayout(new FlowLayout());
				_4_42_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_42_container, "2, 42");
				
				Container _6_42_container = new Container();
				_6_42_container.add(new JLabel("Cold Taiga M"));
				_6_42_container.setLayout(new FlowLayout());
				_6_42_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_6_42_container, "3, 42");
				
				Container _2_44_container = new Container();
				_2_44_container.add(new JLabel("Ice Plains"));
				_2_44_container.setLayout(new FlowLayout());
				_2_44_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_44_container, "1, 44");
				
				Container _4_44_container = new Container();
				_4_44_container.add(new JLabel("Ice Mountains"));
				_4_44_container.setLayout(new FlowLayout());
				_4_44_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_44_container, "2, 44");
				
				JLabel in_waterBiomesTxt = new JLabel("Water Biomes");
				in_waterBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(in_waterBiomesTxt, 18);
				Util.Underline(in_waterBiomesTxt);
				biomesPanel.add(in_waterBiomesTxt, "2, 46");
				
				Container _2_48_container = new Container();
				_2_48_container.add(new JLabel("Beach"));
				_2_48_container.setLayout(new FlowLayout());
				_2_48_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_48_container, "1, 48");
				
				Container _2_50_container = new Container();
				_2_50_container.add(new JLabel("River"));
				_2_50_container.setLayout(new FlowLayout());
				_2_50_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_50_container, "1, 50");
				
				Container _4_50_container = new Container();
				_4_50_container.add(new JLabel("Ocean"));
				_4_50_container.setLayout(new FlowLayout());
				_4_50_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_50_container, "2, 50");
				
				Container _2_52_container = new Container();
				_2_52_container.add(new JLabel("Frozen River"));
				_2_52_container.setLayout(new FlowLayout());
				_2_52_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_52_container, "1, 52");
				
				Container _4_52_container = new Container();
				_4_52_container.add(new JLabel("Frozen Ocean"));
				_4_52_container.setLayout(new FlowLayout());
				_4_52_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_52_container, "2, 52");
				
				Container _2_54_container = new Container();
				_2_54_container.add(new JLabel("Mushroom Island"));
				_2_54_container.setLayout(new FlowLayout());
				_2_54_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_54_container, "1, 54");
				
				Container _4_54_container = new Container();
				_4_54_container.add(new JLabel("Mushroom Island Shore"));
				_4_54_container.setLayout(new FlowLayout());
				_4_54_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_4_54_container, "2, 54");
				
				
				JLabel in_biomeSets = new JLabel("Biome Sets");
				in_biomeSets.setHorizontalAlignment(SwingConstants.CENTER);
				Util.setFontSize(in_biomeSets, 18);
				Util.Underline(in_biomeSets);
				biomesPanel.add(in_biomeSets, "2, 60");

				Container _1_62_container = new Container();
				_1_62_container.add(new JLabel("Desert Set"));
				_1_62_container.setLayout(new FlowLayout());
				_1_62_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_1_62_container, "1, 62");

				Container _2_62_container = new Container();
				_2_62_container.add(new JLabel("Savanna Set"));
				_2_62_container.setLayout(new FlowLayout());
				_2_62_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_62_container, "2, 62");

				Container _3_62_container = new Container();
				_3_62_container.add(new JLabel("Mesa Set"));
				_3_62_container.setLayout(new FlowLayout());
				_3_62_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_3_62_container, "3, 62");

				Container _1_64_container = new Container();
				_1_64_container.add(new JLabel("Forest Set"));
				_1_64_container.setLayout(new FlowLayout());
				_1_64_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_1_64_container, "1, 64");

				Container _2_64_container = new Container();
				_2_64_container.add(new JLabel("Birch Forest Set"));
				_2_64_container.setLayout(new FlowLayout());
				_2_64_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_64_container, "2, 64");

				Container _3_64_container = new Container();
				_3_64_container.add(new JLabel("Roofed Forest Set"));
				_3_64_container.setLayout(new FlowLayout());
				_3_64_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_3_64_container, "3, 64");

				Container _1_66_container = new Container();
				_1_66_container.add(new JLabel("Swampland Set"));
				_1_66_container.setLayout(new FlowLayout());
				_1_66_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_1_66_container, "1, 66");

				
				Container _2_66_container = new Container();
				_2_66_container.add(new JLabel("Jungle Set"));
				_2_66_container.setLayout(new FlowLayout());
				_2_66_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_66_container, "2, 66");

				Container _3_66_container = new Container();
				_3_66_container.add(new JLabel("Bamboo Jungle Set"));
				_3_66_container.setLayout(new FlowLayout());
				_3_66_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_3_66_container, "3, 66");

				Container _1_68_container = new Container();
				_1_68_container.add(new JLabel("Extreme Hills Set"));
				_1_68_container.setLayout(new FlowLayout());
				_1_68_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_1_68_container, "1, 68");
				
				Container _2_68_container = new Container();
				_2_68_container.add(new JLabel("Taiga Set"));
				_2_68_container.setLayout(new FlowLayout());
				_2_68_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_68_container, "2, 68");

				Container _3_68_container = new Container();
				_3_68_container.add(new JLabel("Mega Taiga Set"));
				_3_68_container.setLayout(new FlowLayout());
				_3_68_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_3_68_container, "3, 68");

				Container _1_70_container = new Container();
				_1_70_container.add(new JLabel("Cold Taiga Set"));
				_1_70_container.setLayout(new FlowLayout());
				_1_70_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_1_70_container, "1, 70");

				Container _2_70_container = new Container();
				_2_70_container.add(new JLabel("Mushroom Island Set"));
				_2_70_container.setLayout(new FlowLayout());
				_2_70_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_2_70_container, "2, 70");

				Container _3_70_container = new Container();
				_3_70_container.add(new JLabel("Frozen Ocean Set"));
				_3_70_container.setLayout(new FlowLayout());
				_3_70_container.add(new JComboBox<String>(include_exclude_txt));
				biomesPanel.add(_3_70_container, "3, 70");
		/*
		 FORMAT:
		 
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "2, ");
		
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "4, ");
		
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "6, ");
		 */
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _2_6_container = new Container();
			_2_6_container.add(new JLabel("Savanna"));
			_2_6_container.setLayout(new FlowLayout());
			_2_6_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_6_container, "1, 6");
			
			Container _4_6_container = new Container();
			_4_6_container.add(new JLabel("Savanna Plateau"));
			_4_6_container.setLayout(new FlowLayout());
			_4_6_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_6_container, "2, 6");
			
			Container _6_6_container = new Container();
			_6_6_container.add(new JLabel("Savanna M"));
			_6_6_container.setLayout(new FlowLayout());
			_6_6_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_6_container, "3, 6");
			
			Container _2_8_container = new Container();
			_2_8_container.add(new JLabel("Savanna Plateau M"));
			_2_8_container.setLayout(new FlowLayout());
			_2_8_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_8_container, "1, 8");
			
			Container _4_8_container = new Container();
			_4_8_container.add(new JLabel("Mesa"));
			_4_8_container.setLayout(new FlowLayout());
			_4_8_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_8_container, "2, 8");
			
			Container _6_8_container = new Container();
			_6_8_container.add(new JLabel("Mesa Plateau F"));
			_6_8_container.setLayout(new FlowLayout());
			_6_8_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_8_container, "3, 8");
			
			Container _2_10_container = new Container();
			_2_10_container.add(new JLabel("Mesa Plateau"));
			_2_10_container.setLayout(new FlowLayout());
			_2_10_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_10_container, "1, 10");
			
			Container _4_10_container = new Container();
			_4_10_container.add(new JLabel("Mesa (Bryce)"));
			_4_10_container.setLayout(new FlowLayout());
			_4_10_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_10_container, "2, 10");
			
			Container _6_10_container = new Container();
			_6_10_container.add(new JLabel("Mesa Plateau F M"));
			_6_10_container.setLayout(new FlowLayout());
			_6_10_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_10_container, "3, 10");
			
			Container _2_12_container = new Container();
			_2_12_container.add(new JLabel("Mesa Plateau F M"));
			_2_12_container.setLayout(new FlowLayout());
			_2_12_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_12_container, "1, 12");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _4_16_container = new Container();
			_4_16_container.add(new JLabel("Sunflower Plains"));
			_4_16_container.setLayout(new FlowLayout());
			_4_16_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_16_container, "2, 16");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _4_18_container = new Container();
			_4_18_container.add(new JLabel("Flower Forest"));
			_4_18_container.setLayout(new FlowLayout());
			_4_18_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_18_container, "2, 18");
			
			Container _6_18_container = new Container();
			_6_18_container.add(new JLabel("Birch Forest"));
			_6_18_container.setLayout(new FlowLayout());
			_6_18_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_18_container, "3, 18");
			
			Container _2_20_container = new Container();
			_2_20_container.add(new JLabel("Birch Forest Hills"));
			_2_20_container.setLayout(new FlowLayout());
			_2_20_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_20_container, "1, 20");
			
			Container _4_20_container = new Container();
			_4_20_container.add(new JLabel("Birch Forest M"));
			_4_20_container.setLayout(new FlowLayout());
			_4_20_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_20_container, "2, 20");
			
			Container _6_20_container = new Container();
			_6_20_container.add(new JLabel("Birch Forest Hills M"));
			_6_20_container.setLayout(new FlowLayout());
			_6_20_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_20_container, "3, 20");
			
			Container _2_22_container = new Container();
			_2_22_container.add(new JLabel("Roofed Forest"));
			_2_22_container.setLayout(new FlowLayout());
			_2_22_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_22_container, "1, 22");
			
			Container _4_22_container = new Container();
			_4_22_container.add(new JLabel("Roofed Forest M"));
			_4_22_container.setLayout(new FlowLayout());
			_4_22_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_22_container, "2, 22");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_14_3)) {
			Container _2_28_container = new Container();
			_2_28_container.add(new JLabel("Bamboo Jungle"));
			_2_28_container.setLayout(new FlowLayout());
			_2_28_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_28_container, "1, 28");
			
			Container _4_28_container = new Container();
			_4_28_container.add(new JLabel("Bamboo Jungle Hills"));
			_4_28_container.setLayout(new FlowLayout());
			_4_28_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_28_container, "2, 28");
		}
		
		// Biome stopped spawning naturally as of 1.7
		if (Version.isOrOlderThanVersion(Version.V1_6_4)) {
			Container _4_32_container = new Container();
			_4_32_container.add(new JLabel("Extreme Hills Edge"));
			_4_32_container.setLayout(new FlowLayout());
			_4_32_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_32_container, "2, 32");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _6_32_container = new Container();
			_6_32_container.add(new JLabel("Extreme Hills+"));
			_6_32_container.setLayout(new FlowLayout());
			_6_32_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_32_container, "3, 32");
			
			Container _2_34_container = new Container();
			_2_34_container.add(new JLabel("Extreme Hills M"));
			_2_34_container.setLayout(new FlowLayout());
			_2_34_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_34_container, "1, 34");
			
			Container _4_34_container = new Container();
			_4_34_container.add(new JLabel("Extreme Hills+ M"));
			_4_34_container.setLayout(new FlowLayout());
			_4_34_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_34_container, "2, 34");
			
			Container _6_34_container = new Container();
			_6_34_container.add(new JLabel("Taiga"));
			_6_34_container.setLayout(new FlowLayout());
			_6_34_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_34_container, "3, 34");
		
			Container _2_36_container = new Container();
			_2_36_container.add(new JLabel("Taiga Hills"));
			_2_36_container.setLayout(new FlowLayout());
			_2_36_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_36_container, "1, 36");
			
			Container _4_36_container = new Container();
			_4_36_container.add(new JLabel("Mega Taiga"));
			_4_36_container.setLayout(new FlowLayout());
			_4_36_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_36_container, "2, 36");
			
			Container _6_36_container = new Container();
			_6_36_container.add(new JLabel("Mega Taiga Hills"));
			_6_36_container.setLayout(new FlowLayout());
			_6_36_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_36_container, "3, 36");
			
			Container _2_38_container = new Container();
			_2_38_container.add(new JLabel("Taiga M"));
			_2_38_container.setLayout(new FlowLayout());
			_2_38_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_38_container, "1, 38");
			
			Container _4_38_container = new Container();
			_4_38_container.add(new JLabel("Mega Spruce Taiga"));
			_4_38_container.setLayout(new FlowLayout());
			_4_38_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_38_container, "2, 38");
			
			Container _6_38_container = new Container();
			_6_38_container.add(new JLabel("Mega Spruce Taiga (Hills)"));
			_6_38_container.setLayout(new FlowLayout());
			_6_38_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_38_container, "3, 38");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _6_44_container = new Container();
			_6_44_container.add(new JLabel("Ice Plains Spikes"));
			_6_44_container.setLayout(new FlowLayout());
			_6_44_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_44_container, "3, 44");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _4_48_container = new Container();
			_4_48_container.add(new JLabel("Stone Beach"));
			_4_48_container.setLayout(new FlowLayout());
			_4_48_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_48_container, "2, 48");
			
			Container _6_48_container = new Container();
			_6_48_container.add(new JLabel("Cold Beach"));
			_6_48_container.setLayout(new FlowLayout());
			_6_48_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_48_container, "3, 48");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_7_10)) {
			Container _6_50_container = new Container();
			_6_50_container.add(new JLabel("Deep Ocean"));
			_6_50_container.setLayout(new FlowLayout());
			_6_50_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_50_container, "3, 50");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_13_2)) {
			Container _6_52_container = new Container();
			_6_52_container.add(new JLabel("Frozen Deep Ocean"));
			_6_52_container.setLayout(new FlowLayout());
			_6_52_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_52_container, "3, 52");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_13_2)) {
			Container _6_54_container = new Container();
			_6_54_container.add(new JLabel("Warm Ocean"));
			_6_54_container.setLayout(new FlowLayout());
			_6_54_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_54_container, "3, 54");
		}
		
		if (Version.isOrNewerThanVersion(Version.V9_99_99)) {
			Container _2_56_container = new Container();
			_2_56_container.add(new JLabel("Warm Deep Ocean"));
			_2_56_container.setLayout(new FlowLayout());
			_2_56_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_56_container, "1, 56");
		}
		
		if (Version.isOrNewerThanVersion(Version.V1_13_2)) {
			Container _4_56_container = new Container();
			_4_56_container.add(new JLabel("Lukewarm Ocean"));
			_4_56_container.setLayout(new FlowLayout());
			_4_56_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_56_container, "2, 56");
			
			Container _6_56_container = new Container();
			_6_56_container.add(new JLabel("Lukewarm Deep Ocean"));
			_6_56_container.setLayout(new FlowLayout());
			_6_56_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_6_56_container, "3, 56");
			
			Container _2_58_container = new Container();
			_2_58_container.add(new JLabel("Cold Ocean"));
			_2_58_container.setLayout(new FlowLayout());
			_2_58_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_2_58_container, "1, 58");
			
			Container _4_58_container = new Container();
			_4_58_container.add(new JLabel("Cold Deep Ocean"));
			_4_58_container.setLayout(new FlowLayout());
			_4_58_container.add(new JComboBox<String>(include_exclude_txt));
			biomesPanel.add(_4_58_container, "2, 58");
		}
		
		
		
		// Panel 2: Structures
		
		if (Main.DEV_MODE) {
			JPanel panel_2 = new JPanel();
			tabbedPane.addTab("Structures", null, panel_2, null);
			panel_2.setLayout(null);
			tabbedPane.setEnabledAt(3, false);
			JLabel lblStructuresTxt = new JLabel("Select Structures");
			Util.setFontSize(lblStructuresTxt, 24);
			lblStructuresTxt.setBounds(0, 0, (Main.BACK_FRAME_WIDTH-Main.CONSOLE_WIDTH), 33);
			lblStructuresTxt.setHorizontalAlignment(SwingConstants.CENTER);
			panel_2.add(lblStructuresTxt);
			
			JScrollPane lblStructuresScroll = new JScrollPane();
			lblStructuresScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			lblStructuresScroll.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
			lblStructuresScroll.getVerticalScrollBar().setUnitIncrement(10);
			panel_2.add(lblStructuresScroll);
			
			structures = new JPanel();
			lblStructuresScroll.setViewportView(structures);
			structures.setLayout(
					new FormLayout(
							new ColumnSpec[] {
									FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 1
									FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 2
									FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 3
									},
							new RowSpec[] {
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 2
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 4
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 6
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 8
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 10
									FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 12
									FormSpecs.RELATED_GAP_ROWSPEC,}));
			
			/*
			 FORMAT:
			 
			JCheckBox exb__ = new JCheckBox("");
			excludeCB.add(exb__, "2, ");
			
			JCheckBox exb__ = new JCheckBox("");
			excludeCB.add(exb__, "4, ");
			
			JCheckBox exb__ = new JCheckBox("");
			excludeCB.add(exb__, "6, ");
			 */
			
			JLabel ex_undergroundFeaturesTxt = new JLabel("Underground Features");
			ex_undergroundFeaturesTxt.setHorizontalAlignment(SwingConstants.CENTER);
			Util.setFontSize(ex_undergroundFeaturesTxt, 18);
			Util.Underline(ex_undergroundFeaturesTxt);
			structures.add(ex_undergroundFeaturesTxt, "4, 2");
			
			JCheckBox cb_mineshaft = new JCheckBox("Mineshaft");
			structures.add(cb_mineshaft, "2, 4");
			
			JLabel ex_oceanFeaturesTxt = new JLabel("Ocean Features");
			ex_oceanFeaturesTxt.setHorizontalAlignment(SwingConstants.CENTER);
			Util.setFontSize(ex_oceanFeaturesTxt, 18);
			Util.Underline(ex_oceanFeaturesTxt);
			structures.add(ex_oceanFeaturesTxt, "4, 6");
			
			JCheckBox cb_ocean_monument = new JCheckBox("Ocean Monument");
			structures.add(cb_ocean_monument, "2, 8");
			
			JCheckBox cb_ocean_features = new JCheckBox("Ocean Features");
			structures.add(cb_ocean_features, "4, 8");
			
			JCheckBox cb_ocean_ruins = new JCheckBox("Ocean Ruins");
			structures.add(cb_ocean_ruins, "6, 8");

			JLabel ex_surfaceFeaturesTxt = new JLabel("Surface Features");
			ex_surfaceFeaturesTxt.setHorizontalAlignment(SwingConstants.CENTER);
			Util.setFontSize(ex_surfaceFeaturesTxt, 18);
			Util.Underline(ex_surfaceFeaturesTxt);
			structures.add(ex_surfaceFeaturesTxt, "4, 10");

			JCheckBox cb_mansion = new JCheckBox("Mansion");
			structures.add(cb_mansion, "2, 12");

			JCheckBox cb_stronghold = new JCheckBox("Stronghold");
			structures.add(cb_stronghold, "4, 12");

			JCheckBox cb_village = new JCheckBox("Village");
			structures.add(cb_village, "6, 12");
		}
		
	}
	
	
	/**
	 * Some Biomes come back as null. No idea. The Names match each other so it
	 * should work (Apparently it works like 1 in 10 times...)
	 * 
	 * @param biomeCodesCount
	 * @param biomeCodes
	 * @return
	 * @throws UnknownBiomeIndexException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws FormatException
	 * @throws MinecraftInterfaceCreationException
	 */
	public static Biome[] manageCheckedCheckboxes() throws UnknownBiomeIndexException, InterruptedException,
				IOException, FormatException, MinecraftInterfaceCreationException {
		Component[] comps = biomesPanel.getComponents();
		List<String> checkedTexts = new ArrayList<String>();
		for (Component comp : comps) {
			if (comp instanceof Container) {
				Container container = (Container) comp;
				if (container.getComponents().length < 2) continue;
				@SuppressWarnings("unchecked")
				JComboBox<String> box = (JComboBox<String>) container.getComponent(1);
				if (box.getSelectedIndex() == 1) {
					JLabel biome_name_lbl = (JLabel) container.getComponent(0);
					if(!biome_name_lbl.getText().contains(" Set")){
						checkedTexts.add(biome_name_lbl.getText());
						System.out.println("Include Biome "+biome_name_lbl.getText());
					}
				}
			}
		}
		
		Biome[] biomes = new Biome[checkedTexts.size()];
		for (int i = 0; i < checkedTexts.size(); i++) {
			biomes[i] = Biome.getByName(checkedTexts.get(i));
		}
		
//		if (biomes.length == 0) {
//			allowThreadToSearch = false;
//			Util.console("\nPlease select Biomes!\nSearch has cancelled.\nRecommend you clear console!\n");
//			stop();
//		}
		return biomes;
	}
	
	public static Biome[] manageCheckedCheckboxesRejected() throws UnknownBiomeIndexException, InterruptedException,
				IOException, FormatException, MinecraftInterfaceCreationException {
		Component[] comps = biomesPanel.getComponents();
		List<String> checkedTexts = new ArrayList<String>();
		for (Component comp : comps) {
			if (comp instanceof Container) {
				Container container = (Container) comp;
				if (container.getComponents().length < 2) continue;
				@SuppressWarnings("unchecked")
				JComboBox<String> box = (JComboBox<String>) container.getComponent(1);
				if (box.getSelectedIndex() == 2) {
					JLabel biome_name_lbl = (JLabel) container.getComponent(0);
					if(!biome_name_lbl.getText().contains(" Set")){
						checkedTexts.add(biome_name_lbl.getText());
						System.out.println("Exclude Biome "+biome_name_lbl.getText());
					}
				}
			}
		}
		
		Biome[] biomes = new Biome[checkedTexts.size()];
		for (int i = 0; i < checkedTexts.size(); i++) {
			biomes[i] = Biome.getByName(checkedTexts.get(i));
		}
		
//		if (biomes.length == 0) {
//			Util.console("\nPlease select Rejected Biomes!\nSearch has cancelled.\nRecommend you clear console!\n");
//			stop();
//		}
		return biomes;
	}

	public static HashMap<Biome, String> manageCheckedCheckboxesSets() throws UnknownBiomeIndexException, InterruptedException,
		IOException, FormatException, MinecraftInterfaceCreationException {
			Component[] comps = biomesPanel.getComponents();
			HashMap<String, String> checkedTexts = new HashMap<String, String>();
			for (Component comp : comps) {
				if (comp instanceof Container) {
					Container container = (Container) comp;
					if (container.getComponents().length < 2) continue;
					@SuppressWarnings("unchecked")
					JComboBox<String> box = (JComboBox<String>) container.getComponent(1);
					if (box.getSelectedIndex() == 1) {
						JLabel biome_name_lbl = (JLabel) container.getComponent(0);
						if(biome_name_lbl.getText().contains(" Set")){
							checkedTexts.putAll(biomeSets(biome_name_lbl.getText()));
							// checkedTexts.add(biome_name_lbl.getText());
							System.out.println("Include Biome Sets "+biome_name_lbl.getText());
						}
					}
				}
			}

			HashMap<Biome, String> sets = new HashMap<Biome, String>();
			for (Map.Entry<String, String> entry : checkedTexts.entrySet()) {
				Biome biome = Biome.getByName(entry.getKey());
				String set = entry.getValue();
				sets.put(biome, set);
			}
		

		return sets;
	}

	public static HashMap<Biome, String> manageCheckedCheckboxesSetsRejected() throws UnknownBiomeIndexException, InterruptedException,
	IOException, FormatException, MinecraftInterfaceCreationException {
		Component[] comps = biomesPanel.getComponents();
		HashMap<String, String> checkedTexts = new HashMap<String, String>();
		for (Component comp : comps) {
			if (comp instanceof Container) {
				Container container = (Container) comp;
				if (container.getComponents().length < 2) continue;
				@SuppressWarnings("unchecked")
				JComboBox<String> box = (JComboBox<String>) container.getComponent(1);
				if (box.getSelectedIndex() == 2) {
					JLabel biome_name_lbl = (JLabel) container.getComponent(0);
					if(biome_name_lbl.getText().contains(" Set")){
						checkedTexts.putAll(biomeSets(biome_name_lbl.getText()));
						// checkedTexts.add(biome_name_lbl.getText());
						System.out.println("Rejected Biome Sets "+biome_name_lbl.getText());
					}
				}
			}
		}

		HashMap<Biome, String> sets = new HashMap<Biome, String>();
		for (Map.Entry<String, String> entry : checkedTexts.entrySet()) {
			Biome biome = Biome.getByName(entry.getKey());
			String set = entry.getValue();
			sets.put(biome, set);
		}
	

	return sets;
}
	
	public static Type[] manageCheckedCheckboxesFindStructures() throws UnknownBiomeIndexException, InterruptedException,
				IOException, FormatException, MinecraftInterfaceCreationException {
		Component[] comps = structures.getComponents();
		List<String> checkedTexts = new ArrayList<String>();
		for (Component comp : comps) {
			if (comp instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) comp;
				if (box.isSelected()) {
					String text = box.getText();
					// System.out.println(box.getText());
					checkedTexts.add(text);
					System.out.println("Include Structures "+text);
				}
			}
		}
		
		StructureSearcher.Type[] structures = new StructureSearcher.Type[checkedTexts.size()];
		for (int i = 0; i < checkedTexts.size(); i++) {
			structures[i] = StructureSearcher.Type.valueOf(checkedTexts.get(i).replaceAll(" ", "_").toUpperCase());
		}
		
		if (structures.length == 0) {
			Util.console("\nPlease select Structures!\nSearch has cancelled.\nRecommend you clear console!\n");
			stop();
		}
		return structures;
	}

	public static HashMap<String, String> biomeSets(String name){
		HashMap<String, String> checkedTexts = new HashMap<String, String>();
		switch(name){
			case "Desert Set": 
				checkedTexts.put("Desert", "Desert Set");
				checkedTexts.put("Desert Hills", "Desert Set");
				checkedTexts.put("Desert M", "Desert Set");
				break;
			case "Savanna Set":
				checkedTexts.put("Savanna", "Savanna Set");
				checkedTexts.put("Savanna Plateau", "Savanna Set");
				checkedTexts.put("Savanna M", "Savanna Set");
				checkedTexts.put("Savanna Plateau M", "Savanna Set");
				break;
			case "Mesa Set":
				checkedTexts.put("Mesa", "Mesa Set");
				checkedTexts.put("Mesa Plateau F", "Mesa Set");
				checkedTexts.put("Mesa Plateau", "Mesa Set");
				checkedTexts.put("Mesa (Bryce)", "Mesa Set");
				checkedTexts.put("Mesa Plateau F M", "Mesa Set");
				break;
			case "Forest Set":
				checkedTexts.put("Forest", "Forest Set");
				checkedTexts.put("Forest Hills", "Forest Set");
				break;
			case "Birch Forest Set":
				checkedTexts.put("Birch Forest", "Birch Forest Set");
				checkedTexts.put("Birch Forest Hills", "Birch Forest Set");
				checkedTexts.put("Birch Forest M", "Birch Forest Set");
				checkedTexts.put("Birch Forest Hills M", "Birch Forest Set");
				break;
			case "Roofed Forest Set":
				checkedTexts.put("Roofed Forest", "Roofed Forest Set");
				checkedTexts.put("Roofed Forest M", "Roofed Forest Set");
				break;
			case "Swampland Set": 
				checkedTexts.put("Swampland", "Swampland Set");
				checkedTexts.put("Swampland M", "Swampland Set");
				break;
			case "Jungle Set":
				checkedTexts.put("Jungle", "Jungle Set");
				checkedTexts.put("Jungle Hills", "Jungle Set");
				checkedTexts.put("Jungle Edge", "Jungle Set");
				checkedTexts.put("Jungle M", "Jungle Set");
				checkedTexts.put("Jungle Edge M", "Jungle Set");
				break;
			case "Bamboo Jungle Set":
				checkedTexts.put("Bamboo Jungle", "Bamboo Jungle Set");
				checkedTexts.put("Bamboo Jungle Hills", "Bamboo Jungle Set");
				break;
			case "Extreme Hills Set":
				checkedTexts.put("Extreme Hills", "Extreme Hills Set");
				checkedTexts.put("Extreme Hills+", "Extreme Hills Set");
				checkedTexts.put("Extreme Hills M", "Extreme Hills Set");
				checkedTexts.put("Extreme Hills+ M", "Extreme Hills Set");
				checkedTexts.put("Extreme Hills Edge", "Extreme Hills Set");
				break;
			case "Taiga Set":
				checkedTexts.put("Taiga", "Taiga Set");
				checkedTexts.put("Taiga Hills", "Taiga Set");
				checkedTexts.put("Taiga M", "Taiga Set");
				break;
			case "Mega Taiga Set":
				checkedTexts.put("Mega Taiga", "Mega Taiga Set");
				checkedTexts.put("Mega Taiga Hills", "Mega Taiga Set");
				checkedTexts.put("Mega Spruce Taiga", "Mega Taiga Set");
				checkedTexts.put("Mega Spruce Taiga (Hills)", "Mega Taiga Set");
				break;
			case "Cold Taiga Set":
				checkedTexts.put("Cold Taiga", "Cold Taiga Set");
				checkedTexts.put("Cold Taiga Hills", "Cold Taiga Set");
				checkedTexts.put("Cold Taiga M", "Cold Taiga Set");
				break;
			case "Mushroom Island Set":
				checkedTexts.put("Mushroom Island", "Mushroom Island Set");
				checkedTexts.put("Mushroom Island Shore", "Mushroom Island Set");
				break;
			case "Frozen Ocean Set":
				checkedTexts.put("Frozen Ocean", "Frozen Ocean Set");
				checkedTexts.put("Frozen Deep Ocean", "Frozen Ocean Set");
				break;
			default: 

				break;
		}
		return checkedTexts;
	}
}
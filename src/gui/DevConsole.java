package gui;

import java.io.*;
import javax.swing.*;
import java.awt.*;

public class DevConsole{

    public static void showDevConsole() {
        JFrame devConsole = new JFrame("Developer Console");

        devConsole.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        devConsole.setSize(800,800);

        JPanel panel_devconsole = new JPanel();
		panel_devconsole.setBounds(0, 0, 100, 25);
		devConsole.getContentPane().add(panel_devconsole);
		panel_devconsole.setLayout(new BorderLayout(0, 0));

        JLabel consoleTxt = new JLabel("Console Output");
		consoleTxt.setHorizontalAlignment(SwingConstants.CENTER);
        panel_devconsole.add(consoleTxt, BorderLayout.CENTER);
        
        JScrollPane consoleScrollBar = new JScrollPane();
		consoleScrollBar.setBounds(0, 25, 100, 800);
		panel_devconsole.add(consoleScrollBar);
		
		JTextArea console = new JTextArea();
		console.setLineWrap(true);
		console.setEditable(false);
        consoleScrollBar.setViewportView(console);
        
        devConsole.setLocationRelativeTo(null);
        devConsole.pack();
        devConsole.setVisible(true);
		
	}

}
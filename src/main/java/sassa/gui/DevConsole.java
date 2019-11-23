package sassa.gui;

import java.io.*;
import javax.swing.*;
import java.awt.*;

import sassa.util.CustomOutputStream;

public class DevConsole{

    /**
     * @wbp.parser.entryPoint
     */
    public static void showDevConsole() {
        JFrame devConsole = new JFrame("Developer Console");

        devConsole.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        devConsole.setSize(481,390);
        
                JPanel panel_devconsole = new JPanel();
                panel_devconsole.setBounds(0, 0, 400, 400);
                devConsole.getContentPane().add(panel_devconsole);
                panel_devconsole.setLayout(new BorderLayout(0, 0));
                
                        JLabel consoleTxt = new JLabel("Developer Output");
                        consoleTxt.setHorizontalAlignment(SwingConstants.CENTER);
                        panel_devconsole.add(consoleTxt, BorderLayout.NORTH);
                        
                        JScrollPane consoleScrollBar = new JScrollPane();
                        consoleScrollBar.setBounds(0, 75, 100, 800);
                        panel_devconsole.add(consoleScrollBar, BorderLayout.CENTER);
                        
                        JTextArea console = new JTextArea();
                        console.setRows(25);
                        console.setColumns(75);
                        console.setLineWrap(true);
                        console.setEditable(false);
                        consoleScrollBar.setViewportView(console);

                        PrintStream printStream = new PrintStream(new CustomOutputStream(console));
                        System.setOut(printStream);
                        System.setErr(printStream);
        
        devConsole.setLocationRelativeTo(null);
        devConsole.pack();
        devConsole.setVisible(true);
		
	}

}
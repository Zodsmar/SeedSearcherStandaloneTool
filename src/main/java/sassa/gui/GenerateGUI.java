package sassa.gui;

import com.jgoodies.forms.layout.*;
import org.json.simple.parser.ParseException;
import sassa.main.Main;
import sassa.util.Util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GenerateGUI {

    public static void showGenerateGUI() throws IOException, ParseException {
        JFrame genGUI = new JFrame("Generated GUI");

        genGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        genGUI.setSize(900,515);

        JPanel panel_generated = new JPanel();
        panel_generated.setBounds(0, 0, 400, 400);
        genGUI.getContentPane().add(panel_generated);
        panel_generated.setLayout(new BorderLayout(0, 0));

        JScrollPane generatedScrollBar = new JScrollPane();
        generatedScrollBar.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
        generatedScrollBar.getVerticalScrollBar().setUnitIncrement(10);
        panel_generated.add(generatedScrollBar);

        JPanel biomesPanel = new JPanel();

        Util gen = new Util();
        ArrayList<String> searchingList = gen.createSearchLists("Biomes");

        biomesPanel.setLayout(new GridLayout(searchingList.size()/3, 3));

        generatedScrollBar.setViewportView(biomesPanel);

        for(int i = 0; i < searchingList.size(); i++){
                Container container = new Container();
                Border border = BorderFactory.createLineBorder(Color.darkGray, 1);
                JLabel label = new JLabel(searchingList.get(i));
                label.setBorder(border);
                label.setHorizontalAlignment(JLabel.CENTER);
                container.add(label);
                container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
                biomesPanel.add(container);
        }
        genGUI.setLocationRelativeTo(null);
        genGUI.pack();
        genGUI.setVisible(true);
    }
}

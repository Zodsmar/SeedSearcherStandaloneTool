package sassa.gui;

import com.jgoodies.forms.layout.*;
import org.json.simple.parser.ParseException;
import sassa.main.Main;
import sassa.util.Util;

import javax.swing.*;
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
        generatedScrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        generatedScrollBar.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
        generatedScrollBar.getVerticalScrollBar().setUnitIncrement(10);
        panel_generated.add(generatedScrollBar);

        JPanel biomesPanel = new JPanel();

        FormLayout bLayout= new FormLayout(new ColumnSpec[]{
                FormSpecs.DEFAULT_COLSPEC, // Col 1
                FormSpecs.DEFAULT_COLSPEC, // Col 2
                FormSpecs.DEFAULT_COLSPEC, // Col 3
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
        }, new RowSpec[]{RowSpec.decode("default:grow")});



        Util gen = new Util();
        ArrayList<String> searchingList = gen.createSearchLists("Biomes");
        ArrayList<String> originalSearchingList = searchingList;

        for(int i = 2; i <= (originalSearchingList.size()/3)*2; i+=2){
            bLayout.appendRow(FormSpecs.DEFAULT_ROWSPEC);
            bLayout.appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
        }

        biomesPanel.setLayout(bLayout);
        generatedScrollBar.setViewportView(biomesPanel);

        for(int i = 2; i <= (originalSearchingList.size()/3)*2; i+=2){
            for(int j = 1; j <= 3; j++){
                Container container = new Container();
                container.add(new JLabel(searchingList.get(0), JLabel.CENTER));
                container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
                biomesPanel.add(container, ""+j+","+i);
                System.out.println(""+j+","+i);
                searchingList.remove(0);
            }
        }




        genGUI.setLocationRelativeTo(null);
        genGUI.pack();
        genGUI.setVisible(true);
    }
}

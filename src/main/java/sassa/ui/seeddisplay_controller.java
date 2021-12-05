package sassa.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class seeddisplay_controller {
    @FXML
    Label seed_label;

    @FXML
    Button copy_clipboard, show_thumbnail;

    public void setSeed_label(String seed) {
        seed_label.setText(seed);
    }

    @FXML
    void setCopy_clipboard() {
        StringSelection stringSelection = new StringSelection(seed_label.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}

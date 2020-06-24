package sassa.gui;

import javafx.application.Platform;
import javafx.scene.text.Text;
import sassa.util.Singleton;

import java.io.IOException;

public class Variables {
    private static int checkedWorlds = 0;
    private static int acceptedWorlds = 0;
    private static int worldsSinceAccepted = 0;
    private static Singleton singleton = Singleton.getInstance();

    public static void reset(){
        checkedWorlds = 0;
        acceptedWorlds = 0;
        worldsSinceAccepted = 0;
    }

    public static int checkWorld(){
        ++worldsSinceAccepted;
        ++checkedWorlds;
        // Update gui text
//        Platform.runLater(() -> {
//            Text elem = singleton.getCRejSeed();
//            if (elem != null) elem.setText("" + worldsSinceAccepted);
//            elem = singleton.getTRejSeed();
//            if (elem != null) elem.setText("" + checkedWorlds);
//        });
        return checkedWorlds;
    }

    public static int acceptWorld(){
        worldsSinceAccepted = 0;
        ++acceptedWorlds;
        return acceptedWorlds;
    }

    public static void minOneCheckWorld() {
        checkedWorlds--;
    }

    public static void updateCurrentSeed(long seed){
        // Update gui text
        Platform.runLater(() -> {
            Text elem = singleton.getSequenceSeed();
            if (elem != null) elem.setText("" + seed);
        });
    }

    public static int checkedWorlds(){
        return checkedWorlds;
    }

    public static int acceptedWorlds(){
        return acceptedWorlds;
    }

    public static int worldsSinceAccepted(){
        return worldsSinceAccepted;
    }
}

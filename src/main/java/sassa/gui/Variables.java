package sassa.gui;

import javafx.application.Platform;
import javafx.scene.text.Text;
import sassa.util.Singleton;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Variables {
    private static AtomicLong checkedWorlds = new AtomicLong(0);
    private static int acceptedWorlds = 0;
    private static AtomicLong worldsSinceAccepted = new AtomicLong(0);
    private static Singleton singleton = Singleton.getInstance();

    public static void reset(){
        checkedWorlds.set(0);
        acceptedWorlds = 0;
        worldsSinceAccepted.set(0);
    }

    public static AtomicLong checkWorld(long value){
        worldsSinceAccepted.addAndGet(value);
        checkedWorlds.addAndGet(value);
        return checkedWorlds;
    }

    public static int acceptWorld(){
        worldsSinceAccepted.set(0);
        ++acceptedWorlds;
        return acceptedWorlds;
    }

    public static void minOneCheckWorld() {
        checkedWorlds.addAndGet(-1);
    }

    public static void updateCurrentSeed(long seed){
        // Update gui text
        Platform.runLater(() -> {
            Text elem = singleton.getSequenceSeed();
            if (elem != null) elem.setText("" + seed);
        });
    }

    public static AtomicLong checkedWorlds(){
        return checkedWorlds;
    }

    public static int acceptedWorlds(){
        return acceptedWorlds;
    }

    public static AtomicLong worldsSinceAccepted(){
        return worldsSinceAccepted;
    }
}

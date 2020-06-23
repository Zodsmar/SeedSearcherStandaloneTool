package sassa.util;

import com.sun.tools.javac.comp.Check;

public class CheckProvider {

    long worldSeed;
    boolean structIn, structOut, biomeIn, biomeOut, categoriesIn, categoriesOut;

    public CheckProvider(long worldSeed, boolean structIn, boolean structOut, boolean biomeIn, boolean biomesOut, boolean categoriesIn, boolean categoriesOut) {

        this.worldSeed = worldSeed;
        this.structIn = structIn;
        this.structOut = structOut;
        this.biomeIn = biomeIn;
        this.biomeOut = biomesOut;
        this.categoriesIn = categoriesIn;
        this.categoriesOut = categoriesOut;
    }
}

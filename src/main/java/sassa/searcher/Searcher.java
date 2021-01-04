package sassa.searcher;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.Collection;

public class Searcher {
    protected long worldSeed;
    protected Collection<?> collection;
    private BiomeSource oSource, nSource, eSource;

    public Searcher(long worldSeed, Collection<?> collection){
        this.worldSeed = worldSeed;
        this.collection = collection;
        createBiomeSources(worldSeed);
    }

    private void createBiomeSources(long worldSeed) {
        BiomeSource source = new OverworldBiomeSource(MCVersion.v1_16_4, worldSeed);
        BiomeSource source1 =  new NetherBiomeSource(MCVersion.v1_16_4, worldSeed);
        BiomeSource source2 = new EndBiomeSource(MCVersion.v1_16_4, worldSeed);
    }

    public BiomeSource getBiomeSource(Dimension dimension) {
        BiomeSource source = null;

        switch(dimension){
            case OVERWORLD:
                    source = new OverworldBiomeSource(MCVersion.v1_16_4, worldSeed);
                break;
            case NETHER:
                source = new NetherBiomeSource(MCVersion.v1_16_4, worldSeed);
                break;
            case END:
                source = new EndBiomeSource(MCVersion.v1_16_4, worldSeed);
                break;
            default:
                System.out.println("USE OVERWORLD, NETHER, OR END");
                break;
        }

        return source;
    }
}

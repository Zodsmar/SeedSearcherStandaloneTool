package sassa.util;

import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mcfeature.GenerationContext;
import sassa.Main;
import sassa.enums.WorldType;

public class BiomeSources {
    private long worldSeed;
    private OverworldBiomeSource overworldBiomeSource;
    private NetherBiomeSource netherBiomeSource;
    private EndBiomeSource endBiomeSource;

    public BiomeSources(long worldSeed) {
        this.worldSeed = worldSeed;

        GenerationContext.Context contextO = GenerationContext.getContext(worldSeed, Dimension.OVERWORLD, Main.defaultModel.getSelectedVersion());
        overworldBiomeSource = (OverworldBiomeSource) contextO.getBiomeSource();
        if (Main.defaultModel.getWorldType() == WorldType.LARGE_BIOMES) {
            overworldBiomeSource.biomeSize = 6;
        }
        GenerationContext.Context contextN = GenerationContext.getContext(worldSeed, Dimension.NETHER, Main.defaultModel.getSelectedVersion());
        netherBiomeSource = (NetherBiomeSource) contextN.getBiomeSource();

        GenerationContext.Context contextE = GenerationContext.getContext(worldSeed, Dimension.END, Main.defaultModel.getSelectedVersion());
        endBiomeSource = (EndBiomeSource) contextE.getBiomeSource();

    }

    public OverworldBiomeSource getOverworldBiomeSource() {
        return overworldBiomeSource;
    }

    public NetherBiomeSource getNetherBiomeSource() {
        return netherBiomeSource;
    }

    public EndBiomeSource getEndBiomeSource() {
        return endBiomeSource;
    }
}

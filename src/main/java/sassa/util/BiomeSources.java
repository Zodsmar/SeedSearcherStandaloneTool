package sassa.util;

import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mcfeature.GenerationContext;
import com.seedfinding.mcterrain.terrain.EndTerrainGenerator;
import com.seedfinding.mcterrain.terrain.NetherTerrainGenerator;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import sassa.Main;
import sassa.enums.WorldType;
import sassa.models.Searcher_Model;

public class BiomeSources {


    private long worldSeed;
    private OverworldBiomeSource overworldBiomeSource;
    private NetherBiomeSource netherBiomeSource;
    private EndBiomeSource endBiomeSource;


    private OverworldTerrainGenerator overworldTerrainGenerator;
    private NetherTerrainGenerator netherTerrainGenerator;
    private EndTerrainGenerator endTerrainGenerator;

    public BiomeSources(long worldSeed) {
        this.worldSeed = worldSeed;

        //TODO dont generate everything like this. It is an expensive call
        GenerationContext.Context contextO = GenerationContext.getContext(worldSeed, Dimension.OVERWORLD, Main.defaultModel.getSelectedVersion());
        overworldBiomeSource = (OverworldBiomeSource) contextO.getBiomeSource();
        overworldTerrainGenerator = (OverworldTerrainGenerator) contextO.getGenerator();
        if (Main.defaultModel.getWorldType() == WorldType.LARGE_BIOMES) {
            overworldBiomeSource.biomeSize = 6;
        }
        GenerationContext.Context contextN = GenerationContext.getContext(worldSeed, Dimension.NETHER, Main.defaultModel.getSelectedVersion());
        netherTerrainGenerator = (NetherTerrainGenerator) contextN.getGenerator();
        netherBiomeSource = (NetherBiomeSource) contextN.getBiomeSource();

        GenerationContext.Context contextE = GenerationContext.getContext(worldSeed, Dimension.END, Main.defaultModel.getSelectedVersion());
        endTerrainGenerator = (EndTerrainGenerator) contextE.getGenerator();
        endBiomeSource = (EndBiomeSource) contextE.getBiomeSource();

    }

    public BiomeSources(long worldSeed, Searcher_Model model) {
        this.worldSeed = worldSeed;

        GenerationContext.Context contextO = GenerationContext.getContext(worldSeed, Dimension.OVERWORLD, model.getSelectedVersion());
        overworldBiomeSource = (OverworldBiomeSource) contextO.getBiomeSource();
        overworldTerrainGenerator = (OverworldTerrainGenerator) contextO.getGenerator();
        if (model.getWorldType() == WorldType.LARGE_BIOMES) {
            overworldBiomeSource.biomeSize = 6;
        }
        GenerationContext.Context contextN = GenerationContext.getContext(worldSeed, Dimension.NETHER, model.getSelectedVersion());
        netherTerrainGenerator = (NetherTerrainGenerator) contextN.getGenerator();
        netherBiomeSource = (NetherBiomeSource) contextN.getBiomeSource();

        GenerationContext.Context contextE = GenerationContext.getContext(worldSeed, Dimension.END, model.getSelectedVersion());
        endTerrainGenerator = (EndTerrainGenerator) contextE.getGenerator();
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

    public OverworldTerrainGenerator getOverworldTerrainGenerator() {
        return overworldTerrainGenerator;
    }

    public NetherTerrainGenerator getNetherTerrainGenerator() {
        return netherTerrainGenerator;
    }

    public EndTerrainGenerator getEndTerrainGenerator() {
        return endTerrainGenerator;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

}

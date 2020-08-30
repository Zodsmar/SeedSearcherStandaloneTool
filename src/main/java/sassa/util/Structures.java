package sassa.util;

import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.HashMap;
import java.util.Map;

public class Structures {

    public static Map<String, StructureProvider> STRUCTURE = new HashMap<>();

    static {
        //TODO: Mineshafts and Strongholds
        STRUCTURE.put(Structure.getName(Village.class), new StructureProvider(Village::new, MCVersion.v1_8, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(SwampHut.class), new StructureProvider(SwampHut::new, MCVersion.v1_8, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(Shipwreck.class), new StructureProvider(Shipwreck::new, MCVersion.v1_13, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(RuinedPortal.class) + "_overworld", new StructureProvider(RuinedPortal::new, MCVersion.v1_16, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(RuinedPortal.class) + "_nether", new StructureProvider(RuinedPortal::new, MCVersion.v1_16, Dimension.NETHER, 1));
        STRUCTURE.put(Structure.getName(PillagerOutpost.class), new StructureProvider(PillagerOutpost::new, MCVersion.v1_14, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(OceanRuin.class), new StructureProvider(OceanRuin::new, MCVersion.v1_13, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(NetherFossil.class), new StructureProvider(NetherFossil::new, MCVersion.v1_16, Dimension.NETHER, 1));
        STRUCTURE.put(Structure.getName(Monument.class), new StructureProvider(Monument::new, MCVersion.v1_8, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(Mansion.class), new StructureProvider(Mansion::new, MCVersion.v1_11, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(JunglePyramid.class), new StructureProvider(JunglePyramid::new, MCVersion.v1_8, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(Igloo.class), new StructureProvider(Igloo::new, MCVersion.v1_9, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(Fortress.class), new StructureProvider(Fortress::new, MCVersion.v1_8, Dimension.NETHER, 1));
        STRUCTURE.put(Structure.getName(EndCity.class), new StructureProvider(EndCity::new, MCVersion.v1_8, Dimension.END, 1));
        STRUCTURE.put(Structure.getName(DesertPyramid.class), new StructureProvider(DesertPyramid::new, MCVersion.v1_8, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(BuriedTreasure.class), new StructureProvider(BuriedTreasure::new, MCVersion.v1_13, Dimension.OVERWORLD, 1));
        STRUCTURE.put(Structure.getName(BastionRemnant.class), new StructureProvider(BastionRemnant::new, MCVersion.v1_16, Dimension.NETHER, 1));
    }
}

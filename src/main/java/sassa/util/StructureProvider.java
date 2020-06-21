package sassa.util;

import com.sun.jdi.InterfaceType;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.MCVersion;
import sun.security.krb5.internal.crypto.Des;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StructureProvider {

    StructureSupplier structureSupplier;
    MCVersion version;
    String dimension;

    public StructureProvider(StructureSupplier structureSupplier, MCVersion version, String dimension){
        this.structureSupplier = structureSupplier;
        this.version = version;
        this.dimension = dimension;
    }

    public StructureSupplier getStructureSupplier() {
        return this.structureSupplier;
    }

    public MCVersion getVersion() {
        return this.version;
    }

    public String getDimension() {
        return this.dimension;
    }

    public interface StructureSupplier {
        RegionStructure<?, ?> create(MCVersion version);
    }

}

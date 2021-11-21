package sassa.util;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcfeature.structure.Structure;
import com.seedfinding.mcterrain.TerrainGenerator;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StructureHelper {

    public static Stream<BPos> getClosest(Structure<?, ?> structure, BPos currentPos, long worldseed, ChunkRand chunkRand, BiomeSource source, TerrainGenerator terrainGenerator, int dimCoeff) {
        if (structure instanceof RegionStructure<?, ?>) {
            RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>) structure;
            int chunkInRegion = regionStructure.getSpacing();
            int regionSize = chunkInRegion * 16;
            RPos centerRPos = currentPos.toRegionPos(regionSize);
            SpiralIterator spiral = new SpiralIterator(centerRPos, regionSize);

            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(spiral.iterator(), Spliterator.ORDERED), false)
                    .map(rPos -> StructureHelper.getInRegion(regionStructure, worldseed, chunkRand, rPos))
                    .filter(Objects::nonNull) // remove for methods like bastion that use a float and is not in each region
                    .filter(cPos -> StructureHelper.canSpawn(regionStructure, cPos, source))
                    .filter(cPos -> terrainGenerator == null || StructureHelper.canGenerate(regionStructure, cPos, terrainGenerator))
                    .map(cPos -> {
                        BPos dimPos = cPos.toBlockPos().add(9, 0, 9);
                        return new BPos(dimPos.getX() << dimCoeff, 0, dimPos.getZ() << dimCoeff);
                    });
        } else {
            if (structure instanceof Stronghold) {
                return StreamSupport.stream(Spliterators.spliterator(((Stronghold) structure).getAllStarts(source, chunkRand), Spliterator.ORDERED), false)
                        .map(cPos -> {
                            BPos dimPos = ((CPos) cPos).toBlockPos().add(9, 0, 9);
                            return new BPos(dimPos.getX() << dimCoeff, 0, dimPos.getZ() << dimCoeff);
                        });
            }
        }
        return null;
    }

    public static CPos getInRegion(RegionStructure<?, ?> structure, long worldseed, ChunkRand chunkRand, RPos rPos) {
        return structure.getInRegion(worldseed, rPos.getX(), rPos.getZ(), chunkRand);
    }

    public static boolean canSpawn(RegionStructure<?, ?> structure, CPos cPos, BiomeSource source) {
        return structure.canSpawn(cPos.getX(), cPos.getZ(), source);
    }

    public static boolean canGenerate(RegionStructure<?, ?> structure, CPos cPos, TerrainGenerator source) {
        return structure.canGenerate(cPos.getX(), cPos.getZ(), source);
    }

    public static class SpiralIterator implements Iterable<RPos> {
        private final RPos lowerBound;
        private final RPos upperBound;
        private RPos currentPos;
        private int currentLength = 1;
        private int currentLengthPos = 0;
        private DIRECTION currentDirection = DIRECTION.NORTH;

        public SpiralIterator(RPos currentRPos, RPos lowerBound, RPos upperBound) {
            this.currentPos = currentRPos;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public SpiralIterator(RPos currentRPos, int regionSize) {
            this(currentRPos,
                    new BPos(-30_000_000, 0, -30_000_000).toRegionPos(regionSize),
                    new BPos(30_000_000, 0, 30_000_000).toRegionPos(regionSize)
            );
        }

        public SpiralIterator(RPos currentRPos) {
            this(currentRPos, 32 * 16);
        }

        @Override
        public Iterator<RPos> iterator() {
            return new Iterator<RPos>() {

                @Override
                public boolean hasNext() {
                    return currentPos.getZ() <= upperBound.getZ() && currentPos.getZ() >= lowerBound.getZ() && currentPos.getX() <= upperBound.getX() && currentPos.getX() >= lowerBound.getX();
                }

                @Override
                public RPos next() {
                    RPos result = currentPos;
                    if (isAlmostOutside()) {
                        // if we are at one of the border we know that we already turned before coming here
                        // finish current loop and go full outside the range
                        for (; currentLengthPos < currentLength; currentLengthPos++) {
                            this.update();
                        }
                        int counter = 0;
                        boolean keepRunning = true;
                        while (!this.hasNext() && counter < 4 && keepRunning) {
                            // if we reach the max on that length
                            if (currentLengthPos == currentLength) {
                                counter++;
                                // if it is a direction on which we need to make an extra step
                                if (currentDirection == DIRECTION.EAST || currentDirection == DIRECTION.WEST) {
                                    currentLength += 1;
                                }
                                // switch to next direction
                                currentDirection = currentDirection.next();
                                // reset counter for this direction
                                currentLengthPos = 0;
                            } else {
                                throw new UnsupportedOperationException();
                            }
                            // skip that entire section since it is out for the previous direction
                            for (; currentLengthPos < currentLength; currentLengthPos++) {
                                this.update();
                                if (this.hasNext()) {
                                    keepRunning = false;
                                    currentLengthPos += 1;
                                    break;
                                }
                            }
                        }
                        return result;
                    }
                    // update on that direction
                    this.update();
                    currentLengthPos += 1;

                    // if we reach the max on that length
                    if (currentLengthPos == currentLength) {
                        // if it is a direction on which we need to make an extra step
                        if (currentDirection == DIRECTION.EAST || currentDirection == DIRECTION.WEST) {
                            currentLength += 1;
                        }
                        // switch to next direction
                        currentDirection = currentDirection.next();
                        // reset counter for this direction
                        currentLengthPos = 0;
                    } else if (currentLengthPos > currentLength) {
                        throw new UnsupportedOperationException();
                    }
                    return result;
                }

                public void update() {
                    switch (currentDirection) {
                        case NORTH:
                            currentPos = new RPos(currentPos.getX(), currentPos.getZ() - 1, currentPos.getRegionSize());
                            break;
                        case EAST:
                            currentPos = new RPos(currentPos.getX() + 1, currentPos.getZ(), currentPos.getRegionSize());
                            break;
                        case SOUTH:
                            currentPos = new RPos(currentPos.getX(), currentPos.getZ() + 1, currentPos.getRegionSize());
                            break;
                        case WEST:
                            currentPos = new RPos(currentPos.getX() - 1, currentPos.getZ(), currentPos.getRegionSize());
                            break;
                    }
                }

                public void backtrack() {
                    switch (currentDirection) {
                        case NORTH:
                            currentPos = new RPos(currentPos.getX(), currentPos.getZ() + 1, currentPos.getRegionSize());
                            break;
                        case EAST:
                            currentPos = new RPos(currentPos.getX() - 1, currentPos.getZ(), currentPos.getRegionSize());
                            break;
                        case SOUTH:
                            currentPos = new RPos(currentPos.getX(), currentPos.getZ() - 1, currentPos.getRegionSize());
                            break;
                        case WEST:
                            currentPos = new RPos(currentPos.getX() + 1, currentPos.getZ(), currentPos.getRegionSize());
                            break;
                    }
                }

                public boolean isAlmostOutside() {
                    switch (currentDirection) {
                        case NORTH:
                            return currentPos.getZ() <= lowerBound.getZ();
                        case EAST:
                            return currentPos.getX() >= upperBound.getX();
                        case SOUTH:
                            return currentPos.getZ() >= upperBound.getZ();
                        case WEST:
                            return currentPos.getX() <= lowerBound.getX();
                    }
                    return false;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public enum DIRECTION {
            NORTH,
            EAST,
            SOUTH,
            WEST,
            NONE;

            public DIRECTION next() {
                switch (this) {
                    case NORTH:
                        return EAST;
                    case EAST:
                        return SOUTH;
                    case SOUTH:
                        return WEST;
                    case WEST:
                        return NORTH;
                }
                return NONE;
            }

            public DIRECTION opposite() {
                switch (this) {
                    case NORTH:
                        return SOUTH;
                    case EAST:
                        return WEST;
                    case SOUTH:
                        return NORTH;
                    case WEST:
                        return EAST;
                }
                return NONE;
            }
        }
    }
}
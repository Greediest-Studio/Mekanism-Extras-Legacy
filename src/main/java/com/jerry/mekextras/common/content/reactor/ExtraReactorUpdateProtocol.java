package com.jerry.mekextras.common.content.reactor;

import com.jerry.mekextras.common.ExtraMultiblockManagers;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.api.Coord4D;
import mekanism.common.multiblock.MultiblockCache;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.multiblock.UpdateProtocol;
import mekanism.generators.common.tile.reactor.TileEntityReactorGlass;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * Builds the Mekanism Extras naquadah reactor. The reference chamber is a fixed 9x9x9
 * cuboid with its controller at the center of the top face. Some reference wall cells are
 * intentionally allowed to be absent; the old 1.12 scanner normally treats those cells as
 * a broken cuboid, so they are represented here as virtual shell positions.
 */
public class ExtraReactorUpdateProtocol extends UpdateProtocol<SynchronizedExtraReactorData> {

    private static final int REACTOR_SIZE = 9;
    private static final byte[][] REFERENCE_WALL_GRID = {
          {0, 0, 0, 1, 1, 1, 0, 0, 0},
          {0, 1, 1, 2, 2, 2, 1, 1, 0},
          {0, 1, 2, 2, 2, 2, 2, 1, 0},
          {1, 2, 2, 2, 2, 2, 2, 2, 1},
          {1, 2, 2, 2, 2, 2, 2, 2, 1},
          {1, 2, 2, 2, 2, 2, 2, 2, 1},
          {0, 1, 2, 2, 2, 2, 2, 1, 0},
          {0, 1, 1, 2, 2, 2, 1, 1, 0},
          {0, 0, 0, 1, 1, 1, 0, 0, 0}
    };

    public ExtraReactorUpdateProtocol(TileEntityExtraReactorCasing tileEntity) {
        super(tileEntity);
    }

    @Override
    protected boolean isValidFrame(int x, int y, int z) {
        return isAcceptedShellTile(getTile(x, y, z)) || isVirtualControllerShellCell(x, y, z);
    }

    @Override
    public boolean isViableNode(int x, int y, int z) {
        TileEntity tile = getTile(x, y, z);
        return tile instanceof TileEntityReactorGlass || MultiblockManager.areEqual(tile, pointer)
              || isVirtualControllerShellCell(x, y, z);
    }

    @Override
    public boolean isValidInnerNode(int x, int y, int z) {
        if (super.isValidInnerNode(x, y, z)) {
            return true;
        }
        TileEntity tile = getTile(x, y, z);
        return tile instanceof TileEntityExtraReactorCasing && !(tile instanceof TileEntityExtraReactorController);
    }

    @Override
    public void doUpdate() {
        if (pointer.structure != null) {
            if (isFormedStructureValid(pointer.structure)) {
                return;
            }
            SynchronizedExtraReactorData stale = pointer.structure;
            structureFound = stale;
            pauseSourceStructures(stale);
            structureFound = null;
            iteratedNodes.clear();
            innerNodes.clear();
        }
        Coord4D controller = findControllerAroundPointer();
        if (controller == null) {
            return;
        }
        buildFixedStructure(controller);
    }

    private void buildFixedStructure(Coord4D controller) {
        StructureScan scan = scanFixedStructure(controller);
        if (scan == null) {
            return;
        }
        SynchronizedExtraReactorData structure = getNewStructure();
        structure.locations = scan.locations;
        structure.volLength = REACTOR_SIZE;
        structure.volHeight = REACTOR_SIZE;
        structure.volWidth = REACTOR_SIZE;
        structure.volume = REACTOR_SIZE * REACTOR_SIZE * REACTOR_SIZE;
        structure.minLocation = scan.min;
        structure.maxLocation = scan.max;
        structure.renderLocation = controller;
        structureFound = structure;
        iteratedNodes.clear();
        iteratedNodes.addAll(scan.locations);
        iteratedNodes.addAll(scan.inner);
        innerNodes.clear();
        innerNodes.addAll(scan.inner);
        pointer.reportCacheFormationFailure(null);
        formFoundStructure(null, null, java.util.Collections.emptySet());
    }

    private boolean isFormedStructureValid(SynchronizedExtraReactorData structure) {
        if (structure == null || structure.destroyed || !structure.isFormed()) {
            return false;
        }
        if (structure.volLength != REACTOR_SIZE || structure.volHeight != REACTOR_SIZE ||
              structure.volWidth != REACTOR_SIZE || structure.minLocation == null || structure.maxLocation == null) {
            return false;
        }
        Coord4D controller = new Coord4D((structure.minLocation.x + structure.maxLocation.x) / 2,
              structure.maxLocation.y, (structure.minLocation.z + structure.maxLocation.z) / 2,
              pointer.getWorld().provider.getDimension());
        return scanFixedStructure(controller) != null;
    }

    private StructureScan scanFixedStructure(Coord4D controller) {
        if (!(controller.getTileEntity(pointer.getWorld()) instanceof TileEntityExtraReactorController)) {
            return null;
        }
        Coord4D min = new Coord4D(controller.x - 4, controller.y - 8, controller.z - 4,
              pointer.getWorld().provider.getDimension());
        Coord4D max = new Coord4D(controller.x + 4, controller.y, controller.z + 4,
              pointer.getWorld().provider.getDimension());
        java.util.HashSet<Coord4D> locations = new java.util.HashSet<>();
        java.util.HashSet<Coord4D> inner = new java.util.HashSet<>();
        for (int y = 0; y < REACTOR_SIZE; y++) {
            for (int x = 0; x < REACTOR_SIZE; x++) {
                for (int z = 0; z < REACTOR_SIZE; z++) {
                    Coord4D coord = min.translate(x, y, z);
                    TileEntity tile = coord.getTileEntity(pointer.getWorld());
                    boolean xWall = x == 0 || x == REACTOR_SIZE - 1;
                    boolean yWall = y == 0 || y == REACTOR_SIZE - 1;
                    boolean zWall = z == 0 || z == REACTOR_SIZE - 1;
                    if (!xWall && !yWall && !zWall) {
                        if (tile == null) {
                            continue;
                        }
                        if (!(tile instanceof TileEntityExtraReactorCasing) ||
                              tile instanceof TileEntityExtraReactorController) {
                            return null;
                        }
                        inner.add(coord);
                        continue;
                    }
                    int requirement;
                    if (xWall) {
                        requirement = REFERENCE_WALL_GRID[z][y];
                    } else if (yWall) {
                        requirement = REFERENCE_WALL_GRID[x][z];
                    } else {
                        requirement = REFERENCE_WALL_GRID[x][y];
                    }
                    if (tile == null) {
                        if (requirement != 0) {
                            return null;
                        }
                        continue;
                    }
                    if (!isAcceptedShellTile(tile)) {
                        return null;
                    }
                    if (tile instanceof TileEntityExtraReactorController && !coord.equals(controller)) {
                        return null;
                    }
                    if (requirement == 1 && tile.getClass() != TileEntityExtraReactorCasing.class) {
                        return null;
                    }
                    locations.add(coord);
                }
            }
        }
        return new StructureScan(min, max, locations, inner);
    }

    @Override
    protected boolean canForm(SynchronizedExtraReactorData structure) {
        if (structure.volLength != REACTOR_SIZE || structure.volHeight != REACTOR_SIZE ||
              structure.volWidth != REACTOR_SIZE || structure.minLocation == null || structure.maxLocation == null) {
            return false;
        }
        Coord4D controller = new Coord4D((structure.minLocation.x + structure.maxLocation.x) / 2,
              structure.maxLocation.y, (structure.minLocation.z + structure.maxLocation.z) / 2,
              pointer.getWorld().provider.getDimension());
        return scanFixedStructure(controller) != null;
    }

    private static final class StructureScan {

        private final Coord4D min;
        private final Coord4D max;
        private final java.util.Set<Coord4D> locations;
        private final java.util.Set<Coord4D> inner;

        private StructureScan(Coord4D min, Coord4D max, java.util.Set<Coord4D> locations,
                              java.util.Set<Coord4D> inner) {
            this.min = min;
            this.max = max;
            this.locations = locations;
            this.inner = inner;
        }
    }

    private boolean isVirtualControllerShellCell(int x, int y, int z) {
        Coord4D controller = findControllerAroundPointer();
        if (controller == null) {
            return false;
        }
        Coord4D min = new Coord4D(controller.x - 4, controller.y - 8, controller.z - 4, pointer.getWorld().provider.getDimension());
        return isIgnoredAirCell(x, y, z, min, controller);
    }

    private Coord4D findControllerAroundPointer() {
        if (pointer instanceof TileEntityExtraReactorController) {
            return Coord4D.get(pointer);
        }
        int baseX = pointer.getPos().getX();
        int baseY = pointer.getPos().getY();
        int baseZ = pointer.getPos().getZ();
        for (int dy = 0; dy <= 8; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    TileEntity tile = getTile(baseX + dx, baseY + dy, baseZ + dz);
                    if (tile instanceof TileEntityExtraReactorController) {
                        return Coord4D.get(tile);
                    }
                }
            }
        }
        return null;
    }

    private boolean isIgnoredAirCell(int x, int y, int z, Coord4D min, Coord4D max) {
        if (x < min.x || x > max.x || y < min.y || y > max.y || z < min.z || z > max.z
              || !pointer.getWorld().isAirBlock(new net.minecraft.util.math.BlockPos(x, y, z))) {
            return false;
        }
        int relativeX = x - min.x;
        int relativeY = y - min.y;
        int relativeZ = z - min.z;
        boolean xWall = relativeX == 0 || relativeX == REACTOR_SIZE - 1;
        boolean yWall = relativeY == 0 || relativeY == REACTOR_SIZE - 1;
        boolean zWall = relativeZ == 0 || relativeZ == REACTOR_SIZE - 1;
        if (xWall) {
            return REFERENCE_WALL_GRID[relativeZ][relativeY] == 0;
        } else if (yWall) {
            return REFERENCE_WALL_GRID[relativeX][relativeZ] == 0;
        } else if (zWall) {
            return REFERENCE_WALL_GRID[relativeX][relativeY] == 0;
        }
        return false;
    }

    private TileEntity getTile(int x, int y, int z) {
        return new Coord4D(x, y, z, pointer.getWorld().provider.getDimension()).getTileEntity(pointer.getWorld());
    }

    private static boolean isAcceptedShellTile(TileEntity tile) {
        return tile instanceof TileEntityReactorGlass || tile instanceof TileEntityExtraReactorCasing;
    }

    @Override
    protected ExtraReactorCache getNewCache() {
        return new ExtraReactorCache();
    }

    @Override
    protected SynchronizedExtraReactorData getNewStructure() {
        return new SynchronizedExtraReactorData();
    }

    @Override
    protected MultiblockManager<SynchronizedExtraReactorData> getManager() {
        return ExtraMultiblockManagers.reactorManager;
    }

    @Override
    protected void mergeCaches(List<ItemStack> rejectedItems, MultiblockCache<SynchronizedExtraReactorData> cache,
                               MultiblockCache<SynchronizedExtraReactorData> merge) {
        ExtraReactorCache first = (ExtraReactorCache) cache;
        ExtraReactorCache second = (ExtraReactorCache) merge;
        List<ItemStack> rejects = mekanism.common.util.StackUtils.getMergeRejects(
              first.getInventorySlots(null), second.getInventorySlots(null));
        if (!rejects.isEmpty()) {
            rejectedItems.addAll(rejects);
        }
        mekanism.common.util.StackUtils.merge(first.getInventorySlots(null), second.getInventorySlots(null));
        first.richNaquadahFuel = mergeGasStack(first.richNaquadahFuel, second.richNaquadahFuel);
        first.fuel = mergeGasStack(first.fuel, second.fuel);
        first.richUraniumFuel = mergeGasStack(first.richUraniumFuel, second.richUraniumFuel);
        first.steam = mergeGasStack(first.steam, second.steam);
        first.water = mergeFluidStack(first.water, second.water);
        first.energy = Math.min(SynchronizedExtraReactorData.ENERGY_CAPACITY, first.energy + second.energy);
        first.laserBuffer += second.laserBuffer;
        first.plasmaTemperature = Math.min(SynchronizedExtraReactorData.MAX_PLASMA_TEMPERATURE,
              first.plasmaTemperature + second.plasmaTemperature);
        first.laserCharges = Math.min(64, first.laserCharges + second.laserCharges);
        first.burning |= second.burning;
        first.caseTemperature = Math.max(first.caseTemperature, second.caseTemperature);
    }
}

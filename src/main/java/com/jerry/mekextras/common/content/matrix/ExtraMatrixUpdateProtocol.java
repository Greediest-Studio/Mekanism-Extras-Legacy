package com.jerry.mekextras.common.content.matrix;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import mekanism.common.multiblock.MultiblockCache;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.multiblock.UpdateProtocol;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCell;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionProvider;
import mekanism.common.util.StackUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ExtraMatrixUpdateProtocol extends UpdateProtocol<SynchronizedExtraMatrixData> {

    public ExtraMatrixUpdateProtocol(TileEntityExtraInductionCasing tileEntity) {
        super(tileEntity);
    }

    @Override
    protected boolean isValidFrame(int x, int y, int z) {
        IBlockState state = pointer.getWorld().getBlockState(new BlockPos(x, y, z));
        return state.getBlock() == ExtraBlocks.REINFORCED_INDUCTION_CASING;
    }

    @Override
    public boolean isValidInnerNode(int x, int y, int z) {
        if (super.isValidInnerNode(x, y, z)) {
            return true;
        }
        TileEntity tile = new Coord4D(x, y, z, pointer.getWorld().provider.getDimension()).getTileEntity(pointer.getWorld());
        return tile instanceof TileEntityExtraInductionCell || tile instanceof TileEntityExtraInductionProvider;
    }

    @Override
    protected ExtraMatrixCache getNewCache() {
        return new ExtraMatrixCache();
    }

    @Override
    protected SynchronizedExtraMatrixData getNewStructure() {
        return new SynchronizedExtraMatrixData((TileEntityExtraInductionCasing) pointer);
    }

    @Override
    protected MultiblockManager<SynchronizedExtraMatrixData> getManager() {
        return com.jerry.mekextras.common.ExtraMultiblockManagers.matrixManager;
    }

    @Override
    protected void mergeCaches(List<ItemStack> rejectedItems, MultiblockCache<SynchronizedExtraMatrixData> cache, MultiblockCache<SynchronizedExtraMatrixData> merge) {
        ExtraMatrixCache matrixCache = (ExtraMatrixCache) cache;
        ExtraMatrixCache mergeCache = (ExtraMatrixCache) merge;
        List<ItemStack> rejects = StackUtils.getMergeRejects(matrixCache.getInventorySlots(null), mergeCache.getInventorySlots(null));
        if (!rejects.isEmpty()) {
            rejectedItems.addAll(rejects);
        }
        StackUtils.merge(matrixCache.getInventorySlots(null), mergeCache.getInventorySlots(null));
    }

    @Override
    protected void onStructureDestroyed(SynchronizedExtraMatrixData structure) {
        //Save all energy changes before destroying the structure
        structure.flushEnergy();
        super.onStructureDestroyed(structure);
    }

    @Override
    protected boolean canForm(SynchronizedExtraMatrixData structure) {
        for (Coord4D coord : innerNodes) {
            TileEntity tile = coord.getTileEntity(pointer.getWorld());
            if (tile instanceof TileEntityExtraInductionCell cell) {
                structure.addCell(coord, cell);
            } else if (tile instanceof TileEntityExtraInductionProvider provider) {
                structure.addProvider(coord, provider);
            }
        }
        return true;
    }
}

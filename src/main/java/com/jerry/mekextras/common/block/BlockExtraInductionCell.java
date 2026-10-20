package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ICTier;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCell;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * A single induction matrix cell of one of the extra tiers.
 */
public class BlockExtraInductionCell extends BlockExtraMachine {

    private final ICTier tier;

    public BlockExtraInductionCell(ICTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public ICTier getTier() {
        return tier;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityExtraInductionCell cell = new TileEntityExtraInductionCell();
        cell.tier = tier;
        return cell;
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getInductionCellBlock(advancedTier);
    }
}

package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.FTTier;
import com.jerry.mekextras.common.tile.TileEntityExtraFluidTank;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockExtraFluidTank extends BlockExtraMachine {

    private static final net.minecraft.util.math.AxisAlignedBB TANK_BOUNDS = new net.minecraft.util.math.AxisAlignedBB(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);

    private final FTTier tier;

    public BlockExtraFluidTank(FTTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public FTTier getTier() {
        return tier;
    }

    @Override
    @Deprecated
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(net.minecraft.block.state.IBlockState state, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return TANK_BOUNDS;
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.FLUID_TANK;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityExtraFluidTank(tier);
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getFluidTankBlock(advancedTier);
    }
}


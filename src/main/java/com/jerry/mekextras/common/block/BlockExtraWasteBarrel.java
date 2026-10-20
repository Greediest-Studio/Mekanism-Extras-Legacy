package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.RWBTier;
import com.jerry.mekextras.common.tile.TileEntityLargeCapRadioactiveWasteBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockExtraWasteBarrel extends BlockExtraMachine {

    private static final AxisAlignedBB BARREL_BOUNDS = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    private final RWBTier tier;

    public BlockExtraWasteBarrel(RWBTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public RWBTier getTier() {
        return tier;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return TileEntityLargeCapRadioactiveWasteBarrel.create(tier);
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getWasteBarrelBlock(advancedTier);
    }
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BARREL_BOUNDS;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BARREL_BOUNDS;
    }
}
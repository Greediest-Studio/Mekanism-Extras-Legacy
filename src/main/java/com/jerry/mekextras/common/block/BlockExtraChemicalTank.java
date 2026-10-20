package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.CTTier;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockExtraChemicalTank extends BlockExtraMachine {

    private static final net.minecraft.util.math.AxisAlignedBB TANK_BOUNDS = new net.minecraft.util.math.AxisAlignedBB(0.1875F, 0.0F, 0.1875F, 0.8125F, 1.0F, 0.8125F);

    private final CTTier tier;

    public BlockExtraChemicalTank(CTTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public CTTier getTier() {
        return tier;
    }

    @Override
    @Deprecated
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(net.minecraft.block.state.IBlockState state, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return TANK_BOUNDS;
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.CHEMICAL_TANK;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityExtraChemicalTank(tier);
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getChemicalTankBlock(advancedTier);
    }
}


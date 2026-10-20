package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ECTier;
import com.jerry.mekextras.common.tile.TileEntityExtraEnergyCube;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockExtraEnergyCube extends BlockExtraMachine {

    private final ECTier tier;

    public BlockExtraEnergyCube(ECTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public ECTier getTier() {
        return tier;
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.ENERGY_CUBE;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityExtraEnergyCube(tier);
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getEnergyCubeBlock(advancedTier);
    }
}


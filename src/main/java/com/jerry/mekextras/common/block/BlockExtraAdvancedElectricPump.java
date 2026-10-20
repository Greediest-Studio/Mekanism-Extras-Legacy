package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * The advanced electric pump: a pump with a much larger internal tank, a higher output rate and support for the ionic
 * membrane upgrade that turns hexafluoride fluids into their rich fuel variants.
 */
public class BlockExtraAdvancedElectricPump extends BlockExtraMachine {

    public BlockExtraAdvancedElectricPump() {
        super(Material.IRON);
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.ADVANCED_ELECTRIC_PUMP;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAdvancedElectricPump();
    }
}

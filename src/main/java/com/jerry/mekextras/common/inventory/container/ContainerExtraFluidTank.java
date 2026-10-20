package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.ContainerFluidStorage;
import com.jerry.mekextras.common.tile.TileEntityExtraFluidTank;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraFluidTank extends ContainerFluidStorage<TileEntityExtraFluidTank> {

    public ContainerExtraFluidTank(InventoryPlayer inventory, TileEntityExtraFluidTank tile) {
        super(tile, inventory);
    }

}


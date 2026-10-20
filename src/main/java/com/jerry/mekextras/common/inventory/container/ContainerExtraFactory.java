package com.jerry.mekextras.common.inventory.container;

import com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory;
import mekanism.common.inventory.container.MekanismTileContainer;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraFactory extends MekanismTileContainer<TileEntityExtraFactory> {

    public ContainerExtraFactory(InventoryPlayer inventory, TileEntityExtraFactory tile) {
        super(tile, inventory);
    }


    @Override
    protected int getInventoryYOffset() {
        return tile.getPlayerInventoryYOffset();
    }

    @Override
    protected int getInventoryXOffset() {
        return tile.getPlayerInventoryXOffset();
    }
}

package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalWasher;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraChemicalWasher extends MekanismTileContainer<TileEntityExtraChemicalWasher> {

    public ContainerExtraChemicalWasher(InventoryPlayer inventory, TileEntityExtraChemicalWasher tile) {
        super(tile, inventory);
    }
}

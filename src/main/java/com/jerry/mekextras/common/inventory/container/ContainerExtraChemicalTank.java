package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraChemicalTank extends MekanismTileContainer<TileEntityExtraChemicalTank> {

    public ContainerExtraChemicalTank(InventoryPlayer inventory, TileEntityExtraChemicalTank tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        super.addSlots();
        addArmorSlots(inv, -20, 67, 0);
    }
}


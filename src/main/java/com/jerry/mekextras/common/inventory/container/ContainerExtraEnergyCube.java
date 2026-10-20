package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.ContainerEnergyStorage;
import com.jerry.mekextras.common.tile.TileEntityExtraEnergyCube;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraEnergyCube extends ContainerEnergyStorage<TileEntityExtraEnergyCube> {

    public ContainerExtraEnergyCube(InventoryPlayer inventory, TileEntityExtraEnergyCube tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        super.addSlots();
        addArmorSlots(inv, 180, 41, 0);
    }
}


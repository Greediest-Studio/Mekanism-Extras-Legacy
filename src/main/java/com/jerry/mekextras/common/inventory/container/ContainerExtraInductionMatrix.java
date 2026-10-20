package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.ContainerEnergyStorage;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCasing;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraInductionMatrix extends ContainerEnergyStorage<TileEntityExtraInductionCasing> {

    public ContainerExtraInductionMatrix(InventoryPlayer inventory, TileEntityExtraInductionCasing tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        super.addSlots();
        addArmorSlots(inv, -20, 41, 0);
    }
}

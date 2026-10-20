package com.jerry.mekextras.common.inventory.container;

import com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump;
import mekanism.common.inventory.container.MekanismTileContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraAdvancedElectricPump extends MekanismTileContainer<TileEntityAdvancedElectricPump> {

    public ContainerExtraAdvancedElectricPump(InventoryPlayer inventory, TileEntityAdvancedElectricPump tile) {
        super(tile, inventory);
    }
}

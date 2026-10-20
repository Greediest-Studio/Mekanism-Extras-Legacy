package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraIsotopicCentrifuge;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraIsotopicCentrifuge extends MekanismTileContainer<TileEntityExtraIsotopicCentrifuge> {

    public ContainerExtraIsotopicCentrifuge(InventoryPlayer inventory, TileEntityExtraIsotopicCentrifuge tile) {
        super(tile, inventory);
    }
}

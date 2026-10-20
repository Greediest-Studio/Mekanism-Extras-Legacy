package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraDissolutionChamber;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraDissolutionChamber extends MekanismTileContainer<TileEntityExtraDissolutionChamber> {

    public ContainerExtraDissolutionChamber(InventoryPlayer inventory, TileEntityExtraDissolutionChamber tile) {
        super(tile, inventory);
    }

    @Override
    protected int getInventoryYOffset() {
        return 100;
    }


    @Override
    protected int getInventoryXOffset() {
        return tile == null ? 8 : ExtraMachineGuiLayout.inventoryX(tile.outPutTanks.length);
    }
}

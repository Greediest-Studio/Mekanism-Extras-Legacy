package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraCrystallizer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraCrystallizer extends MekanismTileContainer<TileEntityExtraCrystallizer> {

    public ContainerExtraCrystallizer(InventoryPlayer inventory, TileEntityExtraCrystallizer tile) {
        super(tile, inventory);
    }

    @Override
    protected int getInventoryYOffset() {
        return 97;
    }

    @Override
    protected int getInventoryXOffset() {
        return tile == null ? 8 : ExtraMachineGuiLayout.inventoryX(tile.inputTanks.length);
    }
}

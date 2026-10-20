package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraNutritionalLiquifier;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraNutritionalLiquifier extends MekanismTileContainer<TileEntityExtraNutritionalLiquifier> {

    public ContainerExtraNutritionalLiquifier(InventoryPlayer inventory, TileEntityExtraNutritionalLiquifier tile) {
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

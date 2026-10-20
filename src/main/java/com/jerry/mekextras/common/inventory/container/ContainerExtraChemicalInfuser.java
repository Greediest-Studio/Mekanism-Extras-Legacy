package com.jerry.mekextras.common.inventory.container;

import mekanism.common.inventory.container.MekanismTileContainer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalInfuser;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraChemicalInfuser extends MekanismTileContainer<TileEntityExtraChemicalInfuser> {

    public ContainerExtraChemicalInfuser(InventoryPlayer inventory, TileEntityExtraChemicalInfuser tile) {
        super(tile, inventory);
    }

    @Override
    protected int getInventoryYOffset() {
        return 84;
    }
}

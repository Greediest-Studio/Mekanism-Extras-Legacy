package com.jerry.mekextras.common.inventory.container;

import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.common.inventory.container.MekanismTileContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraReactorController extends MekanismTileContainer<TileEntityExtraReactorController> {
    public ContainerExtraReactorController(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(tile, inventory);
    }
}
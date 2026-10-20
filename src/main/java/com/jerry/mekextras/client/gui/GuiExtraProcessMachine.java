package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.IntSupplier;

/** Shared sizing and background support for Extras machines with a dynamic process row. */
@SideOnly(Side.CLIENT)
public abstract class GuiExtraProcessMachine<TILE extends TileEntityContainerBlock & ISideConfiguration, CONTAINER extends Container>
      extends GuiConfigurableTile<TILE, CONTAINER> {

    private final IntSupplier processCount;

    protected GuiExtraProcessMachine(TILE tile, CONTAINER container, IntSupplier processCount) {
        this(tile, container, processCount, 16);
    }

    protected GuiExtraProcessMachine(TILE tile, CONTAINER container, IntSupplier processCount, int heightExtra) {
        super(tile, container);
        this.processCount = processCount;
        updateSize();
        ySize += heightExtra;
        dynamicSlots = true;
    }

    @Override
    public void initGui() {
        // GuiContainer recalculates guiLeft from xSize here. Reapplying the tier-derived width also handles a tier sync
        // that reached the client shortly before the GUI was first opened.
        updateSize();
        super.initGui();
    }

    protected final int processCount() {
        return processCount.getAsInt();
    }

    private void updateSize() {
        xSize = ExtraMachineGuiLayout.guiWidth(processCount.getAsInt());
    }
}
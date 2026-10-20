package com.jerry.mekextras.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import com.jerry.mekextras.common.inventory.container.ContainerExtraFluidTank;
import com.jerry.mekextras.common.tile.TileEntityExtraFluidTank;

import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.tab.GuiContainerEditModeTab;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExtraFluidTank extends GuiMekanismTile<TileEntityExtraFluidTank, ContainerExtraFluidTank> {

    public GuiExtraFluidTank(InventoryPlayer inventory, TileEntityExtraFluidTank tile) {
        super(tile, new ContainerExtraFluidTank(inventory, tile));
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addButton(GuiSideHolder.armorHolder(this));
        super.addGuiElements();
        addButton(new GuiContainerEditModeTab<>(this, tileEntity));
        addButton(new GuiFluidGauge(this, tileEntity.fluidTank, GuiFluidGauge.Type.WIDE, 48, 18));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText();
        super.drawForegroundText(mouseX, mouseY);
    }
}


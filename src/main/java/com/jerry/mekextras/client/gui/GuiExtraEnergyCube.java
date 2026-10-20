package com.jerry.mekextras.client.gui;

import mekanism.client.gui.GuiConfigurableTile;
import mekanism.common.security.ISecurityTile;
import com.jerry.mekextras.common.inventory.container.ContainerExtraEnergyCube;
import com.jerry.mekextras.common.tile.TileEntityExtraEnergyCube;

import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiExtraEnergyCube extends GuiConfigurableTile<TileEntityExtraEnergyCube, ContainerExtraEnergyCube> {

    public GuiExtraEnergyCube(InventoryPlayer inventory, TileEntityExtraEnergyCube tile) {
        super(tile, new ContainerExtraEnergyCube(inventory, tile));
        dynamicSlots = true;
    }

    @Override
    protected void addSecurityTab() {
        addSecurityTab((net.minecraft.tileentity.TileEntity & mekanism.common.security.ISecurityTile) tileEntity, 6);
    }

    @Override
    protected void addGuiElements() {
        addButton(GuiSideHolder.rightArmorHolder(this));
        super.addGuiElements();
        addButton(new GuiEnergyGauge(this, tileEntity.getEnergyContainer(), GuiEnergyGauge.Type.WIDE, 55, 18));
        addButton(new GuiEnergyTab(this, this::getEnergyTabText));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText();
        super.drawForegroundText(mouseX, mouseY);
    }

    private List<ITextComponent> getEnergyTabText() {
        List<ITextComponent> info = new ArrayList<>();
        info.add(new TextComponentString(LangUtils.localize("gui.inputRate") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getInputRate()) + "/t"));
        info.add(new TextComponentString(LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t"));
        return info;
    }
}


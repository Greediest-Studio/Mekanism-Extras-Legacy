package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.client.gui.element.tab.GuiWarningTab;
import mekanism.client.gui.warning.IWarningTracker;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiExtraReactorInfo extends GuiMekanismTile<TileEntityExtraReactorController, ContainerNull> {

    protected final InventoryPlayer inventory;

    protected GuiExtraReactorInfo(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(tile, new ContainerNull(inventory.player, tile));
        this.inventory = inventory;
        xSize += 10;
        titleLabelY = 5;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new MekanismImageButton(this, 6, 6, 14, getButtonLocation("back"), this::sendBack,
              getOnHover(() -> new TextComponentString(LangUtils.localize("gui.back")))));
        addButton(new GuiEnergyTab(this, this::getEnergyTabText));
        addButton(new GuiHeatTab(this, this::getHeatTabText));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleTextWithOffset(new TextComponentString(LangUtils.localize("gui.reactor.controllerTitle")), 18, 5, getXSize());
        super.drawForegroundText(mouseX, mouseY);
    }

    protected List<ITextComponent> getEnergyTabText() {
        if (!tileEntity.isFormed()) {
            return new ArrayList<>();
        }
        return Arrays.asList(
              new TextComponentString(LangUtils.localize("gui.storing") + ": " +
                    MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy())),
              new TextComponentString(LangUtils.localize("gui.producing") + ": " +
                    MekanismUtils.getEnergyDisplay(tileEntity.getPassiveGeneration(false, true)) + "/t"));
    }

    protected List<ITextComponent> getHeatTabText() {
        if (!tileEntity.isFormed()) {
            return new ArrayList<>();
        }
        String transfer = MekanismUtils.getTemperatureDisplay(tileEntity.getLastTransferLoss(), TemperatureUnit.KELVIN, false);
        String environment = MekanismUtils.getTemperatureDisplay(tileEntity.getLastEnvironmentLoss(), TemperatureUnit.KELVIN, false);
        return Arrays.asList(
              new TextComponentString(LangUtils.localize("gui.transferred") + ": " + transfer + "/t"),
              new TextComponentString(LangUtils.localize("gui.dissipated") + ": " + environment + "/t"));
    }

    @Override
    protected void addWarningTab(IWarningTracker warningTracker) {
        addButton(new GuiWarningTab(this, warningTracker, false));
    }

    protected void sendBack() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiExtraReactorController(inventory, tileEntity));
    }
}

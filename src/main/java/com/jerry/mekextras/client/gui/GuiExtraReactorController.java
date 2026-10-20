package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab;
import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab.ReactorTab;
import com.jerry.mekextras.common.inventory.container.ContainerExtraReactorController;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.client.gui.element.tab.GuiWarningTab;
import mekanism.client.gui.warning.IWarningTracker;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorController extends GuiMekanismTile<TileEntityExtraReactorController, ContainerExtraReactorController> {

    public GuiExtraReactorController(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(tile, new ContainerExtraReactorController(inventory, tile));
        dynamicSlots = true;
        xSize += 10;
        inventoryLabelX += 5;
        inventoryLabelY += 2;
        titleLabelY = 5;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        if (tileEntity.isFormed()) {
            addButton(new GuiEnergyTab(this, this::getEnergyTabText));
            addButton(new GuiHeatTab(this, this::getHeatTabText));
            addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.HEAT));
            addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.FUEL));
            addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.STATS));
        }
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(LangUtils.localize("gui.reactor.controllerTitle")), titleLabelY);
        renderInventoryText();
        drawScrollingString(new TextComponentString(LangUtils.localize(tileEntity.isFormed() ? "gui.formed" : "gui.incomplete")),
              0, 16, TextAlignment.LEFT, titleTextColor(), 13, false);
        super.drawForegroundText(mouseX, mouseY);
    }

    private List<ITextComponent> getEnergyTabText() {
        if (!tileEntity.isFormed()) {
            return new ArrayList<>();
        }
        return Arrays.asList(
              new TextComponentString(LangUtils.localize("gui.storing") + ": " +
                    MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy())),
              new TextComponentString(LangUtils.localize("gui.producing") + ": " +
                    MekanismUtils.getEnergyDisplay(tileEntity.getPassiveGeneration(false, true)) + "/t"));
    }

    private List<ITextComponent> getHeatTabText() {
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
}

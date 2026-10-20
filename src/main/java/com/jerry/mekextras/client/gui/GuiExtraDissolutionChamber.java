package com.jerry.mekextras.client.gui;

import java.util.Arrays;

import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import com.jerry.mekextras.client.gui.element.GuiExtraSortingTab;
import com.jerry.mekextras.common.inventory.container.ContainerExtraDissolutionChamber;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraDissolutionChamber;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;

public class GuiExtraDissolutionChamber extends GuiExtraProcessMachine<TileEntityExtraDissolutionChamber, ContainerExtraDissolutionChamber> {

    public GuiExtraDissolutionChamber(InventoryPlayer inventory, TileEntityExtraDissolutionChamber tile) {
        super(tile, new ContainerExtraDissolutionChamber(inventory, tile), () -> tile.outPutTanks.length);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiExtraSortingTab(this, tileEntity, tileEntity::isSorting));
        int slotLocation = ExtraMachineGuiLayout.processX(0);
        int xDistance = ExtraMachineGuiLayout.PROCESS_SPACING;
        int powerBarY = 13;
        int powerBarHeight = 56 + 30 - powerBarY - 2;
        addButton(new GuiVerticalPowerBar(this, tileEntity, ExtraMachineGuiLayout.powerBarX(processCount()), powerBarY, powerBarHeight));
        addButton(new GuiEnergyTab(this, () -> Arrays.asList(
              new TextComponentString(LangUtils.localize("gui.using") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick) + "/t"),
              new TextComponentString(LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy())))));
        addButton(new GuiGasGauge(this, tileEntity.injectTank, GuiGasGauge.Type.SMALL, 7, 32).withColor(GuiGasGauge.GaugeColor.ORANGE));
        for (int i = 0; i < processCount(); i++) {
            int cacheIndex = i;
            int gaugeX = slotLocation + i * xDistance;
            int progressX = gaugeX + 4;
            addButton(new GuiGasGauge(this, () -> tileEntity.outPutTanks[cacheIndex], GuiGasGauge.Type.SMALL, gaugeX, 56)
                  .withColor(GuiGasGauge.GaugeColor.BLUE));
            addButton(new GuiProgress(() -> tileEntity.getScaledProgress(cacheIndex), ProgressType.DOWN, this, progressX, 33)
                  .recipeViewerCategories(RecipeViewerRecipeType.DISSOLUTION));
        }
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText(ExtraMachineGuiLayout.inventoryX(processCount()), ySize - 91, getXSize());
        super.drawForegroundText(mouseX, mouseY);
    }
}

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
import com.jerry.mekextras.common.inventory.container.ContainerExtraCrystallizer;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraCrystallizer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;

public class GuiExtraCrystallizer extends GuiExtraProcessMachine<TileEntityExtraCrystallizer, ContainerExtraCrystallizer> {

    public GuiExtraCrystallizer(InventoryPlayer inventory, TileEntityExtraCrystallizer tile) {
        super(tile, new ContainerExtraCrystallizer(inventory, tile), () -> tile.inputTanks.length, 13);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiExtraSortingTab(this, tileEntity, tileEntity::isSorting));
        int slotLocation = ExtraMachineGuiLayout.processX(0);
        int xDistance = ExtraMachineGuiLayout.PROCESS_SPACING;
        int powerBarY = 13;
        int powerBarHeight = 71 + 18 - powerBarY - 2;
        addButton(new GuiVerticalPowerBar(this, tileEntity, ExtraMachineGuiLayout.powerBarX(processCount()), powerBarY, powerBarHeight));
        addButton(new GuiEnergyTab(this, () -> Arrays.asList(
              new TextComponentString(LangUtils.localize("gui.using") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick) + "/t"),
              new TextComponentString(LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy())))));
        for (int i = 0; i < processCount(); i++) {
            int cacheIndex = i;
            int gaugeX = slotLocation + i * xDistance;
            addButton(new GuiGasGauge(this, () -> tileEntity.inputTanks[cacheIndex], GuiGasGauge.Type.SMALL, gaugeX, 13)
                  .withColor(GuiGasGauge.GaugeColor.ORANGE));
            addButton(new GuiProgress(() -> tileEntity.getScaledProgress(cacheIndex), ProgressType.DOWN, this, gaugeX + 4, 47)
                  .recipeViewerCategories(RecipeViewerRecipeType.CRYSTALLIZING));
        }
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText(ExtraMachineGuiLayout.inventoryX(processCount()), ySize - 91, getXSize());
        super.drawForegroundText(mouseX, mouseY);
    }
}

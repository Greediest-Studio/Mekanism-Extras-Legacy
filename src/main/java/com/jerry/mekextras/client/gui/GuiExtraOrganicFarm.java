package com.jerry.mekextras.client.gui;

import mekanism.api.processing.MachineResourceStack;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiFluidBar;
import mekanism.client.gui.element.bar.GuiGasBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiDynamicResourceSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.capabilities.merged.MergedTank.CurrentType;
import com.jerry.mekextras.client.gui.element.GuiExtraSortingTab;
import com.jerry.mekextras.common.inventory.container.ContainerExtraOrganicFarm;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraOrganicFarm;
import com.jerry.mekextras.common.util.ExtraMachineGuiLayout;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory-style Organic Farm screen with one shared scrolling output preview.
 */
public class GuiExtraOrganicFarm extends GuiConfigurableTile<TileEntityExtraOrganicFarm, ContainerExtraOrganicFarm> {

    private static final int OUTPUT_PREVIEW_Y = 56;
    private static final int OUTPUT_PREVIEW_BORDER_COLOR = 0xFF4141A0;

    private GuiExtraOrganicFarmOutputWindow outputWindow;

    public GuiExtraOrganicFarm(InventoryPlayer inventory, TileEntityExtraOrganicFarm tile) {
        super(tile, new ContainerExtraOrganicFarm(inventory, tile));
        xSize = ExtraMachineGuiLayout.guiWidth(tile.threadCount);
        // Organic Farm has the same secondary-resource row as the vanilla
        // chemical factories: inventory starts at y=95 and the GUI is 177px tall.
        ySize += 11;
        inventoryLabelX = ExtraMachineGuiLayout.inventoryX(tile.threadCount);
        inventoryLabelY = ySize - 92;
        titleLabelY = 4;
        dynamicSlots = true;
    }

    @Override
    public void initGui() {
        xSize = ExtraMachineGuiLayout.guiWidth(tileEntity.threadCount);
        inventoryLabelX = ExtraMachineGuiLayout.inventoryX(tileEntity.threadCount);
        super.initGui();
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiExtraSortingTab(this, tileEntity, tileEntity::isSorting));
        addButton(new GuiVerticalPowerBar(this, tileEntity.getMainEnergyContainer(), ExtraMachineGuiLayout.powerBarX(tileEntity.threadCount), 16, 52));
        addButton(new GuiEnergyTab(this, tileEntity.getMainEnergyContainer(), tileEntity::getActive));
        addMediumBar();
        for (int lane = 0; lane < tileEntity.threadCount; lane++) {
            int laneIndex = lane;
            int x = tileEntity.getLaneX(lane);
            addButton(new GuiProgress(() -> tileEntity.getScaledProgress(laneIndex), ProgressType.DOWN, this, x + 4, 33)
                    .recipeViewerCategories(RecipeViewerRecipeType.ORGANIC_FARM));
        }
        addOutputPreview();
    }

    private void addOutputPreview() {
        int firstSlotX = tileEntity.getLaneX(0) - 1;
        int lastSlotRight = tileEntity.getLaneX(tileEntity.threadCount - 1) - 1 +
                SlotType.OUTPUT.getWidth();
        int frameWidth = lastSlotRight - firstSlotX;
        GuiDynamicResourceSlot preview = GuiDynamicResourceSlot.withFrameWidth(this, firstSlotX, OUTPUT_PREVIEW_Y,
                        frameWidth, 1, this::getDisplayedOutputResources)
                .autoFitSpacing(GuiDynamicResourceSlot.DEFAULT_RESOURCE_GAP)
                    .borderColor(GuiDynamicResourceSlot.BorderColor.OUTPUT)
                .click((element, mouseX, mouseY) -> openOutputWindow());
        preview.active = true;
        addButton(preview);
    }

    private List<MachineResourceStack> getDisplayedOutputResources() {
        List<MachineResourceStack> resources = new ArrayList<>(tileEntity.outputSlots.size());
        for (int slot = 0; slot < tileEntity.outputSlots.size(); slot++) {
            ItemStack output = tileEntity.outputSlots.get(slot).getStack();
            if (output.isEmpty()) {
                continue;
            }
            MachineResourceStack resource = MachineResourceStack.item("item_output", output);
            boolean merged = false;
            for (int index = 0; index < resources.size(); index++) {
                MachineResourceStack existing = resources.get(index);
                if (existing.sameResource(resource)) {
                    resources.set(index, existing.withAmount(existing.amount() + resource.amount()));
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                resources.add(resource);
            }
        }
        return resources.isEmpty() ? Collections.emptyList() : resources;
    }

    private void addMediumBar() {
        int width = tileEntity.getLaneX(tileEntity.threadCount - 1) + 18 - 7 - 2;
        GuiGasBar gasBar = new GuiGasBar(this, tileEntity.mergedTank.getGasTank(), 7, 76, width, 4, false,
                Collections::emptyList) {
            @Override
            public void tick() {
                super.tick();
                visible = !isFluidMediumSelected();
            }
        };
        GuiFluidBar fluidBar = new GuiFluidBar(this, tileEntity.mergedTank.getFluidTank(), 7, 76, width, 4, false) {
            @Override
            public void tick() {
                super.tick();
                visible = isFluidMediumSelected();
            }
        };
        gasBar.visible = !isFluidMediumSelected();
        fluidBar.visible = isFluidMediumSelected();
        addButton(gasBar);
        addButton(fluidBar);
    }

    private boolean isFluidMediumSelected() {
        return tileEntity.mergedTank.getCurrentType() == CurrentType.FLUID;
    }

    private boolean openOutputWindow() {
        if (outputWindow != null && getWindows().contains(outputWindow)) {
            focusWindow(outputWindow);
            return true;
        }
        outputWindow = new GuiExtraOrganicFarmOutputWindow(this, 8, 8,
                (ContainerExtraOrganicFarm) getContainer());
        outputWindow.setTabListeners(window -> outputWindow = null, null);
        addWindow(outputWindow);
        return true;
    }

    @Override
    protected void initPinnedWindows() {
        super.initPinnedWindows();
        if (new mekanism.common.inventory.container.SelectedWindowData(
                mekceumoremachine.common.ui.MoreMachineWindowTypes.ORGANIC_FARM_OUTPUT).wasPinned()) {
            openOutputWindow();
        }
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText();
        super.drawForegroundText(mouseX, mouseY);
    }
}

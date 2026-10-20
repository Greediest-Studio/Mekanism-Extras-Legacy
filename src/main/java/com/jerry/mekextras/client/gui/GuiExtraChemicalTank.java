package com.jerry.mekextras.client.gui;

import mekanism.common.network.PacketTileEntity;
import mekanism.client.gui.GuiConfigurableTile;
import com.jerry.mekextras.common.inventory.container.ContainerExtraChemicalTank;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;

import mekanism.api.EnumColor;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiGasBar;
import mekanism.client.gui.element.button.GuiGasMode;
import mekanism.client.gui.element.tab.GuiWarningTab;
import mekanism.client.gui.warning.IWarningTracker;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.recipe.GasStackFuelToEnergyRecipe;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiExtraChemicalTank extends GuiConfigurableTile<TileEntityExtraChemicalTank, ContainerExtraChemicalTank> {

    public GuiExtraChemicalTank(InventoryPlayer inventory, TileEntityExtraChemicalTank tile) {
        super(tile, new ContainerExtraChemicalTank(inventory, tile));
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addButton(GuiSideHolder.armorHolder(this));
        super.addGuiElements();
        addButton(new GuiGasBar(this, tileEntity.gasTank, 42, 16, this::getGasBarTooltip));
        addButton(new GuiInnerScreen(this, 42, 37, 118, 28, this::getScreenText));
        addButton(new GuiGasMode(this, 159, 72, true, () -> tileEntity.dumping, this::sendModePacket));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 4);
        renderInventoryText(85);
        super.drawForegroundText(mouseX, mouseY);
    }

    @Override
    protected void addWarningTab(IWarningTracker warningTracker) {
        addButton(new GuiWarningTab(this, warningTracker, 109, false));
    }

    private void sendModePacket() {
        Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, TileNetworkList.withContents(0)));
        SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    private String getGasDisplay() {
        return LangUtils.localize("gui.gas") + ": " + (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() :
                LangUtils.localize("gui.none"));
    }

    private String getCapacityDisplay() {
        return getStoredText(tileEntity.gasTank.getStored()) + " / " + getStoredText((int) Math.min(Integer.MAX_VALUE, tileEntity.tier.getStorage()));
    }

    private List<ITextComponent> getScreenText() {
        List<ITextComponent> text = new ArrayList<>();
        text.add(new TextComponentString(getGasDisplay()));
        text.add(new TextComponentString(getCapacityDisplay()));
        return text;
    }

    private String getStoredText(int amount) {
        return amount == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : Integer.toString(amount);
    }

    private List<String> getGasBarTooltip() {
        List<String> tooltip = new ArrayList<>();
        GasStack stack = tileEntity.gasTank.getGas();
        if (stack == null) {
            tooltip.add(LangUtils.localize("gui.none"));
            return tooltip;
        }
        tooltip.add(stack.getGas().getLocalizedName() + ": " + getStoredText(tileEntity.gasTank.getStored()));
        if (stack.getGas().isRadiation()) {
            tooltip.add(EnumColor.GREY + LangUtils.localize("chemical.mekanism.attribute.radiation") + EnumColor.INDIGO +
                    UnitDisplayUtils.getDisplayShort(stack.getGas().getRadioactivity(), UnitDisplayUtils.RadiationUnit.SVH, 2));
        }
        if (RecipeHandler.Recipe.GAS_FUEL_TO_ENERGY_RECIPE.containsRecipe(stack.getGas())) {
            GasStackFuelToEnergyRecipe recipe = RecipeHandler.getGasStackFuelToEnergyRecipe(stack);
            if (recipe != null) {
                tooltip.add(LangUtils.localize("chemical.mekanism.attribute.fuel.burn_ticks") + EnumColor.INDIGO + recipe.getInput().ingredient.amount +
                        TextFormatting.RESET + " t");
                tooltip.add(LangUtils.localize("chemical.mekanism.attribute.fuel.energy_density") + EnumColor.INDIGO +
                        MekanismUtils.getEnergyDisplay(recipe.getOutput().energyOutput * recipe.getInput().ingredient.amount));
            }
        }
        return tooltip;
    }
}


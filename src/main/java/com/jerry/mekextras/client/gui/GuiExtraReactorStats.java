package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab;
import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab.ReactorTab;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.api.EnumColor;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.NumberFormat;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorStats extends GuiExtraReactorInfo {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();

    public GuiExtraReactorStats(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(inventory, tile);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.HEAT));
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.FUEL));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        if (tileEntity.isFormed()) {
            int indentation = 4;
            int textArea = xSize - indentation;
            drawScrollingString(new TextComponentString(EnumColor.DARK_GREEN + LangUtils.localize("gui.passive")),
                  0, 26, TextAlignment.LEFT, titleTextColor(), 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.minInject") + ": " +
                        tileEntity.getMinInjectionRate(false)),
                  indentation, 36, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.ignition") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getIgnitionTemperature(), TemperatureUnit.KELVIN)),
                  indentation, 46, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.maxPlasma") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getMaxPlasmaTemperature(false), TemperatureUnit.KELVIN)),
                  indentation, 56, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.maxCasing") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getMaxCasingTemperature(false), TemperatureUnit.KELVIN)),
                  indentation, 66, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.passiveGeneration") + ": " +
                        MekanismUtils.getEnergyDisplay(tileEntity.getPassiveGeneration(false, false)) + "/t"),
                  indentation, 76, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);

            drawScrollingString(new TextComponentString(EnumColor.DARK_BLUE + LangUtils.localize("gui.active")),
                  0, 92, TextAlignment.LEFT, titleTextColor(), 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.minInject") + ": " +
                        tileEntity.getMinInjectionRate(true)),
                  indentation, 102, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.ignition") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getIgnitionTemperature(), TemperatureUnit.KELVIN)),
                  indentation, 112, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.maxPlasma") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getMaxPlasmaTemperature(true), TemperatureUnit.KELVIN)),
                  indentation, 122, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.maxCasing") + ": " +
                        MekanismUtils.getTemperatureDisplay(tileEntity.getMaxCasingTemperature(true), TemperatureUnit.KELVIN)),
                  indentation, 132, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.passiveGeneration") + ": " +
                        MekanismUtils.getEnergyDisplay(tileEntity.getPassiveGeneration(true, false)) + "/t"),
                  indentation, 142, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
            drawScrollingString(new TextComponentString(LangUtils.localize("gui.steamProduction") + ": " +
                        NUMBER_FORMAT.format(tileEntity.getSteamPerTick(false)) + "mB/t"),
                  indentation, 152, TextAlignment.LEFT, titleTextColor(), textArea, 6, false);
        }
        super.drawForegroundText(mouseX, mouseY);
    }
}

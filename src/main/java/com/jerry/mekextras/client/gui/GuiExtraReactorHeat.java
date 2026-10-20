package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab;
import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab.ReactorTab;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge.INumberInfoHandler;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorHeat extends GuiExtraReactorInfo {

    private static final double MAX_LEVEL = 500_000_000D;

    public GuiExtraReactorHeat(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(inventory, tile);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(FluidRegistry.LAVA, MekanismRenderer.FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tileEntity.getPlasmaTemperature();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1D, getLevel() / MAX_LEVEL);
            }

            @Override
            public String getText() {
                return LangUtils.localize("gui.Plasma") + ": " +
                      MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN);
            }
        }, GaugeType.STANDARD, this, 12, 50));
        addButton(new GuiProgress(() -> tileEntity.getPlasmaTemperature() > tileEntity.getCasingTemperature() ? 1 : 0,
              ProgressType.SMALL_RIGHT, this, 34, 76));
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(FluidRegistry.LAVA, MekanismRenderer.FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tileEntity.getCasingTemperature();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1D, getLevel() / MAX_LEVEL);
            }

            @Override
            public String getText() {
                return LangUtils.localize("gui.Case") + ": " +
                      MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN);
            }
        }, GaugeType.STANDARD, this, 66, 50));
        addButton(new GuiProgress(() -> tileEntity.getCasingTemperature() > 0D ? 1 : 0,
              ProgressType.SMALL_RIGHT, this, 88, 61));
        addButton(new GuiProgress(() -> tileEntity.getCasingTemperature() > 0D &&
              tileEntity.getWaterTank() != null && tileEntity.getWaterTank().getFluidAmount() > 0 &&
              tileEntity.getSteamTank() != null && tileEntity.getSteamTank().getStored() < tileEntity.getSteamTank().getCapacity() ? 1 : 0,
              ProgressType.SMALL_RIGHT, this, 88, 91));
        if (tileEntity.getWaterTank() != null) {
            addButton(new GuiFluidGauge(this, tileEntity.getWaterTank(), GuiFluidGauge.Type.SMALL, 120, 84));
        }
        if (tileEntity.getSteamTank() != null) {
            addButton(new GuiGasGauge(this, tileEntity.getSteamTank(), GuiGasGauge.Type.SMALL, 156, 84));
        }
        if (tileEntity.getStructureData() != null) {
            addButton(new GuiEnergyGauge(tileEntity.getStructureData(), GaugeType.SMALL, this, 120, 46));
        }
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.FUEL));
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.STATS));
    }
}

package com.jerry.mekextras.client.render.tileentity;

import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.client.render.transmitter.RenderTransmitterSimple;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import mekanism.common.ColourRGBA;
import mekanism.common.config.MekanismConfig;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraPressurizedTube;
import mekanism.common.transmitters.TransmitterImpl;
import mekanism.common.transmitters.grid.GasNetwork;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;

public class ExtraRenderPressurizedTube extends RenderTransmitterSimple<TileEntityExtraPressurizedTube> {

    @Override
    public void render(TileEntityExtraPressurizedTube tube, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        if (!MekanismConfig.current().client.opaqueTransmitters.val()) {
            TransmitterImpl<IGasHandler, GasNetwork, GasStack> transmitter = tube.getTransmitter();
            if (transmitter.hasTransmitterNetwork() && transmitter.getTransmitterNetwork().refGas != null
                    && transmitter.getTransmitterNetwork().gasScale != 0 && shouldRenderTransmitterInterior(tube, partialTick)) {
                render(tube, x, y, z, 0);
            }
        }
    }

    @Override
    protected void renderSide(BufferBuilder renderer, EnumFacing side, TileEntityExtraPressurizedTube tube) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Gas gas = tube.getTransmitter().getTransmitterNetwork().refGas;
        ColourRGBA c = new ColourRGBA(1.0, 1.0, 1.0, tube.currentScale);
        c.setRGBFromInt(gas.getTint());
        renderTransparency(renderer, gas.getSprite(), getModelForSide(tube, side), c);
    }
}

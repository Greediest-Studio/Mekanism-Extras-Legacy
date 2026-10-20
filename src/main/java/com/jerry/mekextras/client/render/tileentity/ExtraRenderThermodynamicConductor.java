package com.jerry.mekextras.client.render.tileentity;

import mekanism.client.render.ColourTemperature;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.transmitter.RenderTransmitterSimple;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.common.config.MekanismConfig;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraThermodynamicConductor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;

public class ExtraRenderThermodynamicConductor extends RenderTransmitterSimple<TileEntityExtraThermodynamicConductor> {

    @Override
    public void render(TileEntityExtraThermodynamicConductor transmitter, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        if (!MekanismConfig.current().client.opaqueTransmitters.val() && transmitter.temperature > 0.01D
              && shouldRenderTransmitterInterior(transmitter, partialTick)) {
            render(transmitter, x, y, z, 15);
        }
    }

    @Override
    public void renderSide(BufferBuilder renderer, EnumFacing side, TileEntityExtraThermodynamicConductor cable) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderTransparency(renderer, MekanismRenderer.whiteIcon, getModelForSide(cable, side), ColourTemperature.fromTemperature(cable.temperature, cable.getBaseColour()));
    }
}

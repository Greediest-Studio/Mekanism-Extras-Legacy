package com.jerry.mekextras.client.render.tileentity;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.transmitter.RenderTransmitterSimple;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.common.ColourRGBA;
import mekanism.common.config.MekanismConfig;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraUniversalCable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;

public class ExtraRenderUniversalCable extends RenderTransmitterSimple<TileEntityExtraUniversalCable> {

    @Override
    public void render(TileEntityExtraUniversalCable cable, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        if (!MekanismConfig.current().client.opaqueTransmitters.val() && cable.currentPower != 0 && shouldRenderTransmitterInterior(cable, partialTick)) {
            render(cable, x, y, z, 15);
        }
    }

    @Override
    protected void renderSide(BufferBuilder renderer, EnumFacing side, TileEntityExtraUniversalCable cable) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderTransparency(renderer, MekanismRenderer.energyIcon, getModelForSide(cable, side), new ColourRGBA(1.0, 1.0, 1.0, cable.currentPower));
    }
}

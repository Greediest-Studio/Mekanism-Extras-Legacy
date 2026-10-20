package com.jerry.mekextras.client.render.tileentity;

import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Renderer for the machines that only need Mekanism's machine overlay renderer (like Mekanism's own gas tank).
 */
@SideOnly(Side.CLIENT)
public class RenderExtraMachine extends TileEntitySpecialRenderer<TileEntityExtraChemicalTank> {

    @Override
    public void render(TileEntityExtraChemicalTank tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        MekanismRenderer.machineRenderer().render(tileEntity, x, y, z, partialTick, destroyStage, alpha);
    }
}

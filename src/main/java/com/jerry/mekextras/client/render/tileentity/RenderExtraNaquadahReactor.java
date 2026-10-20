package com.jerry.mekextras.client.render.tileentity;

import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.api.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.client.render.tileentity.RenderEnergyCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/** Renders the rotating plasma cores inside a formed and burning naquadah reactor. */
@SideOnly(Side.CLIENT)
public class RenderExtraNaquadahReactor extends TileEntitySpecialRenderer<TileEntityExtraReactorController> {

    private static final double TEMPERATURE_SCALE = 100_000_000D;
    private static final ResourceLocation CORE_TEXTURE = RenderEnergyCube.coreTexture;
    private final ModelEnergyCore core = new ModelEnergyCore();

    @Override
    public void render(TileEntityExtraReactorController tile, double x, double y, double z, float partialTick,
                       int destroyStage, float alpha) {
        if (!tile.isFormed() || !tile.isBurning()) {
            return;
        }
        long scaledTemperature = Math.round(tile.getPlasmaTemperature() / TEMPERATURE_SCALE);
        float ticks = MekanismClient.ticksPassed + partialTick;

        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate((float) x + 0.5F, (float) y - 3.5F, (float) z + 0.5F);
        bindTexture(CORE_TEXTURE);
        GlowInfo glowInfo = MekanismRenderer.enableGlow();
        setLightmapDisabled(true);

        float scale = 1 + 0.7F * sinDegrees(3.14F * scaledTemperature + 135);
        renderPart(EnumColor.DARK_GREEN, scale, ticks, -6, -7, 0, 36);

        scale = 1 + 0.8F * sinDegrees(3 * scaledTemperature);
        renderPart(EnumColor.YELLOW, scale, ticks, 4, 4, 0, 36);

        scale = 1 - 0.9F * sinDegrees(4 * scaledTemperature + 90);
        renderPart(EnumColor.WHITE, scale, ticks, 5, -3, -35, 106);

        MekanismRenderer.resetColor();
        setLightmapDisabled(false);
        MekanismRenderer.disableGlow(glowInfo);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        MekanismRenderer.resetBlockRenderState();
        GlStateManager.popMatrix();
    }

    private void renderPart(EnumColor color, float scale, float ticks, int rotationY, int rotationDiag,
                            int shiftY, int shiftDiag) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(2 * scale, 2 * scale, 2 * scale);
        GlStateManager.rotate(ticks * rotationY + shiftY, 0, 1, 0);
        GlStateManager.rotate(ticks * rotationDiag + shiftDiag, 0, 1, 1);
        MekanismRenderer.color(color, 1F);
        core.render(0.0625F);
        GlStateManager.popMatrix();
    }

    private static float sinDegrees(float degrees) {
        return (float) Math.sin(Math.toRadians(degrees % 360));
    }
}

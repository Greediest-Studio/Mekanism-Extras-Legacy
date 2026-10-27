package com.jerry.mekextras.client.render.tileentity;

import mekanism.api.gas.GasStack;
import mekanism.client.render.GasRenderMap;
import mekanism.client.MekanismClient;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraIsotopicCentrifuge;
import mekceumoremachine.client.model.machine.ModelTierIsotopicCentrifuge;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

public class ExtraRenderTierIsotopicCentrifuge extends TileEntitySpecialRenderer<TileEntityExtraIsotopicCentrifuge> {

    public static final ExtraRenderTierIsotopicCentrifuge INSTANCE = new ExtraRenderTierIsotopicCentrifuge();
    private static final GasRenderMap<MekanismRenderer.DisplayInteger[]> CACHED_GAS = new GasRenderMap<>();
    private static final int STAGES = 64;
    private final ModelTierIsotopicCentrifuge model = new ModelTierIsotopicCentrifuge();

    public static void resetDisplayInts() {
        CACHED_GAS.clear();
    }

    @Override
    public void render(TileEntityExtraIsotopicCentrifuge tileEntity, double x, double y, double z, float partialTick,
                       int destroyStage, float alpha) {
        if (tileEntity.outputTank.getStored() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableCull();
            GlStateManager.disableLighting();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translate((float) x, (float) y, (float) z);
            MekanismRenderer.GlowInfo glowInfo = MekanismRenderer.enableGlow();
            MekanismRenderer.DisplayInteger[] displayList = getListAndRender(tileEntity.outputTank.getGas());
            MekanismRenderer.color(tileEntity.outputTank.getGas());
            displayList[Math.min(STAGES - 1, (int) (tileEntity.prevScale * ((float) STAGES - 1)))].render();
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableLighting();
            GlStateManager.disableCull();
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        MekanismRenderer.rotate(tileEntity.facing, 0, 180, 90, 270);
        GlStateManager.rotate(180, 0, 0, 1);
        MekanismRenderer.resetColor();
        bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
              "IsotopicCentrifuge_" + tierSuffix(tileEntity.tier) + "_detail.png"));
        model.renderTier(0.0625F);
        bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
              "IsotopicCentrifuge_" + tierSuffix(tileEntity.tier) + "_mask.png"));
        int tierColor = tileEntity.tier.getAdvanceTier().getDisplayColor(MekanismClient.ticksPassed + partialTick);
        MekanismRenderer.color(0xFF000000 | tierColor);
        model.renderTier(0.0625F);
        MekanismRenderer.resetColor();
        bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER, "IsotopicCentrifuge.png"));
        model.render(0.0625F, true);
        GlStateManager.popMatrix();
        MekanismRenderer.machineRenderer().render(tileEntity, x, y, z, partialTick, destroyStage, alpha);
    }

    private MekanismRenderer.DisplayInteger[] getListAndRender(GasStack gasStack) {
        if (CACHED_GAS.containsKey(gasStack)) {
            return CACHED_GAS.get(gasStack);
        }
        MekanismRenderer.Model3D model3D = new MekanismRenderer.Model3D();
        model3D.baseBlock = Blocks.WATER;
        model3D.setTexture(gasStack.getGas().getSprite());
        MekanismRenderer.DisplayInteger[] displays = new MekanismRenderer.DisplayInteger[STAGES];
        CACHED_GAS.put(gasStack, displays);
        for (int i = 0; i < STAGES; i++) {
            displays[i] = MekanismRenderer.DisplayInteger.createAndStart();
            model3D.minZ = 0.335D;
            model3D.maxZ = 0.7275D;
            model3D.minX = 0.335D;
            model3D.maxX = 0.7275D;
            model3D.minY = 0.9475D;
            model3D.maxY = 0.9275D + ((float) i / STAGES) * 0.75D;
            MekanismRenderer.renderObject(model3D);
            MekanismRenderer.DisplayInteger.endList();
        }
        return displays;
    }

    private static String tierSuffix(ExtraMachineTier tier) {
        return tier.getName();
    }
}

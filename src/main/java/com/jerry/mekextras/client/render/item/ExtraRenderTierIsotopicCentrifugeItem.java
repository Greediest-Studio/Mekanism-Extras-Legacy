package com.jerry.mekextras.client.render.item;

import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import mekanism.client.MekanismClient;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import mekceumoremachine.client.model.machine.ModelTierIsotopicCentrifuge;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ExtraRenderTierIsotopicCentrifugeItem extends MekanismItemStackRenderer {
    public static ItemLayerWrapper model;
    private static final ModelTierIsotopicCentrifuge CUBE = new ModelTierIsotopicCentrifuge();

    @Override
    protected void renderBlockSpecific(@NotNull ItemStack stack, TransformType transformType) {
        ExtraMachineTier tier = ExtraMachineTier.byIndex(ItemBlockExtraMachine.getTier(stack).ordinal());
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.translate(0, -0.55F, 0);
        MekanismRenderer.resetColor();
        MekanismRenderer.bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
                "IsotopicCentrifuge_" + tierSuffix(tier) + "_detail.png"));
        CUBE.renderTier(0.0625F);
        MekanismRenderer.bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
                "IsotopicCentrifuge_" + tierSuffix(tier) + "_mask.png"));
        int tierColor = tier.getAdvanceTier().getDisplayColor(MekanismClient.ticksPassed);
        MekanismRenderer.color(0xFF000000 | tierColor);
        CUBE.renderTier(0.0625F);
        MekanismRenderer.resetColor();
        MekanismRenderer.bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER, "IsotopicCentrifuge.png"));
        CUBE.render(0.0625F, false);
        GlStateManager.popMatrix();
    }

    @Override protected void renderItemSpecific(@NotNull ItemStack stack, TransformType transformType) { }
    @Override protected @NotNull TransformType getTransform(@NotNull ItemStack stack) { return model.getTransform(); }

    private static String tierSuffix(ExtraMachineTier tier) {
        return tier.getName();
    }
}
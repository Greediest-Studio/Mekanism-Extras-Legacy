package com.jerry.mekextras.client.render.item;

import com.jerry.mekextras.client.model.ExtraModelTierNutritionalLiquifier;
import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import mekanism.client.MekanismClient;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ExtraRenderTierNutritionalLiquifierItem extends MekanismItemStackRenderer {
    public static ItemLayerWrapper model;
    private static final ExtraModelTierNutritionalLiquifier CUBE = new ExtraModelTierNutritionalLiquifier();

    @Override
    protected void renderBlockSpecific(@NotNull ItemStack stack, TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(0, -1.0F, 0);
        ExtraMachineTier tier = ExtraMachineTier.byIndex(ItemBlockExtraMachine.getTier(stack).ordinal());
        MekanismRenderer.resetColor();
        MekanismRenderer.bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
                "NutritionalLiquifier_" + tierSuffix(tier) + "_detail.png"));
        CUBE.renderTier(0.0625F);
        MekanismRenderer.bindTexture(MekanismUtils.getResource("mekanism_extras", MekanismUtils.ResourceType.RENDER,
                "NutritionalLiquifier_" + tierSuffix(tier) + "_mask.png"));
        int tierColor = tier.getAdvanceTier().getDisplayColor(MekanismClient.ticksPassed);
        MekanismRenderer.color(0xFF000000 | tierColor);
        CUBE.renderTier(0.0625F);
        MekanismRenderer.resetColor();
        MekanismRenderer.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.RENDER, "NutritionalLiquifier.png"));
        CUBE.render(0.0625F, false);
        CUBE.renderBlade(0.0625F);
        CUBE.renderGlass(0.0625F, false);
        GlStateManager.popMatrix();
    }

    @Override protected void renderItemSpecific(@NotNull ItemStack stack, TransformType transformType) { }
    @Override protected @NotNull TransformType getTransform(@NotNull ItemStack stack) { return model.getTransform(); }

    private static String tierSuffix(ExtraMachineTier tier) {
        return tier.getName();
    }
}
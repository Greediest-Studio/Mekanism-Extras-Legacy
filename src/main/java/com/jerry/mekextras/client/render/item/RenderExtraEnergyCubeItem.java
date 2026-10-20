package com.jerry.mekextras.client.render.item;

import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.tier.ECTier;
import mekanism.client.MekanismClient;
import com.jerry.mekextras.client.model.ExtraModelEnergyCube;
import com.jerry.mekextras.client.model.ExtraModelEnergyCube.EnergyCubeSideState;
import com.jerry.mekextras.client.model.ExtraModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.client.render.tileentity.RenderEnergyCube;
import com.jerry.mekextras.client.render.tileentity.RenderExtraEnergyCube;

import mekanism.common.util.StorageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Inventory/held renderer for the extra energy cubes, mirroring Mekanism's own RenderEnergyCubeItem.
 */
@SideOnly(Side.CLIENT)
public class RenderExtraEnergyCubeItem extends MekanismItemStackRenderer {

    private static final Map<Item, ItemLayerWrapper> MODELS = new HashMap<>();
    private static final ExtraModelEnergyCube energyCube = new ExtraModelEnergyCube();
    private static final ModelEnergyCore core = new ModelEnergyCore();

    public static void registerModel(Item item, ItemLayerWrapper wrapper) {
        MODELS.put(item, wrapper);
    }

    @Override
    protected void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType) {
        ECTier tier = ECTier.byIndex(ItemBlockExtraMachine.getTier(stack).ordinal());
        float ticks = MekanismClient.ticksPassed;
        int tierColor = tier.getAdvanceTier().getDisplayColor(ticks);
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -1.0F, 0);
        MekanismRenderer.bindTexture(RenderExtraEnergyCube.getBaseTexture(tier.getAdvanceTier()));
        energyCube.render(0.0625F, tierColor, Minecraft.getMinecraft().renderEngine, true);

        for (EnumFacing side : EnumFacing.VALUES) {
            MekanismRenderer.bindTexture(RenderExtraEnergyCube.getBaseTexture(tier.getAdvanceTier()));
            energyCube.renderSide(0.0625F, side, side == EnumFacing.NORTH ? EnergyCubeSideState.OUTPUT : EnergyCubeSideState.INPUT, tierColor, Minecraft.getMinecraft().renderEngine);
        }
        GlStateManager.popMatrix();

        double energyPercentage = StorageUtils.getStoredEnergyForDisplay(stack) / Math.max(1D, ((ItemBlockExtraMachine) stack.getItem()).getEnergyCapacity(stack));
        energyPercentage = Math.min(1D, Math.max(0D, energyPercentage));
        if (energyPercentage > 0.1) {
            MekanismRenderer.bindTexture(RenderEnergyCube.coreTexture);
            GlowInfo glowInfo = MekanismRenderer.enableGlow();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
            MekanismRenderer.color(((int) (energyPercentage * 255) << 24) | tierColor);
            GlStateManager.translate(0, (float) Math.sin(Math.toRadians(3 * ticks)) / 7, 0);
            GlStateManager.rotate(4 * ticks, 0, 1, 0);
            GlStateManager.rotate(36F + 4 * ticks, 0, 1, 1);
            core.render(0.0625F);
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType) {
    }

    @Nonnull
    @Override
    protected TransformType getTransform(@Nonnull ItemStack stack) {
        ItemLayerWrapper wrapper = MODELS.get(stack.getItem());
        return wrapper == null ? TransformType.NONE : wrapper.getTransform();
    }
}

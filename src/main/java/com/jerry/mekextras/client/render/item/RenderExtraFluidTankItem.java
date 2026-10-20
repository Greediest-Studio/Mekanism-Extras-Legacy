package com.jerry.mekextras.client.render.item;

import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.tier.FTTier;
import com.jerry.mekextras.client.model.ExtraModelFluidTank;
import mekanism.client.render.FluidRenderMap;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Inventory/held renderer for the extra fluid tanks, mirroring Mekanism's own RenderFluidTankItem.
 */
@SideOnly(Side.CLIENT)
public class RenderExtraFluidTankItem extends MekanismItemStackRenderer {

    private static final Map<Item, ItemLayerWrapper> MODELS = new HashMap<>();
    private static final ExtraModelFluidTank fluidTank = new ExtraModelFluidTank();
    private static final FluidRenderMap<DisplayInteger[]> cachedCenterFluids = new FluidRenderMap<>();
    private static final int stages = 64;

    public static void registerModel(Item item, ItemLayerWrapper wrapper) {
        MODELS.put(item, wrapper);
    }

    public static void resetDisplayInts() {
        cachedCenterFluids.values().forEach(DisplayInteger::deleteAll);
        cachedCenterFluids.clear();
    }

    @Override
    protected void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType) {
        ItemBlockExtraMachine item = (ItemBlockExtraMachine) stack.getItem();
        FluidStack fluid = item.getFluidStack(stack);
        int capacity = Math.max(1, item.getFluidCapacity(stack));
        float fluidScale = fluid == null ? 0 : (float) fluid.amount / capacity;
        FTTier tier = FTTier.byIndex(ItemBlockExtraMachine.getTier(stack).ordinal());

        GlStateManager.pushMatrix();
        if (fluid != null && fluidScale > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableCull();
            GlStateManager.disableLighting();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            MekanismRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlowInfo glowInfo = MekanismRenderer.enableGlow(fluid);
            DisplayInteger[] displayList = getListAndRender(fluid);
            MekanismRenderer.color(fluid, fluidScale);
            if (fluid.getFluid().isGaseous(fluid)) {
                displayList[stages - 1].render();
            } else {
                displayList[Math.min(stages - 1, (int) (fluidScale * ((float) stages - 1)))].render();
            }
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableLighting();
            GlStateManager.disableCull();
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(0, -0.9F, 0);
        GlStateManager.scale(0.9F, 0.8F, 0.9F);
        MekanismRenderer.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "FluidTank.png"));
        fluidTank.render(0.073F, tier.getAdvanceTier().getDisplayColor(getAnimationTicks()), false);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType) {
    }

    private static float getAnimationTicks() {
        return (System.currentTimeMillis() % 600_000L) / 50.0F;
    }

    @Nonnull
    @Override
    protected TransformType getTransform(@Nonnull ItemStack stack) {
        ItemLayerWrapper wrapper = MODELS.get(stack.getItem());
        return wrapper == null ? TransformType.NONE : wrapper.getTransform();
    }

    private static DisplayInteger[] getListAndRender(FluidStack fluid) {
        if (cachedCenterFluids.containsKey(fluid)) {
            return cachedCenterFluids.get(fluid);
        }

        Model3D toReturn = new Model3D();
        toReturn.baseBlock = Blocks.WATER;
        toReturn.setTexture(MekanismRenderer.getFluidTexture(fluid, FluidType.STILL));

        DisplayInteger[] displays = new DisplayInteger[stages];
        cachedCenterFluids.put(fluid, displays);

        for (int i = 0; i < stages; i++) {
            displays[i] = DisplayInteger.createAndStart();

            if (fluid.getFluid().getStill(fluid) != null) {
                toReturn.minX = 0.125 + .01;
                toReturn.minY = 0.0625 + .01;
                toReturn.minZ = 0.125 + .01;

                toReturn.maxX = 0.875 - .01;
                toReturn.maxY = 0.0625 + ((float) i / (float) stages) * 0.875 - .01;
                toReturn.maxZ = 0.875 - .01;

                MekanismRenderer.renderObject(toReturn);
            }

            DisplayInteger.endList();
        }

        return displays;
    }
}

package com.jerry.mekextras.common.item;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.block.BlockExtraTransmitter;
import com.jerry.mekextras.common.tier.ExtraCableTier;
import com.jerry.mekextras.common.tier.ExtraConductorTier;
import com.jerry.mekextras.common.tier.ExtraPipeTier;
import com.jerry.mekextras.common.tier.ExtraTransporterTier;
import com.jerry.mekextras.common.tier.ExtraTubeTier;
import mekanism.api.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismClient;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.MekanismKeyHandler;
import mekanism.client.gui.GuiUtils;
import mekanism.common.Mekanism;
import mekanism.common.block.states.BlockStateTransmitter.TransmitterType;
import mekanism.common.interfaces.IOverlayRenderAware;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Item form of the extra tier transmitters. Mirrors Mekanism's own transmitter tooltip so the extra tiers show their
 * capacity, pump rate and conduction values the same way Mekanism's own transmitters do.
 */
public class ItemBlockExtraTransmitter extends ItemBlock implements IOverlayRenderAware {

    public ItemBlockExtraTransmitter(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
        if (!(getBlock() instanceof BlockExtraTransmitter transmitter)) {
            return false;
        }
        TransmitterType type = transmitter.getTransmitterType();
        String iconType;
        if (type == TransmitterType.UNIVERSAL_CABLE) {
            iconType = "UniversalCable";
        } else if (type == TransmitterType.PRESSURIZED_TUBE) {
            iconType = "PressurizedTube";
        } else if (type == TransmitterType.THERMODYNAMIC_CONDUCTOR) {
            iconType = "ThermodynamicConductor";
        } else {
            return false;
        }
        ResourceLocation icon = new ResourceLocation(MekanismExtras.MODID, "gui/icons/" +
              iconType.toLowerCase(Locale.ROOT) + "_mask.png");
        float ticks = MekanismClient.ticksPassed + Minecraft.getMinecraft().getRenderPartialTicks();
        int color = transmitter.getExtraTier().getDisplayColor(ticks);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0, 0, 200);
            GlStateManager.disableDepth();
            MekanismRenderer.color(0xFF000000 | color);
            Minecraft.getMinecraft().renderEngine.bindTexture(icon);
            GuiUtils.blit(xPosition, yPosition, 0, 0, 16, 16, 16, 16);
        } finally {
            MekanismRenderer.resetColor();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        if (!(getBlock() instanceof BlockExtraTransmitter)) {
            return;
        }
        BlockExtraTransmitter transmitter = (BlockExtraTransmitter) getBlock();
        TransmitterType type = transmitter.getTransmitterType();
        AdvancedTier tier = transmitter.getExtraTier();
        int index = tier.ordinal();
        if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            switch (type) {
                case UNIVERSAL_CABLE:
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                          MekanismUtils.getEnergyDisplay(ExtraCableTier.byIndex(index).getCableCapacity()) + "/t");
                    break;
                case MECHANICAL_PIPE:
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                          ExtraPipeTier.byIndex(index).getPipeCapacity() + "mB/t");
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.pumpRate") + ": " + EnumColor.GREY +
                          ExtraPipeTier.byIndex(index).getPipePullAmount() + "mB/t");
                    break;
                case PRESSURIZED_TUBE:
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                          ExtraTubeTier.byIndex(index).getTubeCapacity() + "mB/t");
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.pumpRate") + ": " + EnumColor.GREY +
                          ExtraTubeTier.byIndex(index).getTubePullAmount() + "mB/t");
                    break;
                case LOGISTICAL_TRANSPORTER:
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.speed") + ": " + EnumColor.GREY +
                          (ExtraTransporterTier.byIndex(index).getSpeed() / (100 / 20)) + " m/s");
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.pumpRate") + ": " + EnumColor.GREY +
                          ExtraTransporterTier.byIndex(index).getPullAmount() * 2 + "/s");
                    break;
                case THERMODYNAMIC_CONDUCTOR:
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.conduction") + ": " + EnumColor.GREY +
                          ExtraConductorTier.byIndex(index).getInverseConduction());
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.insulation") + ": " + EnumColor.GREY +
                          ExtraConductorTier.byIndex(index).getInverseConductionInsulation());
                    tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.heatCapacity") + ": " + EnumColor.GREY +
                          ExtraConductorTier.byIndex(index).getHeatCapacity());
                    break;
                default:
                    break;
            }
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA +
                  GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails"));
        } else {
            tooltip.add(EnumColor.DARK_GREY + LangUtils.localize("tooltip.capableTrans") + ":");
            switch (type) {
                case UNIVERSAL_CABLE:
                    tooltip.add("- " + EnumColor.PURPLE + "RF " + EnumColor.GREY + "(ThermalExpansion)");
                    tooltip.add("- " + EnumColor.PURPLE + "EU " + EnumColor.GREY + "(IndustrialCraft)");
                    tooltip.add("- " + EnumColor.PURPLE + "Joules " + EnumColor.GREY + "(Mekanism)");
                    break;
                case MECHANICAL_PIPE:
                    tooltip.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.fluids") + " " + EnumColor.GREY + "(MinecraftForge)");
                    break;
                case PRESSURIZED_TUBE:
                    tooltip.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.gasses") + " (Mekanism)");
                    break;
                case LOGISTICAL_TRANSPORTER:
                    tooltip.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.items") + " (" + LangUtils.localize("tooltip.universal") + ")");
                    tooltip.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.blocks") + " (" + LangUtils.localize("tooltip.universal") + ")");
                    break;
                case THERMODYNAMIC_CONDUCTOR:
                    tooltip.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.heat") + " (Mekanism)");
                    break;
                default:
                    break;
            }
        }
    }
}

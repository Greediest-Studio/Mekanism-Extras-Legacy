package com.jerry.mekextras.common.item;

import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.util.ExtraUtils;
import mekanism.common.item.ItemMekanism;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.SecurityUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemExtraTierInstaller extends ItemMekanism {

    @Nullable
    private final AdvancedTier fromTier;
    private final AdvancedTier toTier;

    public ItemExtraTierInstaller(@Nullable AdvancedTier fromTier, AdvancedTier toTier) {
        this.fromTier = fromTier;
        this.toTier = toTier;
    }

    @Nullable
    public AdvancedTier getFromTier() {
        return fromTier;
    }

    public AdvancedTier getToTier() {
        return toTier;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (!canUpgrade(tile)) {
            return EnumActionResult.PASS;
        }
        if (!SecurityUtils.canAccess(player, tile)) {
            if (!world.isRemote) {
                SecurityUtils.displayNoAccess(player);
            }
            return EnumActionResult.FAIL;
        }
        if (!world.isRemote) {
            boolean upgraded = tile instanceof IExtraTierMachine
                  ? ExtraUtils.applyExtraTier(world, pos, toTier)
                  : ExtraUtils.applyUpstreamExtraTier(world, pos, toTier);
            if (!upgraded) {
                return EnumActionResult.PASS;
            }
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private boolean canUpgrade(@Nullable TileEntity tile) {
        if (tile == null) {
            return false;
        }
        if (tile instanceof TileEntityBasicBlock && !((TileEntityBasicBlock) tile).playersUsing.isEmpty()) {
            return false;
        }
        if (tile instanceof IExtraTierMachine) {
            return ((IExtraTierMachine) tile).canInstallExtraTier(toTier);
        }
        return ExtraUtils.canApplyUpstreamExtraTier(tile, toTier);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String localizedTier = ExtraUtils.getTierName(toTier);
        if (fromTier == null) {
            tooltip.add(TextFormatting.GRAY + net.minecraft.util.text.translation.I18n.translateToLocalFormatted(
                  "tooltip.mekanism_extras.tier_installer", localizedTier));
        } else {
            tooltip.add(TextFormatting.GRAY + net.minecraft.util.text.translation.I18n.translateToLocalFormatted(
                  "tooltip.mekanism_extras.tier_installer_upgrade", ExtraUtils.getTierName(fromTier), localizedTier));
        }
    }
}

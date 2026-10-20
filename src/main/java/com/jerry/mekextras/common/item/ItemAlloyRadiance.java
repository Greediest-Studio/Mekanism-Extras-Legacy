package com.jerry.mekextras.common.item;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.util.ExtraTransmitterUpgradeUtils;
import mekanism.common.item.ItemMekanism;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Radiance alloy bridges an upstream Ultimate transmitter network to the Absolute tier.
 */
public class ItemAlloyRadiance extends ItemMekanism {

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!ExtraTransmitterUpgradeUtils.canUpgrade(world, pos, AdvancedTier.ABSOLUTE)) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote && !ExtraTransmitterUpgradeUtils.tryUpgrade(
              player, world, pos, player.getHeldItem(hand), AdvancedTier.ABSOLUTE)) {
            return EnumActionResult.PASS;
        }
        return EnumActionResult.SUCCESS;
    }
}

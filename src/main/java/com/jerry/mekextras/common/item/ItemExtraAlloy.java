package com.jerry.mekextras.common.item;

import com.jerry.mekextras.api.tier.ExtraAlloyTier;
import com.jerry.mekextras.common.util.ExtraTransmitterUpgradeUtils;
import mekanism.common.item.ItemMekanism;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemExtraAlloy extends ItemMekanism {

    private final ExtraAlloyTier tier;

    public ItemExtraAlloy(ExtraAlloyTier tier, EnumRarity rarity) {
        this.tier = tier;
        setRarity(rarity);
    }

    public ExtraAlloyTier getTier() {
        return tier;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        if (ExtraTransmitterUpgradeUtils.canUpgrade(world, pos, tier.getAdvanceTier())) {
            if (!world.isRemote && !ExtraTransmitterUpgradeUtils.tryUpgrade(
                  player, world, pos, player.getHeldItem(hand), tier.getAdvanceTier())) {
                return EnumActionResult.PASS;
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}

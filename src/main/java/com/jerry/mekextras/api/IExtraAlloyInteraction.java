package com.jerry.mekextras.api;

import mekanism.common.tier.ITier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Implemented by machines that can be alloy-upgraded with the alloys added by Mekanism Extras.
 */
public interface IExtraAlloyInteraction {

    void onExtraAlloyInteraction(EntityPlayer player, ItemStack stack, ITier tier, EnumFacing side);
}

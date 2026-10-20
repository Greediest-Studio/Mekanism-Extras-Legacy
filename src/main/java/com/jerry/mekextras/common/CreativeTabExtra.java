package com.jerry.mekextras.common;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.common.registries.ExtraItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabExtra extends CreativeTabs {

    public CreativeTabExtra() {
        super(MekanismExtras.MODID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ExtraItems.INFINITE_CONTROL_CIRCUIT);
    }
}

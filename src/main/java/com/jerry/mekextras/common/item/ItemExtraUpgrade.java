package com.jerry.mekextras.common.item;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.ExtraUpgrade;
import com.jerry.mekextras.common.registries.ExtraUpgrades;
import mekanism.common.Upgrade;
import mekanism.common.item.ItemUpgrade;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ItemExtraUpgrade extends ItemUpgrade {

    private final ExtraUpgrade upgrade;

    public ItemExtraUpgrade(ExtraUpgrade upgrade, EnumRarity rarity) {
        super(resolveUpgrade(upgrade));
        this.upgrade = upgrade;
        setRarity(rarity);
    }

    public ExtraUpgrade getUpgrade() {
        return upgrade;
    }

    @Override
    public Upgrade getUpgradeType(ItemStack stack) {
        return super.getUpgradeType(stack);
    }

    private static Upgrade resolveUpgrade(ExtraUpgrade upgrade) {
        ExtraUpgrades.init();
        Upgrade registered = Upgrade.byName(MekanismExtras.MODID + ":" + upgrade.getName());
        return Objects.requireNonNull(registered, "Upgrade is not registered: " + upgrade.getName());
    }
}

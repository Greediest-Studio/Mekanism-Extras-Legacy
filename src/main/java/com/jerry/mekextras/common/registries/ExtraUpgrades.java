package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import mekanism.api.EnumColor;
import mekanism.common.Upgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * The upgrades added by Mekanism Extras Legacy.
 * <p>
 * Mekanism CE keeps its upgrades in an extensible registry, so the extra upgrades are registered there instead of
 * being bolted onto Mekanism's own upgrade handling. Once registered they behave like Mekanism's own upgrades: they
 * can be installed, are saved with the machine and show up in the upgrade tab of a machine's GUI.
 */
public class ExtraUpgrades {

    /**
     * Lets the advanced electric pump turn uranium and naquadah hexafluoride into their rich fuel variants.
     */
    public static final Upgrade IONIC_MEMBRANE = Upgrade.builder(
                new ResourceLocation(MekanismExtras.MODID, com.jerry.mekextras.api.ExtraUpgrade.IONIC_MEMBRANE.getName()),
                MekanismExtras.MODID + "." + com.jerry.mekextras.api.ExtraUpgrade.IONIC_MEMBRANE.getName())
          .maxInstalled(1)
          .maxItemStackSize(1)
          .color(EnumColor.WHITE)
          .stack(count -> new ItemStack(ExtraItems.IONIC_MEMBRANE_UPGRADE, count))
          .register();

    /**
     * Creative tier machines: no energy use and no working time.
     */
    public static final Upgrade CREATIVE = Upgrade.builder(
                new ResourceLocation(MekanismExtras.MODID, com.jerry.mekextras.api.ExtraUpgrade.CREATIVE.getName()),
                MekanismExtras.MODID + "." + com.jerry.mekextras.api.ExtraUpgrade.CREATIVE.getName())
          .maxInstalled(1)
          .maxItemStackSize(1)
          .color(EnumColor.PURPLE)
          .stack(count -> new ItemStack(ExtraItems.CREATIVE_UPGRADE, count))
          .register();

    private ExtraUpgrades() {
    }

    /**
     * Forces the static initializer of this class so the upgrades are registered before items are registered.
     */
    public static void init() {
    }

    public static boolean isCreativeInstalled(mekanism.common.base.IUpgradeTile tile) {
        return tile != null && tile.isUpgradeInstalled(CREATIVE);
    }

    public static boolean isIonicMembraneInstalled(mekanism.common.base.IUpgradeTile tile) {
        return tile != null && tile.isUpgradeInstalled(IONIC_MEMBRANE);
    }
}

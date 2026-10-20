package com.jerry.mekextras.common;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.tier.AdvancedTier;
import mekanism.common.util.LangUtils;

/**
 * Translation keys used by Mekanism Extras Legacy.
 */
public class ExtraLang {

    public static final String MOD_NAME = "constants." + MekanismExtras.MODID + ".mod_name";

    public static String tier(AdvancedTier tier) {
        return "tier." + MekanismExtras.MODID + "." + tier.getLowerName();
    }

    public static String item(String name) {
        return "item." + MekanismExtras.MODID + "." + name;
    }

    public static String block(String name) {
        return "tile." + MekanismExtras.MODID + "." + name + ".name";
    }

    public static String gas(String name) {
        return "gas." + MekanismExtras.MODID + "." + name;
    }

    public static String infuse(String name) {
        return "infuse." + MekanismExtras.MODID + "." + name;
    }

    public static String oreGas(String name) {
        return "oregas." + name;
    }

    public static String localize(String key) {
        return LangUtils.localize(key);
    }
}

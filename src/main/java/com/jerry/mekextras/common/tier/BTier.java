package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum BTier implements IAdvancedTier, IStringSerializable {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 1_048_576),
    SUPREME(AdvancedTier.SUPREME, 8_388_608),
    COSMIC(AdvancedTier.COSMIC, 134_217_728),
    INFINITE(AdvancedTier.INFINITE, Integer.MAX_VALUE);

    private final AdvancedTier advancedTier;
    private final int storage;

    BTier(AdvancedTier advancedTier, int storage) {
        this.advancedTier = advancedTier;
        this.storage = storage;
    }

    public int getStorage() {
        return storage;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static BTier byIndex(int index) {
        BTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

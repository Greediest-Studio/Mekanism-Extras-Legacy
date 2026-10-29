package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.ColourRGBA;
import mekanism.common.tier.BaseTier;

/**
 * Cable tiers above Mekanism's own {@link mekanism.common.tier.CableTier#ULTIMATE}.
 */
public enum ExtraCableTier implements IAdvancedTier {

    FINAL(AdvancedTier.FINAL, 64_000),
    ARCANE(AdvancedTier.ARCANE, 320_000),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 1_600_000),
    SUPREME(AdvancedTier.SUPREME, 8_000_000),
    COSMIC(AdvancedTier.COSMIC, 40_000_000),
    INFINITE(AdvancedTier.INFINITE, 200_000_000);

    private final AdvancedTier advancedTier;
    private final int capacity;

    ExtraCableTier(AdvancedTier advancedTier, int capacity) {
        this.advancedTier = advancedTier;
        this.capacity = capacity;
    }

    public int getCableCapacity() {
        return capacity;
    }

    public int getBaseCapacity() {
        return capacity;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    public static ExtraCableTier byIndex(int index) {
        ExtraCableTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }

    public static ExtraCableTier get(BaseTier tier) {
        if (tier == null) {
            return ABSOLUTE;
        }
        ExtraCableTier[] values = values();
        int index = tier.ordinal() - BaseTier.ULTIMATE.ordinal();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

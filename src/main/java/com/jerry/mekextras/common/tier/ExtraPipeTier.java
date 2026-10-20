package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;

/**
 * Mechanical pipe tiers above Mekanism's own {@link mekanism.common.tier.PipeTier#ULTIMATE}.
 */
public enum ExtraPipeTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 256_000, 25_600),
    SUPREME(AdvancedTier.SUPREME, 1_024_000, 102_400),
    COSMIC(AdvancedTier.COSMIC, 4_096_000, 409_600),
    INFINITE(AdvancedTier.INFINITE, 16_384_000, 1_638_400);

    private final AdvancedTier advancedTier;
    private final int capacity;
    private final int pull;

    ExtraPipeTier(AdvancedTier advancedTier, int capacity, int pull) {
        this.advancedTier = advancedTier;
        this.capacity = capacity;
        this.pull = pull;
    }

    public int getPipeCapacity() {
        return capacity;
    }

    public int getPipePullAmount() {
        return pull;
    }

    public int getBaseCapacity() {
        return capacity;
    }

    public int getBasePull() {
        return pull;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    public static ExtraPipeTier byIndex(int index) {
        ExtraPipeTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }

    public static ExtraPipeTier get(BaseTier tier) {
        if (tier == null) {
            return ABSOLUTE;
        }
        ExtraPipeTier[] values = values();
        int index = tier.ordinal() - BaseTier.ULTIMATE.ordinal();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;

/**
 * Pressurized tube tiers above Mekanism's own {@link mekanism.common.tier.TubeTier#ULTIMATE}.
 */
public enum ExtraTubeTier implements IAdvancedTier {

    FINAL(AdvancedTier.FINAL, 4_096, 1_024),
    ARCANE(AdvancedTier.ARCANE, 16_384, 4_096),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 65_536, 16_384),
    SUPREME(AdvancedTier.SUPREME, 262_144, 65_536),
    COSMIC(AdvancedTier.COSMIC, 1_048_576, 262_144),
    INFINITE(AdvancedTier.INFINITE, 4_194_304, 1_048_576);

    private final AdvancedTier advancedTier;
    private final int capacity;
    private final int pull;

    ExtraTubeTier(AdvancedTier advancedTier, int capacity, int pull) {
        this.advancedTier = advancedTier;
        this.capacity = capacity;
        this.pull = pull;
    }

    public int getTubeCapacity() {
        return capacity;
    }

    public int getTubePullAmount() {
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

    public static ExtraTubeTier byIndex(int index) {
        ExtraTubeTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }

    public static ExtraTubeTier get(BaseTier tier) {
        if (tier == null) {
            return ABSOLUTE;
        }
        ExtraTubeTier[] values = values();
        int index = tier.ordinal() - BaseTier.ULTIMATE.ordinal();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

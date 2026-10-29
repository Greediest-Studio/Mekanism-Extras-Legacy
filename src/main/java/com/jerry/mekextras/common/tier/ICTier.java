package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum ICTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 512_000_000_000L),
    ARCANE(AdvancedTier.ARCANE, 4_096_000_000_000L),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 32_768_000_000_000L),
    SUPREME(AdvancedTier.SUPREME, 262_144_000_000_000L),
    COSMIC(AdvancedTier.COSMIC, 2_097_152_000_000_000L),
    INFINITE(AdvancedTier.INFINITE, Long.MAX_VALUE);

    private final AdvancedTier advancedTier;
    private final long maxEnergy;

    ICTier(AdvancedTier advancedTier, long maxEnergy) {
        this.advancedTier = advancedTier;
        this.maxEnergy = maxEnergy;
    }

    public long getMaxEnergy() {
        return maxEnergy;
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

    public static ICTier byIndex(int index) {
        ICTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

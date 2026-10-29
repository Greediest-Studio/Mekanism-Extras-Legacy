package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum ECTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 64_000_000L, 64_000L),
    ARCANE(AdvancedTier.ARCANE, 256_000_000L, 256_000L),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 1_024_000_000L, 1_024_000L),
    SUPREME(AdvancedTier.SUPREME, 4_096_000_000L, 4_096_000L),
    COSMIC(AdvancedTier.COSMIC, 16_384_000_000L, 16_384_000L),
    INFINITE(AdvancedTier.INFINITE, 65_536_000_000L, 65_536_000L);

    private final AdvancedTier advancedTier;
    private final long maxEnergy;
    private final long output;

    ECTier(AdvancedTier advancedTier, long maxEnergy, long output) {
        this.advancedTier = advancedTier;
        this.maxEnergy = maxEnergy;
        this.output = output;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public long getOutput() {
        return output;
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

    public static ECTier byIndex(int index) {
        ECTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

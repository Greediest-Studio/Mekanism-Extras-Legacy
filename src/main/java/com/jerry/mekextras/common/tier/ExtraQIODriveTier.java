package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum ExtraQIODriveTier implements IAdvancedTier, IStringSerializable {

    COLLAPSE(AdvancedTier.ABSOLUTE, 512_000_000_000L, 131_072),
    GAMMA(AdvancedTier.SUPREME, 65_536_000_000_000L, 2_097_152),
    BLACK_HOLE(AdvancedTier.COSMIC, 33_554_432_000_000_000L, 33_554_432),
    SINGULARITY(AdvancedTier.INFINITE, Long.MAX_VALUE, Integer.MAX_VALUE);

    private final AdvancedTier advancedTier;
    private final long count;
    private final int types;

    ExtraQIODriveTier(AdvancedTier advancedTier, long count, int types) {
        this.advancedTier = advancedTier;
        this.count = count;
        this.types = types;
    }

    public long getCount() {
        return count;
    }

    public int getTypes() {
        return types;
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

    public static ExtraQIODriveTier byIndex(int index) {
        ExtraQIODriveTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : COLLAPSE;
    }
}

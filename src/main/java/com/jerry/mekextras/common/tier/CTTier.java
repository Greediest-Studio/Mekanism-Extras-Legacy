package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CTTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 4_096_000L, 2_048_000L),
    ARCANE(AdvancedTier.ARCANE, 32_768_000L, 16_384_000L),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 131_072_000L, 65_536_000L),
    SUPREME(AdvancedTier.SUPREME, 4_194_304_000L, 2_097_150_000L),
    COSMIC(AdvancedTier.COSMIC, 268_435_456_000L, 134_217_728_000L),
    INFINITE(AdvancedTier.INFINITE, 34_359_738_368_000L, 17_179_869_184_000L);

    private final AdvancedTier advancedTier;
    private final long storage;
    private final long output;

    CTTier(AdvancedTier advancedTier, long storage, long output) {
        this.advancedTier = advancedTier;
        this.storage = storage;
        this.output = output;
    }

    public long getStorage() {
        return storage;
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

    public static CTTier byIndex(int index) {
        CTTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum FTTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 64_000, 32_000),
    ARCANE(AdvancedTier.ARCANE, 512_000, 256_000),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 4_096_000, 2_048_000),
    SUPREME(AdvancedTier.SUPREME, 32_768_000, 16_384_000),
    COSMIC(AdvancedTier.COSMIC, 262_144_000, 131_072_000),
    INFINITE(AdvancedTier.INFINITE, 2_097_152_000, 1_048_576_000);

    private final AdvancedTier advancedTier;
    private final int storage;
    private final int output;

    FTTier(AdvancedTier advancedTier, int storage, int output) {
        this.advancedTier = advancedTier;
        this.storage = storage;
        this.output = output;
    }

    public int getStorage() {
        return storage;
    }

    public int getOutput() {
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

    public static FTTier byIndex(int index) {
        FTTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

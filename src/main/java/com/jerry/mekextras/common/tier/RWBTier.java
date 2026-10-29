package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum RWBTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 128_000L, 40, 1),
    ARCANE(AdvancedTier.ARCANE, 512_000L, 30, 2),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 2_048_000L, 20, 4),
    SUPREME(AdvancedTier.SUPREME, 8_192_000L, 10, 16),
    COSMIC(AdvancedTier.COSMIC, 32_768_000L, 5, 64),
    INFINITE(AdvancedTier.INFINITE, 131_072_000L, 5, 256);

    private final AdvancedTier advancedTier;
    private final long storage;
    private final int processTicks;
    private final long decayAmount;

    RWBTier(AdvancedTier advancedTier, long storage, int processTicks, long decayAmount) {
        this.advancedTier = advancedTier;
        this.storage = storage;
        this.processTicks = processTicks;
        this.decayAmount = decayAmount;
    }

    public long getStorage() {
        return storage;
    }

    public int getProcessTicks() {
        return processTicks;
    }

    public long getDecayAmount() {
        return decayAmount;
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

    public static RWBTier byIndex(int index) {
        RWBTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

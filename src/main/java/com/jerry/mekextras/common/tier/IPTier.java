package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum IPTier implements IAdvancedTier, IStringSerializable {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 1_048_576_000L),
    SUPREME(AdvancedTier.SUPREME, 8_388_608_000L),
    COSMIC(AdvancedTier.COSMIC, 67_108_864_000L),
    INFINITE(AdvancedTier.INFINITE, 536_870_912_000L);

    private final AdvancedTier advancedTier;
    private final long output;

    IPTier(AdvancedTier advancedTier, long output) {
        this.advancedTier = advancedTier;
        this.output = output;
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

    public static IPTier byIndex(int index) {
        IPTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

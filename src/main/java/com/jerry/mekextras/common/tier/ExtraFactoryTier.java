package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum ExtraFactoryTier implements IAdvancedTier, IStringSerializable {

    FINAL(AdvancedTier.FINAL, 11),
    ARCANE(AdvancedTier.ARCANE, 13),
    ABSOLUTE(AdvancedTier.ABSOLUTE, 15),
    SUPREME(AdvancedTier.SUPREME, 17),
    COSMIC(AdvancedTier.COSMIC, 19),
    INFINITE(AdvancedTier.INFINITE, 21);

    public final int processes;
    private final AdvancedTier advancedTier;

    ExtraFactoryTier(AdvancedTier advancedTier, int processes) {
        this.advancedTier = advancedTier;
        this.processes = processes;
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

    public static ExtraFactoryTier byIndex(int index) {
        ExtraFactoryTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : FINAL;
    }
}

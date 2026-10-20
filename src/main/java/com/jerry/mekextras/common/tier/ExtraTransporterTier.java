package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;

/**
 * Logistical transporter tiers above Mekanism's own {@link mekanism.common.tier.TransporterTier#ULTIMATE}.
 */
public enum ExtraTransporterTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 30, 4),
    SUPREME(AdvancedTier.SUPREME, 40, 8),
    COSMIC(AdvancedTier.COSMIC, 50, 16),
    INFINITE(AdvancedTier.INFINITE, 60, 32);

    private final AdvancedTier advancedTier;
    private final int speed;
    private final int pullAmount;

    ExtraTransporterTier(AdvancedTier advancedTier, int speed, int pullAmount) {
        this.advancedTier = advancedTier;
        this.speed = speed;
        this.pullAmount = pullAmount;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPullAmount() {
        return pullAmount;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    public static ExtraTransporterTier byIndex(int index) {
        ExtraTransporterTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

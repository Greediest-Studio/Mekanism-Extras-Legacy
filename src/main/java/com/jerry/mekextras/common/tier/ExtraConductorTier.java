package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.ColourRGBA;
import mekanism.common.tier.BaseTier;

/**
 * Thermodynamic conductor tiers above Mekanism's own {@link mekanism.common.tier.ConductorTier#ULTIMATE}.
 */
public enum ExtraConductorTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 5, 10, 500_000, new ColourRGBA(0.2, 0.2, 0.2, 1)),
    SUPREME(AdvancedTier.SUPREME, 5, 40, 2_500_000, new ColourRGBA(0.2, 0.2, 0.2, 1)),
    COSMIC(AdvancedTier.COSMIC, 5, 160, 12_500_000, new ColourRGBA(0.2, 0.2, 0.2, 1)),
    INFINITE(AdvancedTier.INFINITE, 5, 640, 62_500_000, new ColourRGBA(0.2, 0.2, 0.2, 1));

    private final AdvancedTier advancedTier;
    private final double inverseConduction;
    private final double heatCapacity;
    private final double inverseConductionInsulation;
    private final ColourRGBA colour;

    ExtraConductorTier(AdvancedTier advancedTier, double inverseConduction, double heatCapacity, double inverseConductionInsulation, ColourRGBA colour) {
        this.advancedTier = advancedTier;
        this.inverseConduction = inverseConduction;
        this.heatCapacity = heatCapacity;
        this.inverseConductionInsulation = inverseConductionInsulation;
        this.colour = colour;
    }

    public double getInverseConduction() {
        return inverseConduction;
    }

    public double getHeatCapacity() {
        return heatCapacity;
    }

    @Deprecated
    public double getInverseHeatCapacity() {
        return heatCapacity;
    }

    public double getInverseConductionInsulation() {
        return inverseConductionInsulation;
    }

    public ColourRGBA getBaseColour() {
        return colour;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    public static ExtraConductorTier byIndex(int index) {
        ExtraConductorTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }

    public static ExtraConductorTier get(BaseTier tier) {
        if (tier == null) {
            return ABSOLUTE;
        }
        ExtraConductorTier[] values = values();
        int index = tier.ordinal() - BaseTier.ULTIMATE.ordinal();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

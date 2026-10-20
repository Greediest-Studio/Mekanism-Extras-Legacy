package com.jerry.mekextras.common.tier;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.IAdvancedTier;
import mekanism.common.tier.BaseTier;
import mekanism.common.tier.GasTankTier;

import java.util.Locale;

/**
 * Tiers used by the MoreMachine based machines of Mekanism Extras Legacy. The process counts continue the line of
 * MoreMachine's own tiers (3/5/7/9).
 */
public enum ExtraMachineTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 11),
    SUPREME(AdvancedTier.SUPREME, 13),
    COSMIC(AdvancedTier.COSMIC, 15),
    INFINITE(AdvancedTier.INFINITE, 17);

    public final int processes;
    private final AdvancedTier advancedTier;

    ExtraMachineTier(AdvancedTier advancedTier, int processes) {
        this.advancedTier = advancedTier;
        this.processes = processes;
    }

    /**
     * @param baseTicks the base machine's cycle time
     * @return the cycle time for this tier (each tier is 25% faster, never below one tick)
     */
    public int ticksRequired(int baseTicks) {
        double factor = 1 - 0.25 * ordinal();
        return Math.max(1, (int) Math.round(baseTicks * factor));
    }

    public GasTankTier getGasTankTier() {
        return GasTankTier.ULTIMATE;
    }

    public boolean canUpgradeTo(BaseTier upgradeTier) {
        return false;
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Legacy MoreMachine only ships basic/advanced/elite/ultimate detail atlases. Those atlases are overlays on
     * top of the shared machine body, so the four Legacy tiers continue that sequence.
     */
    public String getDetailTextureSuffix() {
        switch (this) {
            case ABSOLUTE:
                return "basic";
            case SUPREME:
                return "advanced";
            case COSMIC:
                return "elite";
            case INFINITE:
                return "ultimate";
            default:
                return "basic";
        }
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return advancedTier;
    }

    @Override
    public BaseTier getBaseTier() {
        return advancedTier.getBaseTier();
    }

    public static ExtraMachineTier byIndex(int index) {
        ExtraMachineTier[] values = values();
        return index >= 0 && index < values.length ? values[index] : ABSOLUTE;
    }
}

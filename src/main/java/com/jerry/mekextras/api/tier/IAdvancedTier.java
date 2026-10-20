package com.jerry.mekextras.api.tier;

import mekanism.common.tier.ITier;

/**
 * Tiers added by Mekanism Extras. These are ordered after Mekanism's own {@link mekanism.common.tier.BaseTier#ULTIMATE}.
 */
public interface IAdvancedTier extends ITier {

    /**
     * @return the advanced tier this object belongs to
     */
    AdvancedTier getAdvanceTier();
}

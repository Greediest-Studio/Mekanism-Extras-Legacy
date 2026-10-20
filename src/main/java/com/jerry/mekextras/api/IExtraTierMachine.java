package com.jerry.mekextras.api;

import com.jerry.mekextras.api.tier.AdvancedTier;
import net.minecraft.block.Block;

/**
 * Implemented by every machine added by Mekanism Extras so that the extra tiers (Absolute and above) can be installed
 * with the alloys and tier installers added by this mod.
 */
public interface IExtraTierMachine {

    AdvancedTier getExtraTier();

    void setExtraTier(AdvancedTier tier);

    /**
     * @return the block of this machine in the given tier, used when upgrading a placed machine
     */
    Block getBlockForTier(AdvancedTier tier);

    /**
     * @return whether the given tier is exactly one step above this machine's current tier
     */
    default boolean canInstallExtraTier(AdvancedTier tier) {
        return tier != null && getExtraTier() != null && tier.ordinal() == getExtraTier().ordinal() + 1;
    }
}

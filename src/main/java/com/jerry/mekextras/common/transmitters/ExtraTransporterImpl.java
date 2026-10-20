package com.jerry.mekextras.common.transmitters;

import com.jerry.mekextras.common.tier.ExtraTransporterTier;
import mekanism.common.tier.TransporterTier;
import mekanism.common.transmitters.TransporterImpl;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporter;

/**
 * Logistical transporter implementation carrying one of our extra tiers. Mekanism's interface only knows its own tier
 * enum, so the delegate reports {@link TransporterTier#ULTIMATE} there while speed and throughput come from our tier.
 */
public class ExtraTransporterImpl extends TransporterImpl {

    private final ExtraTransporterTier tier;

    public ExtraTransporterImpl(TileEntityLogisticalTransporter tile, ExtraTransporterTier tier) {
        super(tile);
        this.tier = tier;
    }

    public ExtraTransporterTier getExtraTier() {
        return tier;
    }

    @Override
    public TransporterTier getTier() {
        return TransporterTier.ULTIMATE;
    }

    @Override
    public int getSpeed() {
        return tier.getSpeed();
    }

    @Override
    public int getPullAmount() {
        return tier.getPullAmount();
    }
}

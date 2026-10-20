package com.jerry.mekextras.common.tile.transmitter;

import com.jerry.mekextras.common.tier.ExtraTransporterTier;
import com.jerry.mekextras.common.transmitters.ExtraTransporterImpl;
import mekanism.api.TileNetworkList;
import mekanism.common.tier.AlloyTier;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporter;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Extra tier logistical transporter. Uses our own transmitter delegate so that speed and throughput scale with our tier.
 */
public class TileEntityExtraLogisticalTransporter extends TileEntityLogisticalTransporter {

    private ExtraTransporterTier tier = ExtraTransporterTier.ABSOLUTE;

    public TileEntityExtraLogisticalTransporter() {
    }

    public TileEntityExtraLogisticalTransporter(ExtraTransporterTier tier) {
        this.tier = tier;
        this.transmitterDelegate = new ExtraTransporterImpl(this, tier);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        if (nbtTags.hasKey("extraTransporterTier")) {
            tier = ExtraTransporterTier.byIndex(nbtTags.getInteger("extraTransporterTier"));
            transmitterDelegate = new ExtraTransporterImpl(this, tier);
        }
        super.readCustomNBT(nbtTags);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("extraTransporterTier", tier.ordinal());
    }

    public ExtraTransporterTier getTransporterTier() {
        return tier;
    }

    @Override
    public boolean upgrade(AlloyTier tierOrdinal) {
        //Mekanism's Basic/Advanced/Elite/Ultimate alloys do not define the Extra tier ladder.
        return false;
    }
}

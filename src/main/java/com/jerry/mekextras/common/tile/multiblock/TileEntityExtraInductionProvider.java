package com.jerry.mekextras.common.tile.multiblock;

import io.netty.buffer.ByteBuf;
import mekanism.api.TileNetworkList;
import com.jerry.mekextras.common.content.matrix.SynchronizedExtraMatrixData;
import com.jerry.mekextras.common.tier.IPTier;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityExtraInductionProvider extends TileEntityBasicBlock {

    public IPTier tier = IPTier.ABSOLUTE;
    private SynchronizedExtraMatrixData matrix;
    private long transferTick = Long.MIN_VALUE;
    private double inputUsed;
    private double outputUsed;

    public double getTransferCapacity() {
        return tier.getOutput();
    }

    public double getRemainingTransfer(long tick, boolean input) {
        if (transferTick != tick) {
            transferTick = tick;
            inputUsed = 0;
            outputUsed = 0;
        }
        return Math.max(0, getTransferCapacity() - (input ? inputUsed : outputUsed));
    }

    public double useTransfer(long tick, double amount, boolean input) {
        if (!Double.isFinite(amount) || amount <= 0) return 0;
        double accepted = Math.min(amount, getRemainingTransfer(tick, input));
        if (accepted <= 0 || !Double.isFinite(accepted)) return 0;
        double used = input ? inputUsed : outputUsed;
        double updated = used + accepted;
        // Round usage towards exhaustion: a sub-ULP transfer must not get a free allowance.
        if (updated - used < accepted) updated = Math.nextUp(updated);
        updated = Math.min(updated, getTransferCapacity());
        if (input) inputUsed = updated;
        else outputUsed = updated;
        return accepted;
    }

    public void bindMatrix(SynchronizedExtraMatrixData owner) {
        if (matrix != null && matrix != owner) matrix.setFormed(false);
        matrix = owner;
    }

    public void unbindMatrix(SynchronizedExtraMatrixData owner) {
        if (matrix == owner) matrix = null;
    }

    @Override
    public void onChunkUnload() {
        if (matrix != null) matrix.internalRemoved();
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        if (matrix != null) matrix.internalRemoved();
        super.invalidate();
    }


    public String getName() {
        return com.jerry.mekextras.common.util.ExtraUtils.getTierName(tier.getAdvanceTier()) + " " + LangUtils.localize("tile.mekanism_extras.reinforced_induction_provider.name");
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            IPTier prevTier = tier;
            tier = MekanismUtils.getByIndex(IPTier.values(), dataStream.readInt(), tier);
            if (prevTier != tier) {
                MekanismUtils.updateBlock(world, getPos());
            }
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        return data;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        tier = MekanismUtils.getByIndex(IPTier.values(), nbtTags.getInteger("tier"), tier);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
    }
}

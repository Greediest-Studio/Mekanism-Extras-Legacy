package com.jerry.mekextras.common.content.matrix;

import mekanism.common.multiblock.MultiblockCache;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class ExtraMatrixCache extends MultiblockCache<SynchronizedExtraMatrixData> {

    @Override
    public void validateCapacity(SynchronizedExtraMatrixData target) throws IOException {
        super.validateCapacity(target);
        requireMerge(target.hasValidEnergy(), "Induction cells have invalid energy or matrix capacity");
    }

    @Override
    public void apply(SynchronizedExtraMatrixData data) {
        applyInventory(data);
    }

    @Override
    public void sync(SynchronizedExtraMatrixData data) {
        data.flushEnergy();
        syncInventory(data);
    }

    @Override
    public void load(NBTTagCompound nbtTags) {
        loadInventory(nbtTags);
    }

    @Override
    public void save(NBTTagCompound nbtTags) {
        saveInventory(nbtTags);
    }
}

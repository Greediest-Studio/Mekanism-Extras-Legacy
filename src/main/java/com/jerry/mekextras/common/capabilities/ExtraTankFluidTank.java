package com.jerry.mekextras.common.capabilities;

import com.jerry.mekextras.common.tier.FTTier;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.IntSupplier;

/**
 * Fluid tank used by the extra fluid tanks. Mirrors Mekanism's own {@code FluidTankFluidTank} but the capacity and
 * transfer rate come from our own tier instead of Mekanism's {@code FluidTankTier}.
 */
public class ExtraTankFluidTank extends BasicFluidTank {

    public static ExtraTankFluidTank create(IntSupplier capacity, IntSupplier rate, @Nullable IContentsListener listener) {
        return new ExtraTankFluidTank(capacity, rate, listener);
    }

    private final IntSupplier rate;

    private ExtraTankFluidTank(IntSupplier capacity, IntSupplier rate, @Nullable IContentsListener listener) {
        super(capacity.getAsInt(), alwaysTrueBi, alwaysTrueBi, alwaysTrue, listener);
        this.rate = rate;
        this.capacitySupplier = capacity;
    }

    private final IntSupplier capacitySupplier;

    @Override
    protected int getRate(@Nullable AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? rate.getAsInt() : super.getRate(automationType);
    }

    @Override
    public int getCapacity() {
        return capacitySupplier.getAsInt();
    }

    public void setFluid(@Nullable FluidStack stack) {
        setStackUnchecked(stack);
    }

    public ExtraTankFluidTank readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("Empty")) {
            setFluid(FluidStack.loadFluidStackFromNBT(nbt));
        } else {
            setFluid(null);
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (stored != null) {
            stored.writeToNBT(nbt);
        } else {
            nbt.setBoolean("Empty", true);
        }
        return nbt;
    }
}

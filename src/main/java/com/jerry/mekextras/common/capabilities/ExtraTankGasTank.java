package com.jerry.mekextras.common.capabilities;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.gas.Gas;
import mekanism.common.capabilities.gas.BasicGasTank;

import javax.annotation.Nullable;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Gas (chemical) tank used by the extra chemical tanks. Mirrors Mekanism's own {@code GasTankGasTank} but capacity and
 * transfer rate come from our own tier instead of Mekanism's {@code GasTankTier}.
 */
public class ExtraTankGasTank extends BasicGasTank {

    public static ExtraTankGasTank create(IntSupplier capacity, IntSupplier rate, Predicate<Gas> validator, @Nullable IContentsListener listener) {
        return new ExtraTankGasTank(capacity, rate, validator, listener);
    }

    private final IntSupplier capacity;
    private final IntSupplier rate;

    private ExtraTankGasTank(IntSupplier capacity, IntSupplier rate, Predicate<Gas> validator, @Nullable IContentsListener listener) {
        super(capacity.getAsInt(), alwaysTrueBi, alwaysTrueBi, validator, listener);
        this.capacity = capacity;
        this.rate = rate;
    }

    @Override
    protected int getInsertRate(@Nullable AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? rate.getAsInt() : super.getInsertRate(automationType);
    }

    @Override
    protected int getExtractRate(@Nullable AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? rate.getAsInt() : super.getExtractRate(automationType);
    }

    @Override
    public int getCapacity() {
        return capacity.getAsInt();
    }
}

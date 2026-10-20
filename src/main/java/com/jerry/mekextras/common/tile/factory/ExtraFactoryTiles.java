package com.jerry.mekextras.common.tile.factory;

import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import mekanism.common.block.states.BlockStateMachine.MachineType;

/**
 * One tile entity class per extra factory tier.
 * <p>
 * Chunks are loaded through the no argument constructor of the registered class, so the tier has to be part of the
 * class itself. The recipe type of a factory is stored in NBT and restored on load.
 */
public final class ExtraFactoryTiles {

    private ExtraFactoryTiles() {
    }

    private static MachineType machineType(ExtraFactoryTier tier) {
        switch (tier) {
            case SUPREME:
                return MachineType.ADVANCED_FACTORY;
            case COSMIC:
                return MachineType.ELITE_FACTORY;
            case INFINITE:
                return MachineType.ULTIMATE_FACTORY;
            case ABSOLUTE:
            default:
                return MachineType.BASIC_FACTORY;
        }
    }

    public static TileEntityExtraFactory forTier(ExtraFactoryTier tier) {
        switch (tier) {
            case SUPREME:
                return new Supreme();
            case COSMIC:
                return new Cosmic();
            case INFINITE:
                return new Infinite();
            case ABSOLUTE:
            default:
                return new Absolute();
        }
    }

    public static final class Absolute extends TileEntityExtraFactory {
        public Absolute() {
            super(ExtraFactoryTier.ABSOLUTE, machineType(ExtraFactoryTier.ABSOLUTE));
        }
    }

    public static final class Supreme extends TileEntityExtraFactory {
        public Supreme() {
            super(ExtraFactoryTier.SUPREME, machineType(ExtraFactoryTier.SUPREME));
        }
    }

    public static final class Cosmic extends TileEntityExtraFactory {
        public Cosmic() {
            super(ExtraFactoryTier.COSMIC, machineType(ExtraFactoryTier.COSMIC));
        }
    }

    public static final class Infinite extends TileEntityExtraFactory {
        public Infinite() {
            super(ExtraFactoryTier.INFINITE, machineType(ExtraFactoryTier.INFINITE));
        }
    }
}

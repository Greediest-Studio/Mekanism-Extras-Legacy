package com.jerry.mekextras.common.tile.factory;

import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import mekanism.common.block.states.BlockStateMachine.MachineType;

/** One tile entity class per extra factory tier. */
public final class ExtraFactoryTiles {
    private ExtraFactoryTiles() {}
    private static MachineType machineType(ExtraFactoryTier tier) {
        switch (tier) {
            case ARCANE: return MachineType.ADVANCED_FACTORY;
            case SUPREME: return MachineType.ELITE_FACTORY;
            case ABSOLUTE:
            case COSMIC:
            case INFINITE: return MachineType.ULTIMATE_FACTORY;
            default: return MachineType.BASIC_FACTORY;
        }
    }
    public static TileEntityExtraFactory forTier(ExtraFactoryTier tier) {
        switch (tier) {
            case ARCANE: return new Arcane();
            case SUPREME: return new Supreme();
            case ABSOLUTE: return new Absolute();
            case COSMIC: return new Cosmic();
            case INFINITE: return new Infinite();
            default: return new Final();
        }
    }
    public static final class Final extends TileEntityExtraFactory { public Final() { super(ExtraFactoryTier.FINAL, machineType(ExtraFactoryTier.FINAL)); } }
    public static final class Arcane extends TileEntityExtraFactory { public Arcane() { super(ExtraFactoryTier.ARCANE, machineType(ExtraFactoryTier.ARCANE)); } }
    public static final class Supreme extends TileEntityExtraFactory { public Supreme() { super(ExtraFactoryTier.SUPREME, machineType(ExtraFactoryTier.SUPREME)); } }
    public static final class Absolute extends TileEntityExtraFactory { public Absolute() { super(ExtraFactoryTier.ABSOLUTE, machineType(ExtraFactoryTier.ABSOLUTE)); } }
    public static final class Cosmic extends TileEntityExtraFactory { public Cosmic() { super(ExtraFactoryTier.COSMIC, machineType(ExtraFactoryTier.COSMIC)); } }
    public static final class Infinite extends TileEntityExtraFactory { public Infinite() { super(ExtraFactoryTier.INFINITE, machineType(ExtraFactoryTier.INFINITE)); } }
}

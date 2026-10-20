package com.jerry.mekextras.common.tile.moremachine;

import com.jerry.mekextras.common.tier.ExtraMachineTier;
import net.minecraft.tileentity.TileEntity;

/**
 * One tile entity class per (machine, tier) pair.
 * <p>
 * Minecraft creates block entities through a no argument constructor when a chunk is loaded, so the tier of a machine
 * has to be encoded in its class, exactly like Mekanism and MoreMachine do it for their own tiered machines.
 */
public final class ExtraMoreMachineTiles {

    private ExtraMoreMachineTiles() {
    }

    public static final class CrystallizerAbsolute extends TileEntityExtraCrystallizer {
        public CrystallizerAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class CrystallizerSupreme extends TileEntityExtraCrystallizer {
        public CrystallizerSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class CrystallizerCosmic extends TileEntityExtraCrystallizer {
        public CrystallizerCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class CrystallizerInfinite extends TileEntityExtraCrystallizer {
        public CrystallizerInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class DissolutionChamberAbsolute extends TileEntityExtraDissolutionChamber {
        public DissolutionChamberAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class DissolutionChamberSupreme extends TileEntityExtraDissolutionChamber {
        public DissolutionChamberSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class DissolutionChamberCosmic extends TileEntityExtraDissolutionChamber {
        public DissolutionChamberCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class DissolutionChamberInfinite extends TileEntityExtraDissolutionChamber {
        public DissolutionChamberInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class ChemicalOxidizerAbsolute extends TileEntityExtraChemicalOxidizer {
        public ChemicalOxidizerAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class ChemicalOxidizerSupreme extends TileEntityExtraChemicalOxidizer {
        public ChemicalOxidizerSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class ChemicalOxidizerCosmic extends TileEntityExtraChemicalOxidizer {
        public ChemicalOxidizerCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class ChemicalOxidizerInfinite extends TileEntityExtraChemicalOxidizer {
        public ChemicalOxidizerInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class NutritionalLiquifierAbsolute extends TileEntityExtraNutritionalLiquifier {
        public NutritionalLiquifierAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class NutritionalLiquifierSupreme extends TileEntityExtraNutritionalLiquifier {
        public NutritionalLiquifierSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class NutritionalLiquifierCosmic extends TileEntityExtraNutritionalLiquifier {
        public NutritionalLiquifierCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class NutritionalLiquifierInfinite extends TileEntityExtraNutritionalLiquifier {
        public NutritionalLiquifierInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class OrganicFarmAbsolute extends TileEntityExtraOrganicFarm {
        public OrganicFarmAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class OrganicFarmSupreme extends TileEntityExtraOrganicFarm {
        public OrganicFarmSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class OrganicFarmCosmic extends TileEntityExtraOrganicFarm {
        public OrganicFarmCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class OrganicFarmInfinite extends TileEntityExtraOrganicFarm {
        public OrganicFarmInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class ChemicalWasherAbsolute extends TileEntityExtraChemicalWasher {
        public ChemicalWasherAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class ChemicalWasherSupreme extends TileEntityExtraChemicalWasher {
        public ChemicalWasherSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class ChemicalWasherCosmic extends TileEntityExtraChemicalWasher {
        public ChemicalWasherCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class ChemicalWasherInfinite extends TileEntityExtraChemicalWasher {
        public ChemicalWasherInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class ChemicalInfuserAbsolute extends TileEntityExtraChemicalInfuser {
        public ChemicalInfuserAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class ChemicalInfuserSupreme extends TileEntityExtraChemicalInfuser {
        public ChemicalInfuserSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class ChemicalInfuserCosmic extends TileEntityExtraChemicalInfuser {
        public ChemicalInfuserCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class ChemicalInfuserInfinite extends TileEntityExtraChemicalInfuser {
        public ChemicalInfuserInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static final class IsotopicCentrifugeAbsolute extends TileEntityExtraIsotopicCentrifuge {
        public IsotopicCentrifugeAbsolute() {
            super(ExtraMachineTier.ABSOLUTE);
        }
    }

    public static final class IsotopicCentrifugeSupreme extends TileEntityExtraIsotopicCentrifuge {
        public IsotopicCentrifugeSupreme() {
            super(ExtraMachineTier.SUPREME);
        }
    }

    public static final class IsotopicCentrifugeCosmic extends TileEntityExtraIsotopicCentrifuge {
        public IsotopicCentrifugeCosmic() {
            super(ExtraMachineTier.COSMIC);
        }
    }

    public static final class IsotopicCentrifugeInfinite extends TileEntityExtraIsotopicCentrifuge {
        public IsotopicCentrifugeInfinite() {
            super(ExtraMachineTier.INFINITE);
        }
    }

    public static TileEntity createCrystallizer(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new CrystallizerSupreme();
            case COSMIC:
                return new CrystallizerCosmic();
            case INFINITE:
                return new CrystallizerInfinite();
            case ABSOLUTE:
            default:
                return new CrystallizerAbsolute();
        }
    }

    public static TileEntity createDissolutionChamber(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new DissolutionChamberSupreme();
            case COSMIC:
                return new DissolutionChamberCosmic();
            case INFINITE:
                return new DissolutionChamberInfinite();
            case ABSOLUTE:
            default:
                return new DissolutionChamberAbsolute();
        }
    }

    public static TileEntity createChemicalOxidizer(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new ChemicalOxidizerSupreme();
            case COSMIC:
                return new ChemicalOxidizerCosmic();
            case INFINITE:
                return new ChemicalOxidizerInfinite();
            case ABSOLUTE:
            default:
                return new ChemicalOxidizerAbsolute();
        }
    }

    public static TileEntity createNutritionalLiquifier(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new NutritionalLiquifierSupreme();
            case COSMIC:
                return new NutritionalLiquifierCosmic();
            case INFINITE:
                return new NutritionalLiquifierInfinite();
            case ABSOLUTE:
            default:
                return new NutritionalLiquifierAbsolute();
        }
    }

    public static TileEntity createOrganicFarm(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new OrganicFarmSupreme();
            case COSMIC:
                return new OrganicFarmCosmic();
            case INFINITE:
                return new OrganicFarmInfinite();
            case ABSOLUTE:
            default:
                return new OrganicFarmAbsolute();
        }
    }

    public static TileEntity createChemicalWasher(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new ChemicalWasherSupreme();
            case COSMIC:
                return new ChemicalWasherCosmic();
            case INFINITE:
                return new ChemicalWasherInfinite();
            case ABSOLUTE:
            default:
                return new ChemicalWasherAbsolute();
        }
    }

    public static TileEntity createChemicalInfuser(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new ChemicalInfuserSupreme();
            case COSMIC:
                return new ChemicalInfuserCosmic();
            case INFINITE:
                return new ChemicalInfuserInfinite();
            case ABSOLUTE:
            default:
                return new ChemicalInfuserAbsolute();
        }
    }

    public static TileEntity createIsotopicCentrifuge(ExtraMachineTier tier) {
        switch (tier) {
            case SUPREME:
                return new IsotopicCentrifugeSupreme();
            case COSMIC:
                return new IsotopicCentrifugeCosmic();
            case INFINITE:
                return new IsotopicCentrifugeInfinite();
            case ABSOLUTE:
            default:
                return new IsotopicCentrifugeAbsolute();
        }
    }

}

package com.jerry.mekextras.common;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.common.inventory.container.ContainerExtraChemicalTank;
import com.jerry.mekextras.common.inventory.container.ContainerExtraEnergyCube;
import com.jerry.mekextras.common.inventory.container.ContainerExtraChemicalInfuser;
import com.jerry.mekextras.common.inventory.container.ContainerExtraChemicalOxidizer;
import com.jerry.mekextras.common.inventory.container.ContainerExtraChemicalWasher;
import com.jerry.mekextras.common.inventory.container.ContainerExtraIsotopicCentrifuge;
import com.jerry.mekextras.common.inventory.container.ContainerExtraCrystallizer;
import com.jerry.mekextras.common.inventory.container.ContainerExtraDissolutionChamber;
import com.jerry.mekextras.common.inventory.container.ContainerExtraFactory;
import com.jerry.mekextras.common.inventory.container.ContainerExtraNutritionalLiquifier;
import com.jerry.mekextras.common.inventory.container.ContainerExtraOrganicFarm;
import com.jerry.mekextras.common.inventory.container.ContainerExtraInductionMatrix;
import com.jerry.mekextras.common.inventory.container.ContainerExtraFluidTank;
import com.jerry.mekextras.common.tile.TileEntityExtraBin;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;
import com.jerry.mekextras.common.tile.TileEntityExtraEnergyCube;
import com.jerry.mekextras.common.tile.TileEntityExtraFluidTank;
import com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump;
import com.jerry.mekextras.common.tile.TileEntityLargeCapRadioactiveWasteBarrel;
import com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory;
import com.jerry.mekextras.common.tile.factory.ExtraFactoryTiles;
import com.jerry.mekextras.common.tile.moremachine.ExtraMoreMachineTiles;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalInfuser;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalOxidizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalWasher;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraIsotopicCentrifuge;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraCrystallizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraDissolutionChamber;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraNutritionalLiquifier;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraOrganicFarm;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCell;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionPort;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionProvider;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLaserFocusMatrix;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorPort;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraMechanicalPipe;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraPressurizedTube;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraThermodynamicConductor;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraLogisticalTransporter;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraUniversalCable;
import mekanism.common.base.IGuiProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.jerry.mekextras.common.util.MekanismMachineConfig;

public class CommonProxy implements IGuiProvider {

    protected static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        GameRegistry.registerTileEntity(clazz, new ResourceLocation(MekanismExtras.MODID, name));
    }

    public void preInit() {
    }

    public void init() {
    }

    public void postInit() {
    }

    public void registerTileEntities() {
        registerTileEntity(TileEntityExtraBin.class, "extra_bin");
        registerTileEntity(TileEntityExtraEnergyCube.class, "extra_energy_cube");
        registerTileEntity(TileEntityExtraFluidTank.class, "extra_fluid_tank");
        registerTileEntity(TileEntityExtraChemicalTank.class, "extra_chemical_tank");
        registerTileEntity(TileEntityLargeCapRadioactiveWasteBarrel.class, "extra_radioactive_waste_barrel");
        registerTileEntity(TileEntityExtraInductionCasing.class, "extra_induction_casing");
        registerTileEntity(TileEntityExtraInductionPort.class, "extra_induction_port");
        registerTileEntity(TileEntityExtraInductionCell.class, "extra_induction_cell");
        registerTileEntity(TileEntityExtraInductionProvider.class, "extra_induction_provider");
        registerTileEntity(TileEntityExtraReactorCasing.class, "extra_reactor_casing");
        registerTileEntity(TileEntityExtraReactorPort.class, "extra_reactor_port");
        registerTileEntity(TileEntityExtraReactorController.class, "extra_reactor_controller");
        registerTileEntity(TileEntityExtraReactorLogicAdapter.class, "extra_reactor_logic_adapter");
        registerTileEntity(TileEntityExtraReactorLaserFocusMatrix.class, "extra_laser_focus_matrix");
        //MoreMachine based machines: one tile entity class per (machine, tier) pair so that chunks can create them.
        if (MekanismMachineConfig.isMoreMachineEnabled("crystallizer")) {
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerFinal.class, "final_crystallizer");
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerArcane.class, "arcane_crystallizer");
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerSupreme.class, "supreme_crystallizer");
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerAbsolute.class, "absolute_crystallizer");
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerCosmic.class, "cosmic_crystallizer");
            registerTileEntity(ExtraMoreMachineTiles.CrystallizerInfinite.class, "infinite_crystallizer");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("dissolution_chamber")) {
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberFinal.class, "final_dissolution_chamber");
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberArcane.class, "arcane_dissolution_chamber");
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberSupreme.class, "supreme_dissolution_chamber");
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberAbsolute.class, "absolute_dissolution_chamber");
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberCosmic.class, "cosmic_dissolution_chamber");
            registerTileEntity(ExtraMoreMachineTiles.DissolutionChamberInfinite.class, "infinite_dissolution_chamber");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("chemical_oxidizer")) {
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerFinal.class, "final_chemical_oxidizer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerArcane.class, "arcane_chemical_oxidizer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerSupreme.class, "supreme_chemical_oxidizer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerAbsolute.class, "absolute_chemical_oxidizer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerCosmic.class, "cosmic_chemical_oxidizer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalOxidizerInfinite.class, "infinite_chemical_oxidizer");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("nutritional_liquifier")) {
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierFinal.class, "final_nutritional_liquifier");
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierArcane.class, "arcane_nutritional_liquifier");
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierSupreme.class, "supreme_nutritional_liquifier");
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierAbsolute.class, "absolute_nutritional_liquifier");
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierCosmic.class, "cosmic_nutritional_liquifier");
            registerTileEntity(ExtraMoreMachineTiles.NutritionalLiquifierInfinite.class, "infinite_nutritional_liquifier");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("organic_farm")) {
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmFinal.class, "final_organic_farm");
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmArcane.class, "arcane_organic_farm");
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmSupreme.class, "supreme_organic_farm");
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmAbsolute.class, "absolute_organic_farm");
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmCosmic.class, "cosmic_organic_farm");
            registerTileEntity(ExtraMoreMachineTiles.OrganicFarmInfinite.class, "infinite_organic_farm");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("chemical_washer")) {
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherFinal.class, "final_chemical_washer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherArcane.class, "arcane_chemical_washer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherSupreme.class, "supreme_chemical_washer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherAbsolute.class, "absolute_chemical_washer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherCosmic.class, "cosmic_chemical_washer");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalWasherInfinite.class, "infinite_chemical_washer");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("chemical_infuser")) {
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserFinal.class, "final_chemical_infuser");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserArcane.class, "arcane_chemical_infuser");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserSupreme.class, "supreme_chemical_infuser");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserAbsolute.class, "absolute_chemical_infuser");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserCosmic.class, "cosmic_chemical_infuser");
            registerTileEntity(ExtraMoreMachineTiles.ChemicalInfuserInfinite.class, "infinite_chemical_infuser");
        }
        if (MekanismMachineConfig.isMoreMachineEnabled("isotopic_centrifuge")) {
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeFinal.class, "final_isotopic_centrifuge");
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeArcane.class, "arcane_isotopic_centrifuge");
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeSupreme.class, "supreme_isotopic_centrifuge");
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeAbsolute.class, "absolute_isotopic_centrifuge");
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeCosmic.class, "cosmic_isotopic_centrifuge");
            registerTileEntity(ExtraMoreMachineTiles.IsotopicCentrifugeInfinite.class, "infinite_isotopic_centrifuge");
        }
        registerTileEntity(TileEntityExtraUniversalCable.class, "extra_universal_cable");
        registerTileEntity(TileEntityExtraMechanicalPipe.class, "extra_mechanical_pipe");
        registerTileEntity(TileEntityExtraPressurizedTube.class, "extra_pressurized_tube");
        registerTileEntity(TileEntityExtraThermodynamicConductor.class, "extra_thermodynamic_conductor");
        registerTileEntity(TileEntityExtraLogisticalTransporter.class, "extra_logistical_transporter");
        if (MekanismMachineConfig.isEnabled(mekanism.common.block.states.BlockStateMachine.MachineType.ELECTRIC_PUMP)) {
            registerTileEntity(TileEntityAdvancedElectricPump.class, "advanced_electric_pump");
        }
        //Factories: one tile entity class per tier, plus the legacy untiered name.
        if (MekanismMachineConfig.anyFactoryEnabled()) {
            registerTileEntity(ExtraFactoryTiles.Final.class, "extra_factory_final");
            registerTileEntity(ExtraFactoryTiles.Arcane.class, "extra_factory_arcane");
            registerTileEntity(ExtraFactoryTiles.Supreme.class, "extra_factory_supreme");
            registerTileEntity(ExtraFactoryTiles.Absolute.class, "extra_factory_absolute");
            registerTileEntity(ExtraFactoryTiles.Cosmic.class, "extra_factory_cosmic");
            registerTileEntity(ExtraFactoryTiles.Infinite.class, "extra_factory_infinite");
            registerTileEntity(TileEntityExtraFactory.class, "extra_factory");
        }
    }

    public void registerTESRs() {
    }

    public void registerRenders() {
    }

    @Override
    public Container getServerGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityExtraEnergyCube) {
            return new ContainerExtraEnergyCube(player.inventory, (TileEntityExtraEnergyCube) tile);
        }
        if (tile instanceof TileEntityExtraFluidTank) {
            return new ContainerExtraFluidTank(player.inventory, (TileEntityExtraFluidTank) tile);
        }
        if (tile instanceof TileEntityExtraInductionCasing) {
            return new ContainerExtraInductionMatrix(player.inventory, (TileEntityExtraInductionCasing) tile);
        }
        if (tile instanceof TileEntityExtraCrystallizer) {
            return new ContainerExtraCrystallizer(player.inventory, (TileEntityExtraCrystallizer) tile);
        }
        if (tile instanceof TileEntityExtraDissolutionChamber) {
            return new ContainerExtraDissolutionChamber(player.inventory, (TileEntityExtraDissolutionChamber) tile);
        }
        if (tile instanceof TileEntityExtraChemicalWasher) {
            return new ContainerExtraChemicalWasher(player.inventory, (TileEntityExtraChemicalWasher) tile);
        }
        if (tile instanceof TileEntityExtraChemicalInfuser) {
            return new ContainerExtraChemicalInfuser(player.inventory, (TileEntityExtraChemicalInfuser) tile);
        }
        if (tile instanceof TileEntityExtraIsotopicCentrifuge) {
            return new ContainerExtraIsotopicCentrifuge(player.inventory, (TileEntityExtraIsotopicCentrifuge) tile);
        }
        if (tile instanceof TileEntityExtraChemicalOxidizer) {
            return new ContainerExtraChemicalOxidizer(player.inventory, (TileEntityExtraChemicalOxidizer) tile);
        }
        if (tile instanceof TileEntityExtraNutritionalLiquifier) {
            return new ContainerExtraNutritionalLiquifier(player.inventory, (TileEntityExtraNutritionalLiquifier) tile);
        }
        if (tile instanceof TileEntityExtraOrganicFarm) {
            return new ContainerExtraOrganicFarm(player.inventory, (TileEntityExtraOrganicFarm) tile);
        }
        if (tile instanceof TileEntityExtraFactory) {
            return new ContainerExtraFactory(player.inventory, (TileEntityExtraFactory) tile);
        }
        if (tile instanceof TileEntityExtraChemicalTank) {
            return new ContainerExtraChemicalTank(player.inventory, (TileEntityExtraChemicalTank) tile);
        }
        if (tile instanceof com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController reactor && reactor.isFormed()) {
            return new com.jerry.mekextras.common.inventory.container.ContainerExtraReactorController(player.inventory, reactor);
        }
        if (tile instanceof com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter adapter) {
            return new mekanism.common.inventory.container.ContainerNull(player, adapter);
        }
        if (tile instanceof TileEntityAdvancedElectricPump) {
            return new com.jerry.mekextras.common.inventory.container.ContainerExtraAdvancedElectricPump(player.inventory, (TileEntityAdvancedElectricPump) tile);
        }
        if (mekanism.common.Mekanism.proxy != null) {
            Container gui = mekanism.common.Mekanism.proxy.getServerGui(ID, player, world, pos);
            if (gui != null) {
                return gui;
            }
        }
        return null;
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }
}

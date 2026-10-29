package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.block.BlockExtraBin;
import com.jerry.mekextras.common.block.BlockExtraAdvancedElectricPump;
import com.jerry.mekextras.common.block.BlockExtraChemicalTank;
import com.jerry.mekextras.common.block.BlockExtraEnergyCube;
import com.jerry.mekextras.common.block.BlockExtraFactory;
import com.jerry.mekextras.common.block.BlockExtraInductionCasing;
import com.jerry.mekextras.common.block.BlockExtraReactor;
import com.jerry.mekextras.common.block.BlockExtraInductionCell;
import com.jerry.mekextras.common.block.BlockExtraInductionProvider;
import com.jerry.mekextras.common.block.BlockExtraMoreMachine;
import com.jerry.mekextras.common.block.BlockExtraTransmitter;
import com.jerry.mekextras.common.block.BlockExtraFluidTank;
import com.jerry.mekextras.common.block.BlockExtraWasteBarrel;
import com.jerry.mekextras.common.block.BlockExtraOre;
import com.jerry.mekextras.common.block.BlockExtraResource;
import com.jerry.mekextras.common.block.ExtraWorldGenerator;
import com.jerry.mekextras.common.item.ItemBlockExtraBin;
import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.item.ItemBlockExtraTransmitter;
import com.jerry.mekextras.common.tier.BTier;
import com.jerry.mekextras.common.tier.CTTier;
import com.jerry.mekextras.common.tier.ECTier;
import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import com.jerry.mekextras.common.tier.FTTier;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import com.jerry.mekextras.common.tier.ExtraTransporterTier;
import com.jerry.mekextras.common.tier.ICTier;
import com.jerry.mekextras.common.tier.IPTier;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateTransmitter.TransmitterType;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraMechanicalPipe;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraPressurizedTube;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraThermodynamicConductor;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalInfuser;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLaserFocusMatrix;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorPort;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalOxidizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalWasher;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraIsotopicCentrifuge;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraCrystallizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraDissolutionChamber;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraNutritionalLiquifier;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraOrganicFarm;
import com.jerry.mekextras.common.tile.moremachine.ExtraMoreMachineTiles;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraLogisticalTransporter;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraUniversalCable;
import com.jerry.mekextras.common.tier.RWBTier;
import com.jerry.mekextras.common.util.MekanismMachineConfig;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;
import java.util.Map;

public class ExtraBlocks {

    //Ores and storage blocks
    public static final Block NAQUADAH_ORE = new BlockExtraOre(null, 3.0F, 5.0F, 2);
    public static final Block END_NAQUADAH_ORE = new BlockExtraOre(null, 4.0F, 6.0F, 2);
    public static final Block NAQUADAH_BLOCK = new BlockExtraResource(5.0F, 10.0F);

    //Storage machines
    public static final Block FINAL_BIN = new BlockExtraBin(BTier.FINAL);
    public static final Block ARCANE_BIN = new BlockExtraBin(BTier.ARCANE);
    public static final Block ABSOLUTE_BIN = new BlockExtraBin(BTier.ABSOLUTE);
    public static final Block SUPREME_BIN = new BlockExtraBin(BTier.SUPREME);
    public static final Block COSMIC_BIN = new BlockExtraBin(BTier.COSMIC);
    public static final Block INFINITE_BIN = new BlockExtraBin(BTier.INFINITE);

    public static final Block FINAL_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.FINAL);
    public static final Block ARCANE_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.ARCANE);
    public static final Block ABSOLUTE_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.ABSOLUTE);
    public static final Block SUPREME_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.SUPREME);
    public static final Block COSMIC_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.COSMIC);
    public static final Block INFINITE_ENERGY_CUBE = new BlockExtraEnergyCube(ECTier.INFINITE);

    public static final Block FINAL_FLUID_TANK = new BlockExtraFluidTank(FTTier.FINAL);
    public static final Block ARCANE_FLUID_TANK = new BlockExtraFluidTank(FTTier.ARCANE);
    public static final Block ABSOLUTE_FLUID_TANK = new BlockExtraFluidTank(FTTier.ABSOLUTE);
    public static final Block SUPREME_FLUID_TANK = new BlockExtraFluidTank(FTTier.SUPREME);
    public static final Block COSMIC_FLUID_TANK = new BlockExtraFluidTank(FTTier.COSMIC);
    public static final Block INFINITE_FLUID_TANK = new BlockExtraFluidTank(FTTier.INFINITE);

    public static final Block FINAL_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.FINAL);
    public static final Block ARCANE_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.ARCANE);
    public static final Block ABSOLUTE_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.ABSOLUTE);
    public static final Block SUPREME_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.SUPREME);
    public static final Block COSMIC_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.COSMIC);
    public static final Block INFINITE_CHEMICAL_TANK = new BlockExtraChemicalTank(CTTier.INFINITE);

    public static final Block FINAL_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.FINAL);
    public static final Block ARCANE_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.ARCANE);
    public static final Block ABSOLUTE_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.ABSOLUTE);
    public static final Block SUPREME_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.SUPREME);
    public static final Block COSMIC_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.COSMIC);
    public static final Block INFINITE_RADIOACTIVE_WASTE_BARREL = new BlockExtraWasteBarrel(RWBTier.INFINITE);
    public static final Block REINFORCED_INDUCTION_CASING = new BlockExtraInductionCasing(false);
    public static final Block REINFORCED_INDUCTION_PORT = new BlockExtraInductionCasing(true);

    public static final Block FINAL_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.FINAL);
    public static final Block ARCANE_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.ARCANE);
    public static final Block ABSOLUTE_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.ABSOLUTE);
    public static final Block SUPREME_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.SUPREME);
    public static final Block COSMIC_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.COSMIC);
    public static final Block INFINITE_INDUCTION_CELL = new BlockExtraInductionCell(ICTier.INFINITE);

    public static final Block FINAL_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.FINAL);
    public static final Block ARCANE_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.ARCANE);
    public static final Block ABSOLUTE_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.ABSOLUTE);
    public static final Block SUPREME_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.SUPREME);
    public static final Block COSMIC_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.COSMIC);
    public static final Block INFINITE_INDUCTION_PROVIDER = new BlockExtraInductionProvider(IPTier.INFINITE);
    /**
     * The extra factories: one block per (tier, recipe type), mirroring Mekanism's own factory blocks.
     */
    public static final Map<RecipeType, Block[]> FACTORIES = new EnumMap<>(RecipeType.class);

    static {
        for (RecipeType type : RecipeType.values()) {
            Block[] tiers = new Block[ExtraFactoryTier.values().length];
            for (ExtraFactoryTier tier : ExtraFactoryTier.values()) {
                Block block = new BlockExtraFactory(tier, type);
                tiers[tier.ordinal()] = block;
                init(block, tier.getName() + "_" + type.name().toLowerCase(java.util.Locale.ROOT) + "_factory");
            }
            FACTORIES.put(type, tiers);
        }
    }
    public static final Block NAQUADAH_REACTOR_CASING = new BlockExtraReactor(BlockExtraReactor.Kind.CASING);
    public static final Block NAQUADAH_REACTOR_PORT = new BlockExtraReactor(BlockExtraReactor.Kind.PORT);
    public static final Block NAQUADAH_REACTOR_CONTROLLER = new BlockExtraReactor(BlockExtraReactor.Kind.CONTROLLER);
    public static final Block NAQUADAH_REACTOR_LOGIC_ADAPTER = new BlockExtraReactor(BlockExtraReactor.Kind.LOGIC_ADAPTER);
    public static final Block LEAD_COATED_LASER_FOCUS_MATRIX = new BlockExtraReactor(BlockExtraReactor.Kind.LASER_FOCUS_MATRIX);

    /**
     * The advanced electric pump, the single machine of this mod that is not a factory or a storage block.
     */
    public static final Block ADVANCED_ELECTRIC_PUMP = new BlockExtraAdvancedElectricPump();

    /**
     * The extra transmitters: one block per (kind, tier) pair, five kinds in total.
     */
    public static final Map<TransmitterType, Block[]> TRANSMITTERS = new EnumMap<>(TransmitterType.class);
    private static final TransmitterType[] EXTRA_TRANSMITTER_TYPES = {
          TransmitterType.UNIVERSAL_CABLE, TransmitterType.MECHANICAL_PIPE, TransmitterType.PRESSURIZED_TUBE,
          TransmitterType.THERMODYNAMIC_CONDUCTOR, TransmitterType.LOGISTICAL_TRANSPORTER
    };

    static {
        for (TransmitterType type : EXTRA_TRANSMITTER_TYPES) {
            Block[] tiers = new Block[AdvancedTier.values().length];
            for (AdvancedTier tier : AdvancedTier.values()) {
                Block block = new BlockExtraTransmitter(type, tier);
                tiers[tier.ordinal()] = block;
                init(block, tier.getLowerName() + "_" + type.getName());
            }
            TRANSMITTERS.put(type, tiers);
        }
    }

    /**
     * The MoreMachine based machines: one block per (kind, tier) pair, eleven kinds in total.
     */
    public static final Map<String, Block[]> MORE_MACHINES = new java.util.LinkedHashMap<>();
    public static final Block[] CRYSTALLIZERS = registerMoreMachine("crystallizer", ExtraGuiIds.CRYSTALLIZER, ExtraMoreMachineTiles::createCrystallizer);
    public static final Block[] DISSOLUTION_CHAMBERS = registerMoreMachine("dissolution_chamber", ExtraGuiIds.DISSOLUTION_CHAMBER, ExtraMoreMachineTiles::createDissolutionChamber);
    public static final Block[] CHEMICAL_OXIDIZERS = registerMoreMachine("chemical_oxidizer", ExtraGuiIds.CHEMICAL_OXIDIZER, ExtraMoreMachineTiles::createChemicalOxidizer);
    public static final Block[] NUTRITIONAL_LIQUIFIERS = registerMoreMachine("nutritional_liquifier", ExtraGuiIds.NUTRITIONAL_LIQUIFIER, ExtraMoreMachineTiles::createNutritionalLiquifier);
    public static final Block[] ORGANIC_FARMS = registerMoreMachine("organic_farm", ExtraGuiIds.ORGANIC_FARM, ExtraMoreMachineTiles::createOrganicFarm);
    public static final Block[] CHEMICAL_WASHERS = registerMoreMachine("chemical_washer", ExtraGuiIds.CHEMICAL_WASHER, ExtraMoreMachineTiles::createChemicalWasher);
    public static final Block[] CHEMICAL_INFUSERS = registerMoreMachine("chemical_infuser", ExtraGuiIds.CHEMICAL_INFUSER, ExtraMoreMachineTiles::createChemicalInfuser);
    public static final Block[] ISOTOPIC_CENTRIFUGES = registerMoreMachine("isotopic_centrifuge", ExtraGuiIds.ISOTOPIC_CENTRIFUGE, ExtraMoreMachineTiles::createIsotopicCentrifuge);

    private static Block[] registerMoreMachine(String name, int guiId, java.util.function.Function<ExtraMachineTier, net.minecraft.tileentity.TileEntity> factory) {
        Block[] tiers = new Block[ExtraMachineTier.values().length];
        for (ExtraMachineTier tier : ExtraMachineTier.values()) {
            Block block = new BlockExtraMoreMachine(tier, factory, guiId);
            tiers[tier.ordinal()] = block;
            init(block, tier.getAdvanceTier().getLowerName() + "_" + name);
        }
        MORE_MACHINES.put(name, tiers);
        return tiers;
    }

    private ExtraBlocks() {
    }

    private static Block init(Block block, String name) {
        block.setTranslationKey(MekanismExtras.MODID + "." + name);
        block.setRegistryName(new ResourceLocation(MekanismExtras.MODID, name));
        block.setCreativeTab(MekanismExtras.tabExtra);
        return block;
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(init(NAQUADAH_ORE, "naquadah_ore"));
        registry.register(init(END_NAQUADAH_ORE, "end_naquadah_ore"));
        registry.register(init(NAQUADAH_BLOCK, "block_naquadah"));

        registry.register(init(FINAL_BIN, "final_bin"));
        registry.register(init(ARCANE_BIN, "arcane_bin"));
        registry.register(init(ABSOLUTE_BIN, "absolute_bin"));
        registry.register(init(SUPREME_BIN, "supreme_bin"));
        registry.register(init(COSMIC_BIN, "cosmic_bin"));
        registry.register(init(INFINITE_BIN, "infinite_bin"));

        registry.register(init(FINAL_ENERGY_CUBE, "final_energy_cube"));
        registry.register(init(ARCANE_ENERGY_CUBE, "arcane_energy_cube"));
        registry.register(init(ABSOLUTE_ENERGY_CUBE, "absolute_energy_cube"));
        registry.register(init(SUPREME_ENERGY_CUBE, "supreme_energy_cube"));
        registry.register(init(COSMIC_ENERGY_CUBE, "cosmic_energy_cube"));
        registry.register(init(INFINITE_ENERGY_CUBE, "infinite_energy_cube"));

        registry.register(init(FINAL_FLUID_TANK, "final_fluid_tank"));
        registry.register(init(ARCANE_FLUID_TANK, "arcane_fluid_tank"));
        registry.register(init(ABSOLUTE_FLUID_TANK, "absolute_fluid_tank"));
        registry.register(init(SUPREME_FLUID_TANK, "supreme_fluid_tank"));
        registry.register(init(COSMIC_FLUID_TANK, "cosmic_fluid_tank"));
        registry.register(init(INFINITE_FLUID_TANK, "infinite_fluid_tank"));

        registry.register(init(FINAL_CHEMICAL_TANK, "final_chemical_tank"));
        registry.register(init(ARCANE_CHEMICAL_TANK, "arcane_chemical_tank"));
        registry.register(init(ABSOLUTE_CHEMICAL_TANK, "absolute_chemical_tank"));
        registry.register(init(SUPREME_CHEMICAL_TANK, "supreme_chemical_tank"));
        registry.register(init(COSMIC_CHEMICAL_TANK, "cosmic_chemical_tank"));
        registry.register(init(INFINITE_CHEMICAL_TANK, "infinite_chemical_tank"));

        registry.register(init(FINAL_RADIOACTIVE_WASTE_BARREL, "final_radioactive_waste_barrel"));
        registry.register(init(ARCANE_RADIOACTIVE_WASTE_BARREL, "arcane_radioactive_waste_barrel"));
        registry.register(init(ABSOLUTE_RADIOACTIVE_WASTE_BARREL, "absolute_radioactive_waste_barrel"));
        registry.register(init(SUPREME_RADIOACTIVE_WASTE_BARREL, "supreme_radioactive_waste_barrel"));
        registry.register(init(COSMIC_RADIOACTIVE_WASTE_BARREL, "cosmic_radioactive_waste_barrel"));
        registry.register(init(INFINITE_RADIOACTIVE_WASTE_BARREL, "infinite_radioactive_waste_barrel"));

        registry.register(init(NAQUADAH_REACTOR_CASING, "naquadah_reactor_casing"));
        registry.register(init(NAQUADAH_REACTOR_PORT, "naquadah_reactor_port"));
        registry.register(init(NAQUADAH_REACTOR_CONTROLLER, "naquadah_reactor_controller"));
        registry.register(init(NAQUADAH_REACTOR_LOGIC_ADAPTER, "naquadah_reactor_logic_adapter"));
        registry.register(init(LEAD_COATED_LASER_FOCUS_MATRIX, "lead_coated_laser_focus_matrix"));
        if (MekanismMachineConfig.isEnabled(mekanism.common.block.states.BlockStateMachine.MachineType.ELECTRIC_PUMP)) {
            registry.register(init(ADVANCED_ELECTRIC_PUMP, "advanced_electric_pump"));
        }
        registry.register(init(REINFORCED_INDUCTION_CASING, "reinforced_induction_casing"));
        registry.register(init(REINFORCED_INDUCTION_PORT, "reinforced_induction_port"));
        registry.register(init(FINAL_INDUCTION_CELL, "final_induction_cell"));
        registry.register(init(ARCANE_INDUCTION_CELL, "arcane_induction_cell"));
        registry.register(init(ABSOLUTE_INDUCTION_CELL, "absolute_induction_cell"));
        registry.register(init(SUPREME_INDUCTION_CELL, "supreme_induction_cell"));
        registry.register(init(COSMIC_INDUCTION_CELL, "cosmic_induction_cell"));
        registry.register(init(INFINITE_INDUCTION_CELL, "infinite_induction_cell"));
        registry.register(init(FINAL_INDUCTION_PROVIDER, "final_induction_provider"));
        registry.register(init(ARCANE_INDUCTION_PROVIDER, "arcane_induction_provider"));
        registry.register(init(ABSOLUTE_INDUCTION_PROVIDER, "absolute_induction_provider"));
        registry.register(init(SUPREME_INDUCTION_PROVIDER, "supreme_induction_provider"));
        registry.register(init(COSMIC_INDUCTION_PROVIDER, "cosmic_induction_provider"));
        registry.register(init(INFINITE_INDUCTION_PROVIDER, "infinite_induction_provider"));
        for (Block[] tiers : FACTORIES.values()) {
            if (MekanismMachineConfig.isFactoryEnabled(recipeTypeFor(tiers))) {
                for (Block block : tiers) {
                    registry.register(block);
                }
            }
        }
        for (Block[] tiers : TRANSMITTERS.values()) {
            for (Block block : tiers) {
                registry.register(block);
            }
        }
        for (Block[] tiers : MORE_MACHINES.values()) {
            if (MekanismMachineConfig.isMoreMachineEnabled(machineNameFor(tiers))) {
                for (Block block : tiers) {
                    registry.register(block);
                }
            }
        }
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registerItemBlock(registry, NAQUADAH_ORE);
        registerItemBlock(registry, END_NAQUADAH_ORE);
        registerItemBlock(registry, NAQUADAH_BLOCK);

        registerMachineItemBlock(registry, FINAL_BIN);
        registerMachineItemBlock(registry, ARCANE_BIN);
        registerMachineItemBlock(registry, ABSOLUTE_BIN);
        registerMachineItemBlock(registry, SUPREME_BIN);
        registerMachineItemBlock(registry, COSMIC_BIN);
        registerMachineItemBlock(registry, INFINITE_BIN);

        registerMachineItemBlock(registry, FINAL_ENERGY_CUBE);
        registerMachineItemBlock(registry, ARCANE_ENERGY_CUBE);
        registerMachineItemBlock(registry, ABSOLUTE_ENERGY_CUBE);
        registerMachineItemBlock(registry, SUPREME_ENERGY_CUBE);
        registerMachineItemBlock(registry, COSMIC_ENERGY_CUBE);
        registerMachineItemBlock(registry, INFINITE_ENERGY_CUBE);

        registerMachineItemBlock(registry, FINAL_FLUID_TANK);
        registerMachineItemBlock(registry, ARCANE_FLUID_TANK);
        registerMachineItemBlock(registry, ABSOLUTE_FLUID_TANK);
        registerMachineItemBlock(registry, SUPREME_FLUID_TANK);
        registerMachineItemBlock(registry, COSMIC_FLUID_TANK);
        registerMachineItemBlock(registry, INFINITE_FLUID_TANK);

        registerMachineItemBlock(registry, FINAL_CHEMICAL_TANK);
        registerMachineItemBlock(registry, ARCANE_CHEMICAL_TANK);
        registerMachineItemBlock(registry, ABSOLUTE_CHEMICAL_TANK);
        registerMachineItemBlock(registry, SUPREME_CHEMICAL_TANK);
        registerMachineItemBlock(registry, COSMIC_CHEMICAL_TANK);
        registerMachineItemBlock(registry, INFINITE_CHEMICAL_TANK);

        registerMachineItemBlock(registry, FINAL_RADIOACTIVE_WASTE_BARREL);
        registerMachineItemBlock(registry, ARCANE_RADIOACTIVE_WASTE_BARREL);
        registerMachineItemBlock(registry, ABSOLUTE_RADIOACTIVE_WASTE_BARREL);
        registerMachineItemBlock(registry, SUPREME_RADIOACTIVE_WASTE_BARREL);
        registerMachineItemBlock(registry, COSMIC_RADIOACTIVE_WASTE_BARREL);
        registerMachineItemBlock(registry, INFINITE_RADIOACTIVE_WASTE_BARREL);

        registerMachineItemBlock(registry, NAQUADAH_REACTOR_CASING);
        registerMachineItemBlock(registry, NAQUADAH_REACTOR_PORT);
        registerMachineItemBlock(registry, NAQUADAH_REACTOR_CONTROLLER);
        registerMachineItemBlock(registry, NAQUADAH_REACTOR_LOGIC_ADAPTER);
        registerMachineItemBlock(registry, LEAD_COATED_LASER_FOCUS_MATRIX);
        if (MekanismMachineConfig.isEnabled(mekanism.common.block.states.BlockStateMachine.MachineType.ELECTRIC_PUMP)) {
            registerMachineItemBlock(registry, ADVANCED_ELECTRIC_PUMP);
        }
        registerMachineItemBlock(registry, REINFORCED_INDUCTION_CASING);
        registerMachineItemBlock(registry, REINFORCED_INDUCTION_PORT);
        registerMachineItemBlock(registry, FINAL_INDUCTION_CELL);
        registerMachineItemBlock(registry, ARCANE_INDUCTION_CELL);
        registerMachineItemBlock(registry, ABSOLUTE_INDUCTION_CELL);
        registerMachineItemBlock(registry, SUPREME_INDUCTION_CELL);
        registerMachineItemBlock(registry, COSMIC_INDUCTION_CELL);
        registerMachineItemBlock(registry, INFINITE_INDUCTION_CELL);
        registerMachineItemBlock(registry, FINAL_INDUCTION_PROVIDER);
        registerMachineItemBlock(registry, ARCANE_INDUCTION_PROVIDER);
        registerMachineItemBlock(registry, ABSOLUTE_INDUCTION_PROVIDER);
        registerMachineItemBlock(registry, SUPREME_INDUCTION_PROVIDER);
        registerMachineItemBlock(registry, COSMIC_INDUCTION_PROVIDER);
        registerMachineItemBlock(registry, INFINITE_INDUCTION_PROVIDER);
        for (Block[] tiers : FACTORIES.values()) {
            if (MekanismMachineConfig.isFactoryEnabled(recipeTypeFor(tiers))) {
                for (Block block : tiers) {
                    registerMachineItemBlock(registry, block);
                }
            }
        }
        for (Block[] tiers : TRANSMITTERS.values()) {
            for (Block block : tiers) {
                registerTransmitterItemBlock(registry, block);
            }
        }
        for (Block[] tiers : MORE_MACHINES.values()) {
            if (MekanismMachineConfig.isMoreMachineEnabled(machineNameFor(tiers))) {
                for (Block block : tiers) {
                    registerMachineItemBlock(registry, block);
                }
            }
        }
    }

    private static void registerTransmitterItemBlock(IForgeRegistry<Item> registry, Block block) {
        Item item = new ItemBlockExtraTransmitter(block);
        item.setRegistryName(block.getRegistryName());
        item.setTranslationKey(block.getTranslationKey().replace("tile.", "").replace(".name", ""));
        item.setCreativeTab(MekanismExtras.tabExtra);
        registry.register(item);
    }

    private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
        Item item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        item.setTranslationKey(block.getTranslationKey().replace("tile.", "").replace(".name", ""));
        item.setCreativeTab(MekanismExtras.tabExtra);
        registry.register(item);
    }

    private static void registerMachineItemBlock(IForgeRegistry<Item> registry, Block block) {
        Item item = block instanceof BlockExtraBin ? new ItemBlockExtraBin(block) : new ItemBlockExtraMachine(block);
        item.setRegistryName(block.getRegistryName());
        item.setTranslationKey(block.getTranslationKey().replace("tile.", "").replace(".name", ""));
        registry.register(item);
    }

    private static RecipeType recipeTypeFor(Block[] tiers) {
        for (Map.Entry<RecipeType, Block[]> entry : FACTORIES.entrySet()) {
            if (entry.getValue() == tiers) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static String machineNameFor(Block[] tiers) {
        for (Map.Entry<String, Block[]> entry : MORE_MACHINES.entrySet()) {
            if (entry.getValue() == tiers) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static void registerWorldGenerators() {
        GameRegistry.registerWorldGenerator(new ExtraWorldGenerator(), 0);
    }

    /**
     * @return the bin block of the given tier, or null if the tier does not have a bin
     */
    public static Block getBinBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_BIN;
            case ARCANE:
                return ARCANE_BIN;
            case SUPREME:
                return SUPREME_BIN;
            case ABSOLUTE:
                return ABSOLUTE_BIN;
            case COSMIC:
                return COSMIC_BIN;
            case INFINITE:
                return INFINITE_BIN;
            default:
                return null;
        }
    }

    public static Block getFactoryBlock(RecipeType recipeType, ExtraFactoryTier tier) {
        if (!MekanismMachineConfig.isFactoryEnabled(recipeType) || tier == null) {
            return null;
        }
        Block[] tiers = FACTORIES.get(recipeType);
        return tiers == null ? null : tiers[tier.ordinal()];
    }
    public static net.minecraft.tileentity.TileEntity createTransmitterTile(TransmitterType type, AdvancedTier tier) {
        int index = tier.ordinal();
        switch (type) {
            case UNIVERSAL_CABLE:
                return new TileEntityExtraUniversalCable(com.jerry.mekextras.common.tier.ExtraCableTier.byIndex(index));
            case MECHANICAL_PIPE:
                return new TileEntityExtraMechanicalPipe(com.jerry.mekextras.common.tier.ExtraPipeTier.byIndex(index));
            case PRESSURIZED_TUBE:
                return new TileEntityExtraPressurizedTube(com.jerry.mekextras.common.tier.ExtraTubeTier.byIndex(index));
            case THERMODYNAMIC_CONDUCTOR:
                return new TileEntityExtraThermodynamicConductor(com.jerry.mekextras.common.tier.ExtraConductorTier.byIndex(index));
            case LOGISTICAL_TRANSPORTER:
                return new TileEntityExtraLogisticalTransporter(ExtraTransporterTier.byIndex(index));
            default:
                return null;
        }
    }
    public static Block getInductionCellBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_INDUCTION_CELL;
            case ARCANE:
                return ARCANE_INDUCTION_CELL;
            case SUPREME:
                return SUPREME_INDUCTION_CELL;
            case ABSOLUTE:
                return ABSOLUTE_INDUCTION_CELL;
            case COSMIC:
                return COSMIC_INDUCTION_CELL;
            case INFINITE:
                return INFINITE_INDUCTION_CELL;
            default:
                return null;
        }
    }

    public static Block getInductionProviderBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_INDUCTION_PROVIDER;
            case ARCANE:
                return ARCANE_INDUCTION_PROVIDER;
            case SUPREME:
                return SUPREME_INDUCTION_PROVIDER;
            case ABSOLUTE:
                return ABSOLUTE_INDUCTION_PROVIDER;
            case COSMIC:
                return COSMIC_INDUCTION_PROVIDER;
            case INFINITE:
                return INFINITE_INDUCTION_PROVIDER;
            default:
                return null;
        }
    }
    public static Block getEnergyCubeBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_ENERGY_CUBE;
            case ARCANE:
                return ARCANE_ENERGY_CUBE;
            case SUPREME:
                return SUPREME_ENERGY_CUBE;
            case ABSOLUTE:
                return ABSOLUTE_ENERGY_CUBE;
            case COSMIC:
                return COSMIC_ENERGY_CUBE;
            case INFINITE:
                return INFINITE_ENERGY_CUBE;
            default:
                return null;
        }
    }

    public static Block getFluidTankBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_FLUID_TANK;
            case ARCANE:
                return ARCANE_FLUID_TANK;
            case SUPREME:
                return SUPREME_FLUID_TANK;
            case ABSOLUTE:
                return ABSOLUTE_FLUID_TANK;
            case COSMIC:
                return COSMIC_FLUID_TANK;
            case INFINITE:
                return INFINITE_FLUID_TANK;
            default:
                return null;
        }
    }

    public static Block getChemicalTankBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_CHEMICAL_TANK;
            case ARCANE:
                return ARCANE_CHEMICAL_TANK;
            case SUPREME:
                return SUPREME_CHEMICAL_TANK;
            case ABSOLUTE:
                return ABSOLUTE_CHEMICAL_TANK;
            case COSMIC:
                return COSMIC_CHEMICAL_TANK;
            case INFINITE:
                return INFINITE_CHEMICAL_TANK;
            default:
                return null;
        }
    }

    public static Block getWasteBarrelBlock(AdvancedTier tier) {
        switch (tier) {
            case FINAL:
                return FINAL_RADIOACTIVE_WASTE_BARREL;
            case ARCANE:
                return ARCANE_RADIOACTIVE_WASTE_BARREL;
            case SUPREME:
                return SUPREME_RADIOACTIVE_WASTE_BARREL;
            case ABSOLUTE:
                return ABSOLUTE_RADIOACTIVE_WASTE_BARREL;
            case COSMIC:
                return COSMIC_RADIOACTIVE_WASTE_BARREL;
            case INFINITE:
                return INFINITE_RADIOACTIVE_WASTE_BARREL;
            default:
                return null;
        }
    }
    public static Block getMachineBlock(String prefix, AdvancedTier tier) {
        if ("bin".equals(prefix)) {
            return getBinBlock(tier);
        }
        return null;
    }

    public static Block getTransmitterBlock(TransmitterType type, AdvancedTier tier) {
        Block[] tiers = TRANSMITTERS.get(type);
        return tiers == null ? null : tiers[tier.ordinal()];
    }

    public static Block getMoreMachineBlock(String name, AdvancedTier tier) {
        if (!MekanismMachineConfig.isMoreMachineEnabled(name) || tier == null) {
            return null;
        }
        Block[] tiers = MORE_MACHINES.get(name);
        return tiers == null ? null : tiers[tier.ordinal()];
    }
}


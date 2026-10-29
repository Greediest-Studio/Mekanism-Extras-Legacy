package com.jerry.mekextras.common.util;

import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.config.MekanismConfig;

/**
 * Bridges Mekanism's machine enable/disable configuration to the corresponding
 * upgraded blocks supplied by Mekanism Extras.
 */
public final class MekanismMachineConfig {

    private MekanismMachineConfig() {
    }

    public static boolean isEnabled(MachineType machine) {
        return machine != null && MekanismConfig.current().general.machinesManager.isEnabled(machine);
    }

    public static boolean isFactoryEnabled(RecipeType recipeType) {
        // All factory tiers share Mekanism's "FactoryEnabled" config entry
        // (their block name is the same), so checking the basic entry covers
        // the complete factory family.
        if (recipeType == null) {
            return true;
        }
        if (!isEnabled(MachineType.BASIC_FACTORY)) {
            return false;
        }
        // Some Mekanism-CE builds expose these legacy recipe types while
        // others keep them only as standalone machines. Use their names so
        // this bridge remains binary-compatible with both variants.
        String recipeName = recipeType.name();
        if ("FARM".equals(recipeName)) {
            return isEnabled(MachineType.ORGANIC_FARM);
        }
        if ("Crystallizer".equals(recipeName)) {
            return isEnabled(MachineType.CHEMICAL_CRYSTALLIZER);
        }
        if ("Dissolution".equals(recipeName)) {
            return isEnabled(MachineType.CHEMICAL_DISSOLUTION_CHAMBER);
        }
        if ("OXIDIZER".equals(recipeName)) {
            return isEnabled(MachineType.CHEMICAL_OXIDIZER);
        }
        if ("WASHER".equals(recipeName)) {
            return isEnabled(MachineType.CHEMICAL_WASHER);
        }
        switch (recipeType) {
            case SMELTING:
                return isEnabled(MachineType.ENERGIZED_SMELTER);
            case ENRICHING:
                return isEnabled(MachineType.ENRICHMENT_CHAMBER);
            case CRUSHING:
                return isEnabled(MachineType.CRUSHER);
            case COMPRESSING:
                return isEnabled(MachineType.OSMIUM_COMPRESSOR);
            case COMBINING:
                return isEnabled(MachineType.COMBINER);
            case PURIFYING:
                return isEnabled(MachineType.PURIFICATION_CHAMBER);
            case INJECTING:
                return isEnabled(MachineType.CHEMICAL_INJECTION_CHAMBER);
            case INFUSING:
                return isEnabled(MachineType.METALLURGIC_INFUSER);
            case SAWING:
                return isEnabled(MachineType.PRECISION_SAWMILL);
            case STAMPING:
                return isEnabled(MachineType.STAMPING);
            case ROLLING:
                return isEnabled(MachineType.ROLLING);
            case BRUSHED:
                return isEnabled(MachineType.BRUSHED);
            case TURNING:
                return isEnabled(MachineType.TURNING);
            case AllOY:
                return isEnabled(MachineType.ALLOY);
            case EXTRACTOR:
                return isEnabled(MachineType.CELL_EXTRACTOR);
            case SEPARATOR:
                return isEnabled(MachineType.CELL_SEPARATOR);
            case RECYCLER:
                return isEnabled(MachineType.RECYCLER);
            case PRC:
                return isEnabled(MachineType.PRESSURIZED_REACTION_CHAMBER);
            case NUCLEOSYNTHESIZER:
                return isEnabled(MachineType.ANTIPROTONIC_NUCLEOSYNTHESIZER);
            default:
                return true;
        }
    }

    public static boolean anyFactoryEnabled() {
        for (RecipeType recipeType : RecipeType.values()) {
            if (isFactoryEnabled(recipeType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMoreMachineEnabled(String name) {
        if (name == null) {
            return false;
        }
        switch (name) {
            case "crystallizer":
                return isEnabled(MachineType.CHEMICAL_CRYSTALLIZER);
            case "dissolution_chamber":
                return isEnabled(MachineType.CHEMICAL_DISSOLUTION_CHAMBER);
            case "chemical_oxidizer":
                return isEnabled(MachineType.CHEMICAL_OXIDIZER);
            case "nutritional_liquifier":
                return isEnabled(MachineType.NUTRITIONAL_LIQUIFIER);
            case "organic_farm":
                return isEnabled(MachineType.ORGANIC_FARM);
            case "chemical_washer":
                return isEnabled(MachineType.CHEMICAL_WASHER);
            case "chemical_infuser":
                return isEnabled(MachineType.CHEMICAL_INFUSER);
            case "isotopic_centrifuge":
                return isEnabled(MachineType.ISOTOPIC_CENTRIFUGE);
            default:
                return true;
        }
    }
}

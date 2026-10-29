package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.OreGas;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import net.minecraft.util.ResourceLocation;

/**
 * Chemicals added by Mekanism Extras Legacy: gases, infuse types and ore slurries.
 * <p>
 * Fluids and their buckets are registered separately by {@link ExtraFluidBlocks}.
 */
public class ExtraFluids {

    private static final String PREFIX = MekanismExtras.MODID + ".";

    //Gases
    public static final Gas MOLTEN_THERMONUCLEAR = new Gas(PREFIX + "molten_thermonuclear", 0x810C0C);
    public static final Gas NAQUADAH_HEXAFLUORIDE = new Gas(PREFIX + "naquadah_hexafluoride", 0xC8C8C8);
    public static final Gas FLUORINATED_NAQUADAH_URANIUM_FUEL = new Gas(PREFIX + "fluorinated_naquadah_uranium_fuel", 0xB1B30C);
    public static final Gas NAQUADAH_URANIUM_FUEL = new Gas(PREFIX + "naquadah_uranium_fuel", 0x837906);
    public static final Gas RICH_NAQUADAH_FUEL = new Gas(PREFIX + "rich_naquadah_fuel", 0xEFEFEF);
    public static final Gas RICH_URANIUM_FUEL = new Gas(PREFIX + "rich_uranium_fuel", 0x6C864A);
    /**
     * Produced by the naquadah reactor when its casing is fed with water.
     */
    public static final Gas POLONIUM_CONTAINING_STEAM = new Gas(PREFIX + "polonium_containing_steam", 0x1B9E7B);
    /**
     * The radioactive byproduct of the naquadah fuel cycle.
     */
    public static final Gas POLONIUM208 = new Gas(PREFIX + "polonium208", 0x1B9E7B);

    //Infuse types
    public static final InfuseType LEAD = new InfuseType("MEKANISM_EXTRAS_LEAD", new ResourceLocation(MekanismExtras.MODID, "blocks/infuse/lead"))
            .setTranslationKey(MekanismExtras.MODID + ".lead");

    //Slurries
    public static final OreGas CLEAN_NAQUADAH = new OreGas(PREFIX + "clean_naquadah", "oregas.mekanism_extras_naquadah", 0x051602);
    public static final OreGas DIRTY_NAQUADAH = new OreGas(PREFIX + "dirty_naquadah", "oregas.mekanism_extras_naquadah", 0x051602, CLEAN_NAQUADAH);

    public static boolean isReactorInputFuel(Gas gas) {
        return gas == RICH_NAQUADAH_FUEL || gas == NAQUADAH_URANIUM_FUEL || gas == RICH_URANIUM_FUEL;
    }

    public static boolean isHohlraumFuel(Gas gas) {
        return gas == NAQUADAH_URANIUM_FUEL;
    }
    private ExtraFluids() {
    }

    public static void register() {
        GasRegistry.register(MOLTEN_THERMONUCLEAR);
        GasRegistry.register(NAQUADAH_HEXAFLUORIDE);
        GasRegistry.register(FLUORINATED_NAQUADAH_URANIUM_FUEL);
        GasRegistry.register(NAQUADAH_URANIUM_FUEL);
        GasRegistry.register(RICH_NAQUADAH_FUEL);
        GasRegistry.register(RICH_URANIUM_FUEL);
        GasRegistry.register(POLONIUM_CONTAINING_STEAM);
        GasRegistry.register(POLONIUM208).setRadiation(0.05);

        InfuseRegistry.registerInfuseType(LEAD);

        GasRegistry.register(CLEAN_NAQUADAH);
        GasRegistry.register(DIRTY_NAQUADAH);

        ExtraFluidBlocks.register();
    }
}

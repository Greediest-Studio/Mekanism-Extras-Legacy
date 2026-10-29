package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.common.ExtraChemicalConstants;
import mekanism.api.chemical.Chemical;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registration.impl.SlurryRegistryObject;
import net.neoforged.bus.api.IEventBus;

public class ExtraChemicals {

    private ExtraChemicals() {

    }

    public static final ChemicalDeferredRegister EXTRA_CHEMICALS = new ChemicalDeferredRegister(MekanismExtras.MOD_ID);

    // Gas
    public static final DeferredChemical<Chemical> MOLTEN_THERMONUCLEAR = EXTRA_CHEMICALS.register(ExtraChemicalConstants.MOLTEN_THERMONUCLEAR);
    public static final DeferredChemical<Chemical> NAQUADAH_HEXAFLUORIDE = EXTRA_CHEMICALS.register(ExtraChemicalConstants.NAQUADAH_HEXAFLUORIDE);
    public static final DeferredChemical<Chemical> FLUORINATED_NAQUADAH_URANIUM_FUEL = EXTRA_CHEMICALS.register(ExtraChemicalConstants.FLUORINATED_NAQUADAH_URANIUM_FUEL);
    public static final DeferredChemical<Chemical> NAQUADAH_URANIUM_FUEL = EXTRA_CHEMICALS.register(ExtraChemicalConstants.NAQUADAH_URANIUM_FUEL);
    public static final DeferredChemical<Chemical> RICH_NAQUADAH_FUEL = EXTRA_CHEMICALS.register(ExtraChemicalConstants.RICH_NAQUADAH_FUEL);
    public static final DeferredChemical<Chemical> RICH_URANIUM_FUEL = EXTRA_CHEMICALS.register(ExtraChemicalConstants.RICH_URANIUM_FUEL);

    // Slurry
    // 如果之后有更多矿物可以使用批量添加
    public static final SlurryRegistryObject<Chemical, Chemical> DIRTY_AND_CLEAN_SLURRIES_NAQUADAH = EXTRA_CHEMICALS.registerSlurry("naquadah", builder -> builder.tint(0x051602));

    public static void register(IEventBus eventBus) {
        EXTRA_CHEMICALS.register(eventBus);
    }
}

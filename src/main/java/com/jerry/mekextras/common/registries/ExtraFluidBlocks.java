package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * The liquefied versions of Mekanism Extras' chemicals.
 * <p>
 * These fluids are registered with Forge's universal bucket and have placeable fluid blocks so the Advanced Electric
 * Pump can extract and process them from the world.
 */
public class ExtraFluidBlocks {

    private static final ResourceLocation LIQUID_TEXTURE = new ResourceLocation("mekanism", "blocks/liquid/liquid");
    private static final List<BlockFluidClassic> FLUID_BLOCKS = new ArrayList<>();

    public static final Fluid NAQUADAH_HEXAFLUORIDE = create("naquadah_hexafluoride", 0xC8C8C8);
    public static final Fluid FLUORINATED_NAQUADAH_URANIUM_FUEL = create("fluorinated_naquadah_uranium_fuel", 0xB1B30C);
    public static final Fluid RICH_NAQUADAH_FUEL = create("rich_naquadah_fuel", 0xEFEFEF);
    public static final Fluid RICH_URANIUM_FUEL = create("rich_uranium_fuel", 0x6C864A);
    public static final Fluid POLONIUM_CONTAINING_SOLUTION = create("polonium_containing_solution", 0x1B9E7B);
    public static final Fluid POLONIUM208 = create("polonium_208", 0x1B9E7B);

    private ExtraFluidBlocks() {
    }

    private static Fluid create(String name, int tint) {
        Fluid fluid = new Fluid(MekanismExtras.MODID + "." + name, LIQUID_TEXTURE, LIQUID_TEXTURE, 0xFF000000 | tint);
        fluid.setUnlocalizedName(MekanismExtras.MODID + "." + name);
        FluidRegistry.registerFluid(fluid);

        BlockFluidClassic block = new BlockFluidClassic(fluid, Material.WATER);
        block.setRegistryName(new ResourceLocation(MekanismExtras.MODID, name));
        block.setTranslationKey(MekanismExtras.MODID + "." + name);
        FLUID_BLOCKS.add(block);

        if (!FluidRegistry.isUniversalBucketEnabled()) {
            FluidRegistry.enableUniversalBucket();
        }
        FluidRegistry.addBucketForFluid(fluid);
        return fluid;
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        for (BlockFluidClassic block : FLUID_BLOCKS) {
            registry.register(block);
        }
    }

    public static void register() {
        ExtraFluids.NAQUADAH_HEXAFLUORIDE.setFluid(NAQUADAH_HEXAFLUORIDE);
        ExtraFluids.FLUORINATED_NAQUADAH_URANIUM_FUEL.setFluid(FLUORINATED_NAQUADAH_URANIUM_FUEL);
        ExtraFluids.RICH_NAQUADAH_FUEL.setFluid(RICH_NAQUADAH_FUEL);
        ExtraFluids.RICH_URANIUM_FUEL.setFluid(RICH_URANIUM_FUEL);
        ExtraFluids.POLONIUM_CONTAINING_STEAM.setFluid(POLONIUM_CONTAINING_SOLUTION);
        ExtraFluids.POLONIUM208.setFluid(POLONIUM208);
    }
}

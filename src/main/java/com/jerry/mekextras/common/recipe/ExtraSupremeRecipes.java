package com.jerry.mekextras.common.recipe;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.common.registries.ExtraItems;
import mekanism.common.MekanismItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

/** Registers the Supreme-tier recipes that need a dynamic netherite-or-osmium ingredient. */
public final class ExtraSupremeRecipes {

    private static final String[] TARGETS = {
          "supreme_alloy_factory",
          "supreme_brushed_factory",
          "supreme_chemical_infuser",
          "supreme_chemical_oxidizer",
          "supreme_chemical_washer",
          "supreme_combining_factory",
          "supreme_compressing_factory",
          "supreme_crushing_factory",
          "supreme_crystallizer",
          "supreme_dissolution_chamber",
          "supreme_enriching_factory",
          "supreme_extractor_factory",
          "supreme_infusing_factory",
          "supreme_injecting_factory",
          "supreme_isotopic_centrifuge",
          "supreme_nucleosynthesizer_factory",
          "supreme_nutritional_liquifier",
          "supreme_organic_farm",
          "supreme_prc_factory",
          "supreme_purifying_factory",
          "supreme_recycler_factory",
          "supreme_rolling_factory",
          "supreme_sawing_factory",
          "supreme_separator_factory",
          "supreme_smelting_factory",
          "supreme_stamping_factory",
          "supreme_turning_factory"
    };

    private ExtraSupremeRecipes() {
    }

    public static void register(IForgeRegistry<IRecipe> registry) {
        NetheriteOrOsmiumIngredient ingot = new NetheriteOrOsmiumIngredient();
        for (String targetName : TARGETS) {
            String previousName = "absolute_" + targetName.substring("supreme_".length());
            Item target = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MekanismExtras.MODID, targetName));
            Item previous = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MekanismExtras.MODID, previousName));
            if (target == null || previous == null) {
                throw new IllegalStateException("Missing Supreme recipe item: " + targetName + " -> " + previousName);
            }
            IRecipe recipe = new ShapedOreRecipe(new ResourceLocation(MekanismExtras.MODID, "supreme_dynamic"),
                  new ItemStack(target), "ACA", "IPI", "ACA",
                  'A', ExtraItems.THERMONUCLEAR_ALLOY,
                  'C', ExtraItems.SUPREME_CONTROL_CIRCUIT,
                  'I', ingot,
                  'P', previous);
            recipe.setRegistryName(new ResourceLocation(MekanismExtras.MODID, targetName));
            registry.register(recipe);
        }
        registerSupremeTierInstaller(registry, ingot);
        registerSupremeEnergyCube(registry, ingot);
    }

    private static void registerSupremeTierInstaller(IForgeRegistry<IRecipe> registry, NetheriteOrOsmiumIngredient ingot) {
        Item target = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MekanismExtras.MODID, "supreme_tier_installer"));
        if (target == null) {
            throw new IllegalStateException("Missing Supreme tier installer item");
        }
        IRecipe recipe = new ShapedOreRecipe(new ResourceLocation(MekanismExtras.MODID, "supreme_dynamic"),
              new ItemStack(target), "ACA", "IPI", "ACA",
              'A', ExtraItems.THERMONUCLEAR_ALLOY,
              'C', ExtraItems.SUPREME_CONTROL_CIRCUIT,
              'I', ingot,
              'P', "plankWood");
        recipe.setRegistryName(new ResourceLocation(MekanismExtras.MODID, "supreme_tier_installer"));
        registry.register(recipe);
    }
    private static void registerSupremeEnergyCube(IForgeRegistry<IRecipe> registry, NetheriteOrOsmiumIngredient ingot) {
        Item target = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MekanismExtras.MODID, "supreme_energy_cube"));
        Item previous = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MekanismExtras.MODID, "absolute_energy_cube"));
        if (target == null || previous == null) {
            throw new IllegalStateException("Missing Supreme energy cube recipe items");
        }
        IRecipe recipe = new ShapedOreRecipe(new ResourceLocation(MekanismExtras.MODID, "supreme_dynamic"),
              new ItemStack(target), "AEA", "IPI", "AEA",
              'A', ExtraItems.THERMONUCLEAR_ALLOY,
              'E', MekanismItems.EnergyTablet,
              'I', ingot,
              'P', previous);
        recipe.setRegistryName(new ResourceLocation(MekanismExtras.MODID, "supreme_energy_cube"));
        registry.register(recipe);
    }
}

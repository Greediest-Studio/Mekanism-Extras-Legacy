package com.jerry.mekextras.client.jei;

import com.jerry.mekextras.common.block.BlockExtraMoreMachine;
import com.jerry.mekextras.common.item.ItemNaquadahHohlraum;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.registries.ExtraItems;
import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import mekanism.api.gas.GasStack;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Registers the machines of this mod as JEI catalysts for Mekanism's own recipe categories.
 * <p>
 * The extra factories produce exactly the same recipes as the Mekanism machines they are based on, so they are added
 * to the matching categories instead of duplicating every recipe page.
 */
@JEIPlugin
public class ExtraJEIPlugin implements IModPlugin {

    private static final Map<MachineType, Recipe<?, ?, ?>> MACHINE_RECIPES = new EnumMap<>(MachineType.class);

    static {
        for (Object value : Recipe.values()) {
            Recipe<?, ?, ?> recipe = (Recipe<?, ?, ?>) value;
            String name = recipe.getRecipeName().toLowerCase(Locale.ROOT);
            for (MachineType type : MachineType.values()) {
                if (type.getBlockName().equalsIgnoreCase(name)) {
                    MACHINE_RECIPES.putIfAbsent(type, recipe);
                    break;
                }
            }
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry registry) {
        registry.registerSubtypeInterpreter(ExtraItems.NAQUADAH_HOHLRAUM, stack -> {
            if (!(stack.getItem() instanceof ItemNaquadahHohlraum item)) {
                return "empty";
            }
            GasStack gas = item.getContainedGas(stack);
            return gas == null ? "empty" : gas.getGas().getName() + ":" + gas.amount;
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new NaquadahReactorRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registerFactories(registry);
        registerMoreMachine(registry, Recipe.CHEMICAL_CRYSTALLIZER, ExtraBlocks.CRYSTALLIZERS);
        registerMoreMachine(registry, Recipe.CHEMICAL_DISSOLUTION_CHAMBER, ExtraBlocks.DISSOLUTION_CHAMBERS);
        registerMoreMachine(registry, Recipe.CHEMICAL_OXIDIZER, ExtraBlocks.CHEMICAL_OXIDIZERS);
        registerMoreMachine(registry, Recipe.NUTRITIONAL_LIQUIFIER, ExtraBlocks.NUTRITIONAL_LIQUIFIERS);
        registerMoreMachine(registry, Recipe.ORGANIC_FARM, ExtraBlocks.ORGANIC_FARMS);
        registerMoreMachine(registry, Recipe.CHEMICAL_WASHER, ExtraBlocks.CHEMICAL_WASHERS);
        registerMoreMachine(registry, Recipe.CHEMICAL_INFUSER, ExtraBlocks.CHEMICAL_INFUSERS);
        registerMoreMachine(registry, Recipe.ISOTOPIC_CENTRIFUGE, ExtraBlocks.ISOTOPIC_CENTRIFUGES);
        registry.addIngredientInfo(new ItemStack(ExtraBlocks.ADVANCED_ELECTRIC_PUMP), VanillaTypes.ITEM,
              "description.mekanism_extras.ionic_membrane_processing");
        registry.addIngredientInfo(new ItemStack(ExtraItems.IONIC_MEMBRANE_UPGRADE), VanillaTypes.ITEM,
              "description.mekanism_extras.ionic_membrane_processing");
        registry.addRecipes(NaquadahReactorRecipeWrapper.createRecipes(), NaquadahReactorRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ExtraBlocks.NAQUADAH_REACTOR_CONTROLLER), NaquadahReactorRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ExtraBlocks.NAQUADAH_REACTOR_PORT), NaquadahReactorRecipeCategory.UID);
    }

    private void registerFactories(IModRegistry registry) {
        for (RecipeType recipeType : RecipeType.values()) {
            Recipe<?, ?, ?> recipe = MACHINE_RECIPES.get(recipeType.getType());
            if (recipe == null) {
                continue;
            }
            if (recipe == Recipe.RECYCLER && !MekanismConfig.current().mekce.EnableRecyclerRecipeInJei.val()) {
                continue;
            }
            String category = recipe.getJEICategory();
            for (ExtraFactoryTier tier : ExtraFactoryTier.values()) {
                Block block = ExtraBlocks.getFactoryBlock(recipeType, tier);
                if (block == null) {
                    continue;
                }
                ItemStack stack = new ItemStack(block);
                registry.addRecipeCatalyst(stack, category);
                if (recipeType == RecipeType.SMELTING) {
                    //Mekanism's smelting factory is also a catalyst for the vanilla furnace recipes.
                    registry.addRecipeCatalyst(stack, VanillaRecipeCategoryUid.SMELTING);
                }
            }
        }
    }

    private void registerMoreMachine(IModRegistry registry, Recipe<?, ?, ?> recipe, Block[] blocks) {
        if (recipe == null || blocks == null) {
            return;
        }
        String category = recipe.getJEICategory();
        for (Block block : blocks) {
            if (block instanceof BlockExtraMoreMachine) {
                registry.addRecipeCatalyst(new ItemStack(block), category);
            }
        }
    }
}

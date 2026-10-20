package com.jerry.mekextras.client.jei;

import com.jerry.mekextras.common.registries.ExtraFluidBlocks;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/** Fusion-style cooling chain pages, including the downstream Polonium-208 evaporation. */
public class NaquadahReactorRecipeWrapper implements IRecipeWrapper {

    private final FluidStack input;
    private final FluidStack output;

    private NaquadahReactorRecipeWrapper(FluidStack input, FluidStack output) {
        this.input = input.copy();
        this.output = output.copy();
    }

    public static List<NaquadahReactorRecipeWrapper> createRecipes() {
        FluidStack water = new FluidStack(FluidRegistry.WATER, 10);
        FluidStack solution = new FluidStack(ExtraFluidBlocks.POLONIUM_CONTAINING_SOLUTION, 10);
        FluidStack polonium208 = new FluidStack(ExtraFluidBlocks.POLONIUM208, 1);
        return Arrays.asList(
              new NaquadahReactorRecipeWrapper(water, solution),
              new NaquadahReactorRecipeWrapper(solution, polonium208)
        );
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.FLUID, input);
        ingredients.setOutput(VanillaTypes.FLUID, output);
    }

    public FluidStack getInput() {
        return input.copy();
    }

    public FluidStack getOutput() {
        return output.copy();
    }
}

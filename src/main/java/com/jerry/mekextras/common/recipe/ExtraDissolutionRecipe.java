package com.jerry.mekextras.common.recipe;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.inputs.MachineInput;
import mekanism.common.recipe.machines.DissolutionRecipe;
import net.minecraft.item.ItemStack;

/**
 * 1.12.2 hardcodes sulfuric acid in the built-in dissolution recipe. This extension keeps the original recipe
 * behaviour while allowing the naquadah processing chain to use hydrofluoric acid and an exact per-operation amount.
 */
public class ExtraDissolutionRecipe extends DissolutionRecipe implements IExtraDissolutionGasRecipe {

    private final Gas requiredGas;
    private final int gasUsagePerOperation;

    public ExtraDissolutionRecipe(ItemStack input, Gas requiredGas, GasStack output) {
        this(input, requiredGas, 1, output);
    }

    public ExtraDissolutionRecipe(ItemStack input, Gas requiredGas, int gasUsagePerOperation, GasStack output) {
        super(input, output);
        this.requiredGas = requiredGas;
        this.gasUsagePerOperation = Math.max(1, gasUsagePerOperation);
    }

    @Override
    public GasStack getGasInput() {
        return new GasStack(requiredGas, gasUsagePerOperation);
    }

    @Override
    public int getGasUsagePerOperation() {
        return gasUsagePerOperation;
    }

    @Override
    public boolean test(ItemStack itemInput, GasStack gasInput) {
        return MachineInput.inputContains(itemInput, getItemInput()) && gasInput != null && gasInput.getGas() == requiredGas;
    }

    @Override
    public ExtraDissolutionRecipe copy() {
        return new ExtraDissolutionRecipe(getItemInput().copy(), requiredGas, gasUsagePerOperation, getOutput().output.copy());
    }
}
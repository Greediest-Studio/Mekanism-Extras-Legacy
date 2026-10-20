package com.jerry.mekextras.common.recipe;

/** A dissolution recipe that declares a custom total gas usage per completed operation. */
public interface IExtraDissolutionGasRecipe {

    int getGasUsagePerOperation();
}
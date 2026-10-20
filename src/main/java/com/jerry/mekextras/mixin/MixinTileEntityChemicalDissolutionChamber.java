package com.jerry.mekextras.mixin;

import com.jerry.mekextras.common.recipe.IExtraDissolutionGasRecipe;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.recipe.cache.CachedRecipe;
import mekanism.common.recipe.cache.SecondaryFuelUsage;
import mekanism.common.recipe.machines.DissolutionRecipe;
import mekanism.common.tile.machine.TileEntityChemicalDissolutionChamber;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityChemicalDissolutionChamber.class)
public abstract class MixinTileEntityChemicalDissolutionChamber {

    @Shadow
    private long baseTotalUsage;

    @Shadow
    private double gasPerTickMeanMultiplier;

    @Shadow
    public abstract DissolutionRecipe getRecipe();

    @Inject(method = "createNewCachedRecipe", at = @At("HEAD"))
    private void extras$applyRecipeGasUsage(DissolutionRecipe recipe, int cacheIndex,
                                            CallbackInfoReturnable<CachedRecipe<DissolutionRecipe>> callback) {
        extras$applyRecipeGasUsage(recipe);
    }

    @Inject(method = "recalculateInjectUsage", at = @At("TAIL"))
    private void extras$reapplyRecipeGasUsage(CallbackInfo callback) {
        extras$applyRecipeGasUsage(getRecipe());
    }

    @Inject(method = "getRecipeGasUsagePerOperation", at = @At("HEAD"), cancellable = true)
    private void extras$displayRecipeGasUsage(CallbackInfoReturnable<Integer> callback) {
        DissolutionRecipe recipe = getRecipe();
        if (recipe instanceof IExtraDissolutionGasRecipe extra) {
            callback.setReturnValue(extra.getGasUsagePerOperation());
        }
    }

    private void extras$applyRecipeGasUsage(DissolutionRecipe recipe) {
        if (recipe instanceof IExtraDissolutionGasRecipe extra) {
            int ticks = Math.max(1, ((TileEntityChemicalDissolutionChamber) (Object) this).getTicksRequired());
            baseTotalUsage = SecondaryFuelUsage.fixedTotal((IUpgradeTile) (Object) this, extra.getGasUsagePerOperation());
            gasPerTickMeanMultiplier = extra.getGasUsagePerOperation() / (double) ticks;
        }
    }
}
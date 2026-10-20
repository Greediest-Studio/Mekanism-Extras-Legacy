package com.jerry.mekextras.mixin;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.util.ExtraUtils;
import mekanism.common.item.ItemBlockMachine;
import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Carries machine item data from the central machine input to the upgraded Extras machine. */
@Mixin(ShapedMekanismRecipe.class)
public class MixinShapedMekanismRecipe {

    @Inject(method = "func_77572_b", at = @At("RETURN"), cancellable = true)
    private void extras$inheritMachineData(InventoryCrafting inventory, CallbackInfoReturnable<ItemStack> callback) {
        ItemStack result = callback.getReturnValue();
        if (result.isEmpty() || !(result.getItem() instanceof ItemBlockExtraMachine)) {
            return;
        }
        ItemStack source = findSourceMachine(inventory);
        if (source.isEmpty() || !source.hasTagCompound()) {
            return;
        }
        NBTTagCompound inherited = source.getTagCompound().copy();
        inherited.removeTag("tier");
        inherited.removeTag("extraTier");
        result.setTagCompound(inherited);
        AdvancedTier tier = AdvancedTier.byIndex(ItemBlockExtraMachine.getDefaultTier(((ItemBlockExtraMachine) result.getItem()).getBlock()));
        ExtraUtils.setTierOnStack(result, tier);
        callback.setReturnValue(result);
    }

    private static ItemStack findSourceMachine(InventoryCrafting inventory) {
        ItemStack best = ItemStack.EMPTY;
        int bestScore = -1;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            int score = sourceScore(stack);
            if (score > bestScore) {
                best = stack;
                bestScore = score;
            }
        }
        return best;
    }

    private static int sourceScore(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
            return -1;
        }
        Item item = stack.getItem();
        ResourceLocation name = item.getRegistryName();
        if (name != null && "mekceumoremachine".equals(name.getNamespace())) {
            return 100;
        }
        if (item instanceof ItemBlockExtraMachine) {
            return 90;
        }
        if (item instanceof ItemBlockMachine) {
            return 80;
        }
        if (name != null && "mekanism".equals(name.getNamespace())) {
            return 70;
        }
        return -1;
    }
}

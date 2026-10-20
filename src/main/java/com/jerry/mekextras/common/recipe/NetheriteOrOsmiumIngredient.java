package com.jerry.mekextras.common.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Uses netherite ingots whenever an ingotNetherite ore-dictionary entry exists. On legacy packs without
 * netherite it transparently falls back to osmium.
 */
public class NetheriteOrOsmiumIngredient extends Ingredient {

    private static final String NETHERITE = "ingotNetherite";
    private static final String OSMIUM = "ingotOsmium";


    public NetheriteOrOsmiumIngredient() {
        super(0);
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        return selectedOres().stream().map(ItemStack::copy).toArray(ItemStack[]::new);
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        List<ItemStack> selected = selectedOres();
        IntList packed = new IntArrayList(selected.size());
        for (ItemStack stack : selected) {
            packed.add(RecipeItemHelper.pack(stack));
        }
        packed.sort(IntComparators.NATURAL_COMPARATOR);
        return packed;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }
        for (ItemStack target : selectedOres()) {
            if (OreDictionary.itemMatches(target, input, false)) {
                return true;
            }
        }
        return false;
    }

    private static List<ItemStack> selectedOres() {
        List<ItemStack> netherite = OreDictionary.getOres(NETHERITE, false);
        return netherite.isEmpty() ? OreDictionary.getOres(OSMIUM, false) : netherite;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
    }

    @Override
    public boolean isSimple() {
        return true;
    }
}
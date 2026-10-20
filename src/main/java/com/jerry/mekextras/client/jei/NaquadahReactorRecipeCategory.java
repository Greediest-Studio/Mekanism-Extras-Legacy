package com.jerry.mekextras.client.jei;

import com.jerry.mekextras.common.registries.ExtraBlocks;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.IProgressInfoHandler;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.jei.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class NaquadahReactorRecipeCategory extends BaseRecipeCategory<NaquadahReactorRecipeWrapper> {

    public static final String UID = "mekanism_extras:naquadah_reactor";
    private GuiFluidGauge inputGauge;
    private GuiFluidGauge outputGauge;

    public NaquadahReactorRecipeCategory(IGuiHelper helper) {
        super(helper, UID, "gui.mekanism_extras.naquadah_reactor_recipe",
              helper.createDrawableIngredient(new net.minecraft.item.ItemStack(ExtraBlocks.NAQUADAH_REACTOR_CONTROLLER)),
              24, 12, 130, 63, ProgressType.LARGE_RIGHT);
    }

    @Override
    protected void addGuiElements() {
        inputGauge = dummyFluidGauge(GuiFluidGauge.Type.STANDARD, GuiFluidGauge.GaugeColor.RED, 25, 13);
        outputGauge = dummyFluidGauge(GuiFluidGauge.Type.STANDARD, GuiFluidGauge.GaugeColor.BLUE, 133, 13);
        guiElements.add(inputGauge);
        guiElements.add(outputGauge);
        guiElements.add(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (float) timer.getValue() / 20F;
            }

            @Override
            public boolean isGuiInJei() {
                return true;
            }
        }, progressType, this, 62, 38));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, NaquadahReactorRecipeWrapper wrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        initFluid(fluids, 0, true, inputGauge, wrapper.getInput());
        initFluid(fluids, 1, false, outputGauge, wrapper.getOutput());
    }
}

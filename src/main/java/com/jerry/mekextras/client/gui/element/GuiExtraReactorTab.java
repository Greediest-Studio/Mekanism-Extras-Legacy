package com.jerry.mekextras.client.gui.element;

import com.jerry.mekextras.client.gui.GuiExtraReactorFuel;
import com.jerry.mekextras.client.gui.GuiExtraReactorHeat;
import com.jerry.mekextras.client.gui.GuiExtraReactorStats;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;
import mekanism.client.gui.element.tab.TabType;
import mekanism.client.render.lib.ColorAtlas.ColorRegistryObject;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorTab extends GuiTabElementType<TileEntityExtraReactorController, GuiExtraReactorTab.ReactorTab> {

    private static final ResourceLocation HEAT_ICON = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "heat.png");
    private static final ResourceLocation FUEL_ICON = new ResourceLocation("mekanismgenerators", "gui/fuel.png");
    private static final ResourceLocation STATS_ICON = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "stats.png");

    public GuiExtraReactorTab(IGuiWrapper gui, TileEntityExtraReactorController tile, ReactorTab type) {
        super(gui, tile, type);
    }

    public enum ReactorTab implements TabType<TileEntityExtraReactorController> {
        HEAT(HEAT_ICON, "gui.heat", 6, SpecialColors.TAB_HEAT_CONFIG),
        FUEL(FUEL_ICON, "gui.fuel", 34, SpecialColors.TAB_GAS_CONFIG),
        STATS(STATS_ICON, "gui.stats", 62, SpecialColors.TAB_MULTIBLOCK_STATS);

        private final ResourceLocation resource;
        private final String descriptionKey;
        private final int yPos;
        private final ColorRegistryObject color;

        ReactorTab(ResourceLocation resource, String descriptionKey, int yPos, ColorRegistryObject color) {
            this.resource = resource;
            this.descriptionKey = descriptionKey;
            this.yPos = yPos;
            this.color = color;
        }

        @Override
        public ResourceLocation getResource() {
            return resource;
        }

        @Override
        public void onClick(TileEntityExtraReactorController tile) {
            Minecraft minecraft = Minecraft.getMinecraft();
            switch (this) {
                case HEAT -> minecraft.displayGuiScreen(new GuiExtraReactorHeat(minecraft.player.inventory, tile));
                case FUEL -> minecraft.displayGuiScreen(new GuiExtraReactorFuel(minecraft.player.inventory, tile));
                case STATS -> minecraft.displayGuiScreen(new GuiExtraReactorStats(minecraft.player.inventory, tile));
            }
        }

        @Override
        public ITextComponent getDescription() {
            return new TextComponentString(LangUtils.localize(descriptionKey));
        }

        @Override
        public int getYPos() {
            return yPos;
        }

        @Override
        public ColorRegistryObject getTabColor() {
            return color;
        }
    }
}

package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * The additional factory tiers added by Mekanism Extras Legacy. Each block is one tier of one factory type, mirroring
 * how Mekanism's own factories work.
 */
public class BlockExtraFactory extends BlockExtraMachine {

    private final ExtraFactoryTier tier;
    private final RecipeType recipeType;

    public BlockExtraFactory(ExtraFactoryTier tier, RecipeType recipeType) {
        super(Material.IRON);
        this.tier = tier;
        this.recipeType = recipeType;
    }

    public ExtraFactoryTier getTier() {
        return tier;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    private static MachineType machineTypeFor(ExtraFactoryTier tier) {
        switch (tier) {
            case SUPREME:
                return MachineType.ADVANCED_FACTORY;
            case COSMIC:
                return MachineType.ELITE_FACTORY;
            case INFINITE:
                return MachineType.ULTIMATE_FACTORY;
            case ABSOLUTE:
            default:
                return MachineType.BASIC_FACTORY;
        }
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.FACTORY;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityExtraFactory tile = com.jerry.mekextras.common.tile.factory.ExtraFactoryTiles.forTier(tier);
        tile.setRecipeType(recipeType);
        return tile;
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getFactoryBlock(recipeType, ExtraFactoryTier.byIndex(advancedTier.ordinal()));
    }
}

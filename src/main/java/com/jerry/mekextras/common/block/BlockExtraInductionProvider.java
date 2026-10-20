package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.IPTier;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * A single induction matrix provider of one of the extra tiers.
 */
public class BlockExtraInductionProvider extends BlockExtraMachine {

    private final IPTier tier;

    public BlockExtraInductionProvider(IPTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public IPTier getTier() {
        return tier;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityExtraInductionProvider provider = new TileEntityExtraInductionProvider();
        provider.tier = tier;
        return provider;
    }

    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getInductionProviderBlock(advancedTier);
    }
}

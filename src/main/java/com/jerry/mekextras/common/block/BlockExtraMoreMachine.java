package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.tier.ExtraMachineTier;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * The extra tier versions of the MoreMachine processing machines (crystallizer, dissolution chamber, oxidizer,
 * nutritional liquifier, organic farm). Each block is one (kind, tier) pair; the tier decides the number of parallel
 * processes (11/13/15/17, continuing MoreMachine's own 3/5/7/9).
 */
public class BlockExtraMoreMachine extends BlockExtraMachine {

    private final ExtraMachineTier tier;
    private final Function<ExtraMachineTier, TileEntity> tileFactory;
    private final int guiId;

    public BlockExtraMoreMachine(ExtraMachineTier tier, Function<ExtraMachineTier, TileEntity> tileFactory, int guiId) {
        super(Material.IRON);
        this.tier = tier;
        this.tileFactory = tileFactory;
        this.guiId = guiId;
    }

    public ExtraMachineTier getTier() {
        return tier;
    }

    @Override
    public int getGuiId() {
        return guiId;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return tileFactory.apply(tier);
    }
}

package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.registries.ExtraItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockExtraOre extends BlockExtraResource {

    private final Item drop;

    public BlockExtraOre(Item drop, float hardness, float resistance, int harvestLevel) {
        super(hardness, resistance);
        this.drop = drop;
        setHarvestLevel("pickaxe", harvestLevel);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return drop;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && drop != null) {
            return 1 + random.nextInt(fortune + 1);
        }
        return quantityDropped(random);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return 3 + fortune;
    }
}

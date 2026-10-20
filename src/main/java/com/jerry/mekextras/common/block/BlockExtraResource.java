package com.jerry.mekextras.common.block;

import com.jerry.mekextras.MekanismExtras;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockExtraResource extends Block {

    public BlockExtraResource(float hardness, float resistance) {
        super(Material.ROCK);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(SoundType.STONE);
        setCreativeTab(MekanismExtras.tabExtra);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getLightValue(state, world, pos);
    }
}

package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.config.ExtraConfig;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class ExtraWorldGenerator implements IWorldGenerator {

    private static final int OVERWORLD = 0;
    private static final int THE_END = 1;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int dimension = world.provider.getDimension();
        if (dimension == OVERWORLD && ExtraConfig.enableNaquadahOre) {
            WorldGenMinable generator = new WorldGenMinable(ExtraBlocks.NAQUADAH_ORE.getDefaultState(), ExtraConfig.naquadahSmallVeinSize);
            for (int i = 0; i < ExtraConfig.naquadahSmallVeinsPerChunk; i++) {
                int y = ExtraConfig.naquadahSmallMinHeight
                      + random.nextInt(ExtraConfig.naquadahSmallMaxHeight - ExtraConfig.naquadahSmallMinHeight + 1);
                BlockPos pos = new BlockPos(chunkX * 16 + random.nextInt(16), y, chunkZ * 16 + random.nextInt(16));
                generator.generate(world, random, pos);
            }
        } else if (dimension == THE_END && ExtraConfig.enableEndNaquadahOre) {
            WorldGenMinable generator = new WorldGenMinable(ExtraBlocks.END_NAQUADAH_ORE.getDefaultState(), ExtraConfig.endNaquadahVeinSize, state -> state.getBlock() == Blocks.END_STONE);
            for (int i = 0; i < ExtraConfig.endNaquadahVeinsPerChunk; i++) {
                int y = ExtraConfig.endNaquadahMinHeight
                      + random.nextInt(ExtraConfig.endNaquadahMaxHeight - ExtraConfig.endNaquadahMinHeight + 1);
                BlockPos pos = new BlockPos(chunkX * 16 + random.nextInt(16), y, chunkZ * 16 + random.nextInt(16));
                generator.generate(world, random, pos);
            }
        }
    }
}

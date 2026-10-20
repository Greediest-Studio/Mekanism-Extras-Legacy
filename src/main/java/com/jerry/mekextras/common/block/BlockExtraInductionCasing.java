package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.ExtraGuiIds;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCasing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * The reinforced induction matrix casing and port added by Mekanism Extras Legacy.
 */
public class BlockExtraInductionCasing extends BlockExtraMachine {

    private final boolean port;

    public BlockExtraInductionCasing(boolean port) {
        super(Material.IRON);
        this.port = port;
    }

    public boolean isPort() {
        return port;
    }

    @Override
    public int getGuiId() {
        return ExtraGuiIds.INDUCTION_MATRIX;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityExtraInductionCasing matrix && matrix.isFormed()) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        return true;
    }
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return port ? new com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionPort()
              : new TileEntityExtraInductionCasing();
    }
}

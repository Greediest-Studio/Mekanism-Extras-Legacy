package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLaserFocusMatrix;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorPort;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * The blocks of the naquadah reactor multiblock: casing, port, controller, logic adapter and laser focus matrix.
 */
public class BlockExtraReactor extends BlockExtraMachine {

    public enum Kind {
        CASING,
        PORT,
        CONTROLLER,
        LOGIC_ADAPTER,
        LASER_FOCUS_MATRIX
    }

    private final Kind kind;

    public BlockExtraReactor(Kind kind) {
        super(Material.IRON);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return kind == Kind.LASER_FOCUS_MATRIX ? BlockRenderLayer.TRANSLUCENT : super.getRenderLayer();
    }

    @Override
    public int getGuiId() {
        if (kind == Kind.CONTROLLER) {
            return com.jerry.mekextras.common.ExtraGuiIds.REACTOR_CONTROLLER;
        } else if (kind == Kind.LOGIC_ADAPTER) {
            return com.jerry.mekextras.common.ExtraGuiIds.REACTOR_LOGIC_ADAPTER;
        }
        return -1;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof mekanism.common.multiblock.IMultiblock<?> multiblock) {
                multiblock.doUpdate();
            }
        }
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, net.minecraft.entity.EntityLivingBase placer, net.minecraft.item.ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof mekanism.common.multiblock.IMultiblock<?> multiblock) {
                multiblock.doUpdate();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        if (kind == Kind.CONTROLLER) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityExtraReactorController controller && controller.isFormed() && !world.isRemote) {
                player.openGui(com.jerry.mekextras.MekanismExtras.instance,
                        com.jerry.mekextras.common.ExtraGuiIds.REACTOR_CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    @Override
    @Deprecated
    public boolean canProvidePower(IBlockState state) {
        return kind == Kind.LOGIC_ADAPTER;
    }

    @Override
    @Deprecated
    public int getWeakPower(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (kind != Kind.LOGIC_ADAPTER) {
            return 0;
        }
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityExtraReactorLogicAdapter adapter ? adapter.getRedstoneLevel(side) : 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (kind) {
            case PORT:
                return new TileEntityExtraReactorPort();
            case CONTROLLER:
                return new TileEntityExtraReactorController();
            case LOGIC_ADAPTER:
                return new TileEntityExtraReactorLogicAdapter();
            case LASER_FOCUS_MATRIX:
                return new TileEntityExtraReactorLaserFocusMatrix();
            case CASING:
            default:
                return new TileEntityExtraReactorCasing();
        }
    }
}

package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.block.states.BlockStateExtraMachine;
import com.jerry.mekextras.common.util.ExtraUtils;
import mekanism.client.render.JsonModelSelectionBoxCache;
import mekanism.client.render.SpecialSelectionWireframeRegistry;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.interfaces.IHighlightBoxProvider;
import mekanism.common.base.ISpecialSelectionWireframeTile;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Base class of every machine block added by Mekanism Extras Legacy.
 * <p>
 * Each tier is its own block (like the upstream mod), the block state only carries the facing and active flags.
 */
public abstract class BlockExtraMachine extends BlockMekanismContainer implements IHighlightBoxProvider {

    protected BlockExtraMachine(Material material) {
        super(material);
        setHardness(3.5F);
        setResistance(16F);
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateExtraMachine(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockStateFacing.facingProperty).ordinal();
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.VALUES[meta % EnumFacing.VALUES.length];
        return getDefaultState().withProperty(BlockStateFacing.facingProperty, facing).withProperty(BlockStateExtraMachine.activeProperty, false);
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = MekanismUtils.getTileEntitySafe(world, pos);
        if (tile instanceof TileEntityBasicBlock) {
            TileEntityBasicBlock basicTile = (TileEntityBasicBlock) tile;
            EnumFacing facing = basicTile.facing;
            if (facing == null || facing.getAxis() == EnumFacing.Axis.Y) {
                //Only horizontal facing variants exist, never let a vertical facing hide the model.
                facing = EnumFacing.NORTH;
            }
            state = state.withProperty(BlockStateFacing.facingProperty, facing);
        }
        return state.withProperty(BlockStateExtraMachine.activeProperty, ExtraUtils.isActive(tile));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        ExtraUtils.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        ExtraUtils.breakBlock(world, pos, state);
        super.breakBlock(world, pos, state);
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityBasicBlock) {
                ((TileEntityBasicBlock) tile).onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return ExtraUtils.onBlockActivated(this, getGuiId(), world, pos, state, player, hand, side);
    }

    /**
     * @return the id used to open this block's GUI, -1 if the block has no GUI
     */
    public int getGuiId() {
        return -1;
    }

    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return ExtraUtils.getDropItem(this, state, world, pos);
    }

    @Override
    @Deprecated
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return ExtraUtils.getComparatorInputOverride(state, world, pos);
    }

    @Override
    public EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos) {
        return ExtraUtils.getValidRotations(world, pos);
    }

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
        return ExtraUtils.rotateBlock(world, pos, axis);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Deprecated
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB[] getHighlightBoxes(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ISpecialSelectionWireframeTile) {
            JsonModelSelectionBoxCache.OutlineBox[] outlines = SpecialSelectionWireframeRegistry.getWireframes(state, world, pos);
            if (outlines.length > 0) {
                AxisAlignedBB[] boxes = new AxisAlignedBB[outlines.length];
                for (int i = 0; i < outlines.length; i++) {
                    boxes[i] = outlines[i].getBounds();
                }
                return boxes;
            }
        }
        AxisAlignedBB[] modelBoxes = JsonModelSelectionBoxCache.getBoxes(state, world, pos);
        return modelBoxes.length == 0 ? new AxisAlignedBB[]{state.getBoundingBox(world, pos)} : modelBoxes;
    }

    @Override
    public float getHighlightRed(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public float getHighlightGreen(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public float getHighlightBlue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public float getHighlightAlpha(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0.4F;
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
    }
}




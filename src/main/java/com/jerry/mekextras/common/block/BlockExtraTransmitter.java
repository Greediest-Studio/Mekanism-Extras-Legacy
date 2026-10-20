package com.jerry.mekextras.common.block;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.util.ExtraUtils;
import mekanism.common.block.BlockTransmitter;
import mekanism.common.block.states.BlockStateTransmitter.TransmitterType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * The extra tier transmitters. Each block is one (type, tier) pair, mirroring how Mekanism's own transmitter block
 * carries a tier property but keeping the tiers of this mod separate.
 */
public class BlockExtraTransmitter extends BlockTransmitter {

    private final TransmitterType type;
    private final AdvancedTier tier;

    public BlockExtraTransmitter(TransmitterType type, AdvancedTier tier) {
        super();
        this.type = type;
        this.tier = tier;
        setDefaultState(getDefaultState().withProperty(mekanism.common.block.states.BlockStateTransmitter.typeProperty, type));
    }

    public TransmitterType getTransmitterType() {
        return type;
    }

    public AdvancedTier getExtraTier() {
        return tier;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(mekanism.common.block.states.BlockStateTransmitter.typeProperty, type);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return ExtraBlocks.createTransmitterTile(type, tier);
    }

    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return ExtraUtils.getDropItem(this, state, world, pos);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return ExtraUtils.getDropItem(this, state, world, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
    }
}
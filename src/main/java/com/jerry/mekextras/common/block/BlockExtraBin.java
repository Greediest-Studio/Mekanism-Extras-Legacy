package com.jerry.mekextras.common.block;

import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.BTier;
import com.jerry.mekextras.common.tile.TileEntityExtraBin;
import mekanism.api.IMekWrench;
import mekanism.api.NBTConstants;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockExtraBin extends BlockExtraMachine {

    private final BTier tier;

    public BlockExtraBin(BTier tier) {
        super(Material.IRON);
        this.tier = tier;
    }

    public BTier getTier() {
        return tier;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityExtraBin(tier);
    }

    public Block getBlockForTier(com.jerry.mekextras.api.tier.AdvancedTier advancedTier) {
        return ExtraBlocks.getBinBlock(advancedTier);
    }

    @Override
    public void onBlockClicked(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityExtraBin bin)) {
            return;
        }
        RayTraceResult trace = MekanismUtils.rayTrace(world, player);
        if (trace == null || trace.sideHit != bin.facing) {
            return;
        }
        ItemStack removed = player.isSneaking() ? bin.remove(1, false) : bin.removeStack();
        if (removed.isEmpty()) {
            return;
        }
        if (!player.inventory.addItemStackToInventory(removed.copy())) {
            BlockPos dropPos = pos.offset(bin.facing);
            Entity item = new EntityItem(world, dropPos.getX() + 0.5F, dropPos.getY() + 0.3F, dropPos.getZ() + 0.5F, removed);
            item.addVelocity(-item.motionX, -item.motionY, -item.motionZ);
            world.spawnEntity(item);
        } else {
            world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                  net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, net.minecraft.util.SoundCategory.PLAYERS,
                  0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityExtraBin bin)) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        ItemStack held = player.getHeldItem(hand);

        // Configuring a bin toggles auto-output. Do not let the configurator become bin contents.
        int configuratorMode = ItemDataUtils.getInt(held, NBTConstants.STATE);
        if (held.getItem() instanceof ItemConfigurator && configuratorMode >= 0 && configuratorMode < 5) {
            if (!world.isRemote) {
                bin.onSneakRightClick(player, side);
            }
            return true;
        }

        IMekWrench wrench = held.isEmpty() ? null : Wrenches.getHandler(held);
        if (wrench != null) {
            RayTraceResult trace = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
            if (wrench.canUseWrench(player, hand, held, trace)) {
                if (!world.isRemote) {
                    wrench.wrenchUsed(player, hand, held, trace);
                    if (player.isSneaking()) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                    } else {
                        bin.setFacing(bin.facing.rotateY());
                        world.notifyNeighborsOfStateChange(pos, this, true);
                    }
                }
                return true;
            }
        }

        // Match Mekanism's repeated-click bulk deposit: the first click inserts the held stack and subsequent
        // clicks continue draining the player's main inventory while addTicks remains active.
        if (!world.isRemote && bin.getItemCount() < bin.getMaxStoredCount()) {
            if (bin.addTicks == 0) {
                if (!held.isEmpty()) {
                    player.setHeldItem(hand, bin.add(held, false));
                    bin.addTicks = 5;
                }
            } else if (bin.addTicks > 0 && bin.getItemCount() > 0) {
                NonNullList<ItemStack> inventory = player.inventory.mainInventory;
                for (int i = 0; i < inventory.size(); i++) {
                    if (bin.getItemCount() == bin.getMaxStoredCount()) {
                        break;
                    }
                    ItemStack stack = inventory.get(i);
                    if (!stack.isEmpty()) {
                        inventory.set(i, bin.add(stack, false));
                        bin.addTicks = 5;
                    }
                }
                if (player instanceof EntityPlayerMP playerMP) {
                    playerMP.sendContainerToPlayer(player.openContainer);
                }
            }
        }
        return true;
    }
}
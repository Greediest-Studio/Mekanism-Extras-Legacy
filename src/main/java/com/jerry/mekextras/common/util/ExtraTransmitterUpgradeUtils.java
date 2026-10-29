package com.jerry.mekextras.common.util;

import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.block.BlockExtraTransmitter;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import mekanism.api.Coord4D;
import mekanism.api.transmitters.DynamicNetwork;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.block.states.BlockStateTransmitter;
import mekanism.common.block.states.BlockStateTransmitter.TransmitterType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.transmitters.TransmitterImpl;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Upgrades a connected transmitter network with the Extra alloy matching the requested tier.
 */
public final class ExtraTransmitterUpgradeUtils {

    private ExtraTransmitterUpgradeUtils() {
    }

    public static boolean isUpgradeEnabled() {
        return MekanismConfig.current().general.allowTransmitterAlloyUpgrade.val();
    }

    public static boolean canUpgrade(World world, BlockPos pos, AdvancedTier targetTier) {
        if (!isUpgradeEnabled() || targetTier == null) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityTransmitter)) {
            return false;
        }
        TileEntityTransmitter clicked = (TileEntityTransmitter) tile;
        DynamicNetwork network = clicked.getTransmitter().hasTransmitterNetwork()
              ? clicked.getTransmitter().getTransmitterNetwork() : null;
        for (TileEntityTransmitter candidate : collectCandidates(clicked, network)) {
            if (!candidate.isInvalid() && isEligible(candidate, targetTier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true when at least one transmitter was upgraded and the held alloy should be consumed
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean tryUpgrade(EntityPlayer player, World world, BlockPos pos, ItemStack stack, AdvancedTier targetTier) {
        if (world.isRemote || !canUpgrade(world, pos, targetTier)) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityTransmitter)) {
            return false;
        }
        TileEntityTransmitter clicked = (TileEntityTransmitter) tile;
        DynamicNetwork network = clicked.getTransmitter().hasTransmitterNetwork()
              ? clicked.getTransmitter().getTransmitterNetwork() : null;
        List<TileEntityTransmitter> candidates = collectCandidates(clicked, network);
        Coord4D origin = new Coord4D(pos, world);
        candidates.sort(Comparator.comparingDouble(candidate -> new Coord4D(candidate.getPos(), candidate.getWorld()).distanceTo(origin)));
        int upgraded = 0;
        for (TileEntityTransmitter candidate : candidates) {
            if (candidate.isInvalid() || !isEligible(candidate, targetTier)) {
                continue;
            }
            Block targetBlock = ExtraBlocks.getTransmitterBlock(candidate.getTransmitterType(), targetTier);
            if (targetBlock == null || candidate.getBlockType() == targetBlock) {
                continue;
            }
            TransmitterImpl oldTransmitter = candidate.getTransmitter();
            DynamicNetwork transmitterNetwork = oldTransmitter.hasTransmitterNetwork()
                  ? oldTransmitter.getTransmitterNetwork() : network;
            if (transmitterNetwork != null) {
                oldTransmitter.takeShare();
            }
            if (replaceTransmitter(world, candidate, targetBlock, targetTier, transmitterNetwork, oldTransmitter)) {
                upgraded++;
                if (upgraded == 8) {
                    break;
                }
            }
        }
        if (upgraded == 0) {
            return false;
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        if (player instanceof EntityPlayerMP) {
            MekanismCriteriaTriggers.ALLOY_UPGRADE.trigger((EntityPlayerMP) player);
        }
        return true;
    }

    private static List<TileEntityTransmitter> collectCandidates(TileEntityTransmitter clicked, DynamicNetwork network) {
        Set<TileEntityTransmitter> candidates = new LinkedHashSet<>();
        if (network != null) {
            for (Object transmitter : network.getTransmitters()) {
                if (transmitter instanceof TransmitterImpl implementation && implementation.containingTile != null) {
                    candidates.add(implementation.containingTile);
                }
            }
        }
        candidates.add(clicked);
        return new ArrayList<>(candidates);
    }

    private static boolean isEligible(TileEntity tile, AdvancedTier targetTier) {
        if (!(tile instanceof TileEntityTransmitter)) {
            return false;
        }
        TileEntityTransmitter transmitter = (TileEntityTransmitter) tile;
        if (ExtraBlocks.getTransmitterBlock(transmitter.getTransmitterType(), targetTier) == null) {
            return false;
        }
        Block currentBlock = tile.getBlockType();
        if (currentBlock instanceof BlockExtraTransmitter) {
            AdvancedTier currentTier = ((BlockExtraTransmitter) currentBlock).getExtraTier();
            return currentTier.ordinal() + 1 == targetTier.ordinal();
        }
        return targetTier == AdvancedTier.FINAL && transmitter.getBaseTier() == BaseTier.ULTIMATE;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean replaceTransmitter(World world, TileEntityTransmitter oldTile, Block targetBlock,
                                              AdvancedTier targetTier, DynamicNetwork network,
                                              TransmitterImpl oldTransmitter) {
        BlockPos pos = oldTile.getPos();
        IBlockState oldState = world.getBlockState(pos);
        NBTTagCompound original = oldTile.writeToNBT(new NBTTagCompound());
        NBTTagCompound targetData = original.copy();
        targetData.setInteger("tier", targetTier.ordinal());
        targetData.setInteger("extraTransporterTier", targetTier.ordinal());
        TransmitterType type = oldTile.getTransmitterType();
        IBlockState targetState = targetBlock.getDefaultState()
              .withProperty(BlockStateTransmitter.typeProperty, type);
        if (!world.setBlockState(pos, targetState, 3)) {
            return false;
        }
        TileEntity targetTile = world.getTileEntity(pos);
        if (!(targetTile instanceof TileEntityTransmitter)) {
            restoreTransmitter(world, pos, oldState, original);
            return false;
        }
        TileEntityTransmitter newTile = (TileEntityTransmitter) targetTile;
        try {
            newTile.readFromNBT(targetData);
        } catch (RuntimeException exception) {
            restoreTransmitter(world, pos, oldState, original);
            return false;
        }
        if (world.getBlockState(pos).getBlock() != targetBlock) {
            restoreTransmitter(world, pos, oldState, original);
            return false;
        }
        if (network != null) {
            TransmitterImpl newTransmitter = newTile.getTransmitter();
            newTransmitter.setTransmitterNetwork(network);
            network.addNewTransmitters(java.util.Collections.singleton(newTransmitter));
            network.commit();
            network.removeTransmitter(oldTransmitter);
            network.updateCapacity();
            network.clampBuffer();
            network.register();
        }
        newTile.markDirty();
        world.notifyBlockUpdate(pos, targetState, targetState, 3);
        world.notifyNeighborsOfStateChange(pos, targetBlock, true);
        return true;
    }

    private static void restoreTransmitter(World world, BlockPos pos, IBlockState oldState, NBTTagCompound snapshot) {
        world.setBlockState(pos, oldState, 3);
        TileEntity restored = world.getTileEntity(pos);
        if (restored != null) {
            restored.readFromNBT(snapshot);
            restored.markDirty();
        }
        world.notifyBlockUpdate(pos, oldState, oldState, 3);
        world.notifyNeighborsOfStateChange(pos, oldState.getBlock(), true);
    }
}

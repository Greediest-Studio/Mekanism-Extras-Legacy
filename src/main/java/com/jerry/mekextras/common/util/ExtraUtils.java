package com.jerry.mekextras.common.util;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.block.BlockExtraMachine;
import com.jerry.mekextras.common.block.states.BlockStateExtraMachine;
import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ExtraFactoryTier;
import mekanism.api.Coord4D;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.base.IBaseTierProvider;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.base.ISustainedTank;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.TileEntityBin;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.TileEntityGasTank;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismPlacementData;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.SecurityUtils;
import mekceumoremachine.common.tile.machine.TierCrystallizer.TileEntityTierChemicalCrystallizer;
import mekceumoremachine.common.tile.machine.TierDissolution.TileEntityTierChemicalDissolutionChamber;
import mekceumoremachine.common.tile.machine.TierNutritional.TileEntityTierNutritionalLiquifier;
import mekceumoremachine.common.tile.machine.TierOrganicFarm.TileEntityTierOrganicFarm;
import mekceumoremachine.common.tile.machine.TierOxidizer.TileEntityTierChemicalOxidizer;
import mekceumoremachine.common.tile.machine.TileEntityTierChemicalInfuser;
import mekceumoremachine.common.tile.machine.TileEntityTierChemicalWasher;
import mekceumoremachine.common.tile.machine.TileEntityTierIsotopicCentrifuge;
import mekceumoremachine.common.tile.machine.TileEntityTierRadioactiveWasteBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtraUtils {

    public static final String TIER_NBT_KEY = "extraTier";

    private ExtraUtils() {
    }

    public static boolean isActive(TileEntity tile) {
        return tile instanceof IActiveState && ((IActiveState) tile).getActive();
    }

    public static void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            TileEntityBasicBlock basicTile = (TileEntityBasicBlock) tile;
            EnumFacing change = EnumFacing.SOUTH;
            if (placer != null) {
                if (basicTile.canSetFacing(EnumFacing.DOWN) && basicTile.canSetFacing(EnumFacing.UP)) {
                    int height = Math.round(placer.rotationPitch);
                    if (height >= 65) {
                        change = EnumFacing.UP;
                    } else if (height <= -65) {
                        change = EnumFacing.DOWN;
                    }
                }
                if (change != EnumFacing.DOWN && change != EnumFacing.UP) {
                    int side = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
                    switch (side) {
                        case 0:
                            change = EnumFacing.NORTH;
                            break;
                        case 1:
                            change = EnumFacing.EAST;
                            break;
                        case 2:
                            change = EnumFacing.SOUTH;
                            break;
                        default:
                            change = EnumFacing.WEST;
                            break;
                    }
                }
            }
            basicTile.facing = change;
            basicTile.redstone = world.getRedstonePowerFromNeighbors(pos) > 0;
            if (tile instanceof IBoundingBlock boundingBlock) {
                IBoundingBlock.PlacementResult result = boundingBlock.tryPlaceBoundingBlocks(world, Coord4D.get(tile));
                if (result == IBoundingBlock.PlacementResult.LEGACY) {
                    boundingBlock.onPlace();
                } else if (result == IBoundingBlock.PlacementResult.BLOCKED) {
                    world.removeTileEntity(pos);
                    world.setBlockToAir(pos);
                    return;
                }
            }
        }
        MekanismPlacementData.apply(world, pos, placer == null ? null : placer, stack);
    }

    public static void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IBoundingBlock boundingBlock && !boundingBlock.removeBoundingBlocks(world, pos)) {
            boundingBlock.onBreak();
        }
    }

    public static boolean onBlockActivated(Block block, int guiId, World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return false;
        }
        if (!SecurityUtils.canAccess(player, tile)) {
            SecurityUtils.displayNoAccess(player);
            return true;
        }
        if (guiId >= 0 && !player.isSneaking()) {
            player.openGui(MekanismExtras.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    public static ItemStack getDropItem(Block block, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        // Every machine tier is its own block; metadata only describes facing/active state and must never leak into the item.
        ItemStack itemStack = new ItemStack(block, 1, 0);
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IExtraTierMachine) {
            itemStack.getTagCompound().setInteger(TIER_NBT_KEY, ((IExtraTierMachine) tile).getExtraTier().ordinal());
        }
        if (tile instanceof ISustainedInventory && itemStack.getItem() instanceof ISustainedInventory) {
            NBTTagList inventory = ((ISustainedInventory) tile).getInventory();
            if (inventory != null && inventory.tagCount() > 0) {
                ((ISustainedInventory) itemStack.getItem()).setInventory(inventory, itemStack);
            }
        }
        if (tile instanceof IStrictEnergyStorage) {
            IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
            StorageUtils.setStoredEnergy(itemStack, storage.getEnergy(), storage.getMaxEnergy());
            ItemDataUtils.setDouble(itemStack, "energyStored", storage.getEnergy());
        }
        if (tile instanceof com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank tank
                && itemStack.getItem() instanceof com.jerry.mekextras.common.item.ItemBlockExtraMachine item) {
            item.setGasStack(itemStack, tank.gasTank.getGas());
        }
        if (tile instanceof ISustainedTank && itemStack.getItem() instanceof ISustainedTank) {
            FluidStack fluid = ((ISustainedTank) tile).getFluidStack();
            if (fluid != null) {
                ((ISustainedTank) itemStack.getItem()).setFluidStack(fluid, itemStack);
            }
        }
        if (tile instanceof mekanism.common.base.ISustainedData sustainedData && itemStack.getItem() instanceof mekanism.common.base.ISustainedInventory) {
            sustainedData.writeSustainedData(itemStack);
        }
        if (tile instanceof mekanism.common.base.IUpgradeTile upgradeTile && itemStack.getItem() instanceof ItemBlockExtraMachine) {
            upgradeTile.writeUpgrades(ItemDataUtils.getDataMap(itemStack));
        }
        if (tile instanceof mekanism.common.base.ISideConfiguration sideConfig && itemStack.getItem() instanceof ItemBlockExtraMachine) {
            if (sideConfig.getConfig() != null) {
                sideConfig.getConfig().write(ItemDataUtils.getDataMap(itemStack));
            }
            if (sideConfig.getEjector() != null) {
                sideConfig.getEjector().write(ItemDataUtils.getDataMap(itemStack));
            }
        }
        if (tile instanceof mekanism.common.base.IRedstoneControl redstoneControl && itemStack.getItem() instanceof ItemBlockExtraMachine) {
            ItemDataUtils.setInt(itemStack, "controlType", redstoneControl.getControlType().ordinal());
        }
        if (tile instanceof mekanism.common.security.ISecurityTile securityTile && itemStack.getItem() instanceof ItemBlockExtraMachine itemMachine) {
            itemMachine.setOwnerUUID(itemStack, securityTile.getSecurity().getOwnerUUID());
            itemMachine.setSecurity(itemStack, securityTile.getSecurity().getMode());
        }
        return itemStack;
    }

    /**
     * Replaces an Extras machine with the next tier while keeping its complete tile state.
     */
    public static boolean applyExtraTier(World world, BlockPos pos, AdvancedTier tier) {
        if (world.isRemote || tier == null) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IExtraTierMachine)) {
            return false;
        }
        IExtraTierMachine machine = (IExtraTierMachine) tile;
        if (!machine.canInstallExtraTier(tier)) {
            return false;
        }
        Block targetBlock = machine.getBlockForTier(tier);
        return targetBlock != null && replaceWithExtraTier(world, pos, tile, targetBlock, tier);
    }

    /**
     * @return whether an upstream Mekanism or MoreMachine ultimate block has an Absolute counterpart in this mod
     */
    public static boolean canApplyUpstreamExtraTier(TileEntity tile, AdvancedTier tier) {
        return tile != null && tier == AdvancedTier.ABSOLUTE && !(tile instanceof IExtraTierMachine)
              && getUpstreamAbsoluteBlock(tile) != null;
    }

    /**
     * Replaces an upstream ultimate block with its Absolute counterpart while carrying the complete tile snapshot.
     */
    public static boolean applyUpstreamExtraTier(World world, BlockPos pos, AdvancedTier tier) {
        if (world.isRemote || tier != AdvancedTier.ABSOLUTE) {
            return false;
        }
        TileEntity oldTile = world.getTileEntity(pos);
        Block targetBlock = getUpstreamAbsoluteBlock(oldTile);
        return targetBlock != null && replaceWithExtraTier(world, pos, oldTile, targetBlock, AdvancedTier.ABSOLUTE);
    }

    private static boolean replaceWithExtraTier(World world, BlockPos pos, TileEntity oldTile,
                                                Block targetBlock, AdvancedTier tier) {
        if (!(targetBlock instanceof BlockExtraMachine)) {
            return false;
        }
        IBlockState oldState = world.getBlockState(pos);
        NBTTagCompound originalSnapshot = oldTile.writeToNBT(new NBTTagCompound());
        NBTTagCompound snapshot = originalSnapshot.copy();
        snapshot.setInteger("tier", tier.ordinal());
        snapshot.setInteger(TIER_NBT_KEY, tier.ordinal());
        EnumFacing facing = oldTile instanceof TileEntityBasicBlock ? ((TileEntityBasicBlock) oldTile).facing : EnumFacing.NORTH;
        if (facing == null || facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        IBlockState targetState = targetBlock.getDefaultState()
              .withProperty(BlockStateFacing.facingProperty, facing)
              .withProperty(BlockStateExtraMachine.activeProperty, false);
        if (!world.setBlockState(pos, targetState, 3)) {
            return false;
        }
        TileEntity newTile = world.getTileEntity(pos);
        if (newTile == null) {
            restoreTile(world, pos, oldState, originalSnapshot);
            return false;
        }
        try {
            newTile.readFromNBT(snapshot);
            if (newTile instanceof IExtraTierMachine) {
                ((IExtraTierMachine) newTile).setExtraTier(tier);
            }
            if (newTile instanceof TileEntityBasicBlock) {
                ((TileEntityBasicBlock) newTile).facing = facing;
            }
        } catch (RuntimeException exception) {
            MekanismExtras.logger.error("Failed to migrate a tile to {}", targetBlock.getRegistryName(), exception);
            restoreTile(world, pos, oldState, originalSnapshot);
            return false;
        }
        newTile.markDirty();
        world.notifyBlockUpdate(pos, targetState, targetState, 3);
        world.notifyNeighborsOfStateChange(pos, targetBlock, true);
        world.updateComparatorOutputLevel(pos, targetBlock);
        return true;
    }
    @Nullable
    private static Block getUpstreamAbsoluteBlock(@Nullable TileEntity tile) {
        if (tile instanceof IExtraTierMachine) {
            return null;
        }
        if (tile instanceof TileEntityEnergyCube && isUltimate(tile)) {
            return ExtraBlocks.getEnergyCubeBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityFluidTank && isUltimate(tile)) {
            return ExtraBlocks.getFluidTankBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityGasTank && isUltimate(tile)) {
            return ExtraBlocks.getChemicalTankBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityBin && isUltimate(tile)) {
            return ExtraBlocks.getBinBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityInductionCell && ((TileEntityInductionCell) tile).tier.getBaseTier() == BaseTier.ULTIMATE) {
            return ExtraBlocks.getInductionCellBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityInductionProvider && ((TileEntityInductionProvider) tile).tier.getBaseTier() == BaseTier.ULTIMATE) {
            return ExtraBlocks.getInductionProviderBlock(AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityFactory && isUltimate(tile)) {
            return ExtraBlocks.getFactoryBlock(((TileEntityFactory) tile).getRecipeType(), ExtraFactoryTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierChemicalDissolutionChamber && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("dissolution_chamber", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierNutritionalLiquifier && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("nutritional_liquifier", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierChemicalOxidizer && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("chemical_oxidizer", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierChemicalCrystallizer && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("crystallizer", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierOrganicFarm && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("organic_farm", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierIsotopicCentrifuge && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("isotopic_centrifuge", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierChemicalInfuser && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("chemical_infuser", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierChemicalWasher && isUltimate(tile)) {
            return ExtraBlocks.getMoreMachineBlock("chemical_washer", AdvancedTier.ABSOLUTE);
        }
        if (tile instanceof TileEntityTierRadioactiveWasteBarrel && isUltimate(tile)) {
            return ExtraBlocks.getWasteBarrelBlock(AdvancedTier.ABSOLUTE);
        }
        return null;
    }

    private static boolean isUltimate(TileEntity tile) {
        return tile instanceof IBaseTierProvider && ((IBaseTierProvider) tile).getBaseTier() == BaseTier.ULTIMATE;
    }

    private static void restoreTile(World world, BlockPos pos, IBlockState state, NBTTagCompound snapshot) {
        world.setBlockState(pos, state, 3);
        TileEntity restored = world.getTileEntity(pos);
        if (restored != null) {
            restored.readFromNBT(snapshot);
            restored.markDirty();
        }
        world.notifyBlockUpdate(pos, state, state, 3);
        world.notifyNeighborsOfStateChange(pos, state.getBlock(), true);
    }

    public static String getBlockLocalizationKey(TileEntity tile) {
        return tile.getBlockType().getTranslationKey() + ".name";
    }
    public static int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IComparatorSupport && ((IComparatorSupport) tile).supportsComparator()) {
            return ((IComparatorSupport) tile).getCurrentRedstoneLevel();
        }
        return 0;
    }

    public static EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos) {
        EnumFacing[] valid = new EnumFacing[6];
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            TileEntityBasicBlock basicTile = (TileEntityBasicBlock) tile;
            for (EnumFacing dir : EnumFacing.VALUES) {
                if (basicTile.canSetFacing(dir)) {
                    valid[dir.ordinal()] = dir;
                }
            }
        }
        return valid;
    }

    public static boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            TileEntityBasicBlock basicTile = (TileEntityBasicBlock) tile;
            if (basicTile.canSetFacing(axis)) {
                basicTile.setFacing(axis);
                return true;
            }
        }
        return false;
    }

    public static void setTierOnStack(ItemStack stack, AdvancedTier tier) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(TIER_NBT_KEY, tier.ordinal());
    }

    public static int getTierFromStack(ItemStack stack, int fallback) {
        if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(TIER_NBT_KEY)) {
            return fallback;
        }
        return stack.getTagCompound().getInteger(TIER_NBT_KEY);
    }

    public static boolean isExtraMachine(Block block) {
        return block instanceof BlockExtraMachine;
    }

    public static net.minecraft.util.text.TextFormatting getTierFormatting(AdvancedTier tier) {
        return net.minecraft.util.text.TextFormatting.WHITE;
    }

    public static String getTierName(AdvancedTier tier) {
        return net.minecraft.util.text.translation.I18n.translateToLocal("tier." + MekanismExtras.MODID + "." + tier.getLowerName());
    }

    public static IBaseTierProvider asTierProvider(TileEntity tile) {
        return tile instanceof IBaseTierProvider ? (IBaseTierProvider) tile : null;
    }
}

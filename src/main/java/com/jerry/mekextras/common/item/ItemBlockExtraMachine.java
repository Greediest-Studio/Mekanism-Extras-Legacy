package com.jerry.mekextras.common.item;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.block.BlockExtraBin;
import com.jerry.mekextras.common.block.BlockExtraChemicalTank;
import com.jerry.mekextras.common.block.BlockExtraEnergyCube;
import com.jerry.mekextras.common.block.BlockExtraFactory;
import com.jerry.mekextras.common.block.BlockExtraFluidTank;
import com.jerry.mekextras.common.block.BlockExtraMoreMachine;
import com.jerry.mekextras.common.block.BlockExtraInductionCell;
import com.jerry.mekextras.common.block.BlockExtraInductionProvider;
import com.jerry.mekextras.common.block.BlockExtraWasteBarrel;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;
import com.jerry.mekextras.common.util.ExtraUtils;
import mekanism.api.EnumColor;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.gas.GasStack;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismClient;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.integration.forgeenergy.ForgeEnergyItemWrapper;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.base.ISustainedTank;
import mekanism.common.base.ITierItem;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemBlockPlacementData;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.tier.BaseTier;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismPlacementData;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Item block of every machine added by Mekanism Extras Legacy. Stores the machine tier, inventory, energy, tank,
 * upgrades and owner so breaking and creative middle click retain the same data as Mekanism's own machine items.
 */
public class ItemBlockExtraMachine extends ItemBlock implements ISustainedInventory, ISustainedTank, ITierItem, IItemBlockPlacementData, ISecurityItem {

    private static final String FLUID_KEY = "fluidTank";

    public ItemBlockExtraMachine(Block block) {
        super(block);
        setMaxStackSize(64);
        setCreativeTab(MekanismExtras.tabExtra);
    }

    public static AdvancedTier getTier(ItemStack stack) {
        Block block = stack.getItem() instanceof ItemBlockExtraMachine ? ((ItemBlockExtraMachine) stack.getItem()).getBlock() : null;
        return AdvancedTier.byIndex(ExtraUtils.getTierFromStack(stack, getDefaultTier(block)));
    }

    public static int getDefaultTier(Block block) {
        if (block instanceof BlockExtraEnergyCube cube) {
            return cube.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof BlockExtraFluidTank tank) {
            return tank.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof BlockExtraChemicalTank tank) {
            return tank.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof BlockExtraWasteBarrel barrel) {
            return barrel.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof BlockExtraBin bin) {
            return bin.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof BlockExtraFactory factory) {
            return factory.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof com.jerry.mekextras.common.block.BlockExtraMoreMachine machine) {
            return machine.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof com.jerry.mekextras.common.block.BlockExtraInductionCell cell) {
            return cell.getTier().getAdvanceTier().ordinal();
        } else if (block instanceof com.jerry.mekextras.common.block.BlockExtraInductionProvider provider) {
            return provider.getTier().getAdvanceTier().ordinal();
        }
        return 0;
    }

    public static void setTier(ItemStack stack, AdvancedTier tier) {
        ExtraUtils.setTierOnStack(stack, tier);
    }

    public double getEnergyCapacity(ItemStack stack) {
        Block block = getBlock();
        if (block instanceof BlockExtraEnergyCube cube) {
            return cube.getTier().getMaxEnergy();
        }
        if (block instanceof com.jerry.mekextras.common.block.BlockExtraInductionCell cell) {
            return cell.getTier().getMaxEnergy();
        }
        return 0;
    }

    public double getEnergyTransfer(ItemStack stack) {
        return Math.min(Integer.MAX_VALUE, getEnergyCapacity(stack) * 0.005D);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (!(getBlock() instanceof BlockExtraEnergyCube)) {
            return super.initCapabilities(stack, nbt);
        }
        return new ItemCapabilityWrapper(stack, new ForgeEnergyItemWrapper(),
              RateLimitEnergyHandler.create(() -> getEnergyTransfer(stack), () -> getEnergyCapacity(stack),
                    BasicEnergyContainer.manualOnly, ConstantPredicates.alwaysTrue()));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getBlock() instanceof BlockExtraEnergyCube && stack.getCount() == 1 && StorageUtils.getStoredEnergy(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        double capacity = getEnergyCapacity(stack);
        return capacity <= 0 ? 1D : 1D - Math.min(1D, StorageUtils.getStoredEnergy(stack) / capacity);
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0F, (float) (1D - getDurabilityForDisplay(stack))) / 3F, 1F, 1F);
    }

    public int getFluidCapacity(ItemStack stack) {
        Block block = getBlock();
        return block instanceof BlockExtraFluidTank ? ((BlockExtraFluidTank) block).getTier().getStorage() : 0;
    }

    public int getGasCapacity(ItemStack stack) {
        Block block = getBlock();
        if (block instanceof BlockExtraChemicalTank tank) {
            return (int) Math.min(Integer.MAX_VALUE, tank.getTier().getStorage());
        }
        return 0;
    }

    @Nullable
    public GasStack getGasStack(ItemStack stack) {
        return ItemDataUtils.getStoredGas(stack, "gasStored");
    }

    public void setGasStack(ItemStack stack, @Nullable GasStack gasStack) {
        ItemDataUtils.setStoredGas(stack, "gasStored", gasStack, getGasCapacity(stack));
    }

    @Override
    public void restorePlacementData(@Nonnull ItemStack stack, @Nonnull EntityLivingBase placer, @Nonnull World world,
                                     @Nonnull BlockPos pos, @Nonnull TileEntity tileEntity) {
        if (tileEntity instanceof IExtraTierMachine) {
            ((IExtraTierMachine) tileEntity).setExtraTier(getTier(stack));
        }
        if (stack.getCount() > 1) {
            // Stacked machine items carry no contents. Only the owner is assigned to the placed block.
            if (tileEntity instanceof mekanism.common.security.ISecurityTile securityTile && placer != null) {
                securityTile.getSecurity().setOwnerUUID(placer.getUniqueID());
            }
            return;
        }
        if (tileEntity instanceof TileEntityExtraChemicalTank tank) {
            tank.gasTank.setGas(getGasStack(stack));
        }
        MekanismPlacementData.restoreCommon(stack, placer, tileEntity);
    }

    @Override
    public void setInventory(NBTTagList nbtTags, Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            ItemDataUtils.setList((ItemStack) data[0], "Items", nbtTags);
        }
    }

    @Override
    public NBTTagList getInventory(Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            return ItemDataUtils.getList((ItemStack) data[0], "Items");
        }
        return null;
    }

    @Override
    public void setFluidStack(FluidStack fluidStack, Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            ItemDataUtils.setStoredFluid((ItemStack) data[0], FLUID_KEY, fluidStack, Integer.MAX_VALUE);
        }
    }

    @Override
    public FluidStack getFluidStack(Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            return ItemDataUtils.getStoredFluid((ItemStack) data[0], FLUID_KEY);
        }
        return null;
    }

    @Override
    public boolean hasTank(Object... data) {
        if (data.length == 0 || !(data[0] instanceof ItemStack)) {
            return false;
        }
        Block block = getBlock();
        return block instanceof BlockExtraFluidTank || block instanceof BlockExtraChemicalTank;
    }

    @Override
    public BaseTier getBaseTier(ItemStack stack) {
        BaseTier[] values = BaseTier.values();
        return values[Math.min(getTier(stack).ordinal(), values.length - 1)];
    }

    @Override
    public void setBaseTier(ItemStack stack, BaseTier tier) {
        setTier(stack, AdvancedTier.byIndex(tier.ordinal()));
    }

    @Override
    public UUID getOwnerUUID(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, "ownerUUID")) {
            return null;
        }
        try {
            return UUID.fromString(ItemDataUtils.getString(stack, "ownerUUID"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void setOwnerUUID(ItemStack stack, UUID owner) {
        if (owner == null) {
            ItemDataUtils.removeData(stack, "ownerUUID");
        } else {
            ItemDataUtils.setString(stack, "ownerUUID", owner.toString());
        }
    }

    @Override
    public boolean hasOwner(ItemStack stack) {
        return getOwnerUUID(stack) != null;
    }

    @Override
    public SecurityMode getSecurity(ItemStack stack) {
        if (!MekanismConfig.current().general.allowProtection.val()) {
            return SecurityMode.PUBLIC;
        }
        int security = ItemDataUtils.getInt(stack, "security");
        SecurityMode[] values = SecurityMode.values();
        return security >= 0 && security < values.length ? values[security] : SecurityMode.PUBLIC;
    }

    @Override
    public void setSecurity(ItemStack stack, SecurityMode mode) {
        if (getOwnerUUID(stack) == null) {
            ItemDataUtils.removeData(stack, "security");
        } else {
            ItemDataUtils.setInt(stack, "security", mode.ordinal());
        }
    }

    @Override
    public boolean hasSecurity(ItemStack stack) {
        return !(getBlock() instanceof BlockExtraBin);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip,
                               @Nonnull ITooltipFlag flag) {
        Block block = getBlock();
        boolean fluidTank = block instanceof BlockExtraFluidTank;
        boolean chemicalTank = block instanceof BlockExtraChemicalTank;
        if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            if (getEnergyCapacity(stack) > 0) {
                if (stack.getCount() == 1) {
                    tooltip.add(EnumColor.BRIGHT_GREEN + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY +
                            MekanismUtils.getEnergyDisplay(StorageUtils.getStoredEnergyForDisplay(stack)));
                }
                tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                        MekanismUtils.getEnergyDisplay(getEnergyCapacity(stack)));
            }
            if (block instanceof com.jerry.mekextras.common.block.BlockExtraInductionProvider provider) {
                tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.outputRate") + ": " + EnumColor.GREY +
                        MekanismUtils.getEnergyDisplay(provider.getTier().getOutput()));
            }
            if (chemicalTank) {
                GasStack gas = stack.getCount() == 1 ? getGasStack(stack) : null;
                if (gas == null) {
                    tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty") + ".");
                } else {
                    tooltip.add(EnumColor.AQUA + LangUtils.localizeGasStack(gas) + ": " + EnumColor.GREY + gas.amount);
                }
                tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY + getGasCapacity(stack));
            }
            if (block instanceof BlockExtraWasteBarrel) {
                com.jerry.mekextras.common.tier.RWBTier tier = ((BlockExtraWasteBarrel) block).getTier();
                GasStack gas = ItemDataUtils.getLegacyGas(stack, "gasStored");
                if (gas == null) {
                    tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty") + ".");
                } else {
                    tooltip.add(EnumColor.AQUA + LangUtils.localizeGasStack(gas) + ": " + EnumColor.GREY + gas.amount);
                }
                tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                        (tier.getStorage() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : tier.getStorage()));
            }
            if (fluidTank) {
                FluidStack fluid = stack.getCount() == 1 ? getFluidStack(stack) : null;
                if (fluid == null) {
                    tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty") + ".");
                } else {
                    String amount = fluid.amount == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : fluid.amount + "mB";
                    tooltip.add(EnumColor.AQUA + LangUtils.localizeFluidStack(fluid) + ": " + EnumColor.GREY + amount);
                }
                int cap = getFluidCapacity(stack);
                tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
                        (cap == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : cap + " mB"));
            }
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO +
                    GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " +
                    LangUtils.localize("tooltip.forDetails") + ".");
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA +
                    GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " +
                    LangUtils.localize("tooltip.and") + " " + EnumColor.AQUA +
                    GameSettings.getKeyDisplayString(MekanismKeyHandler.handModeSwitchKey.getKeyCode()) + EnumColor.GREY + " " +
                    LangUtils.localize("tooltip.forDesc") + ".");
        } else if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.handModeSwitchKey)) {
            if (hasSecurity(stack)) {
                tooltip.add(SecurityUtils.getOwnerDisplay(Minecraft.getMinecraft().player,
                        MekanismClient.clientUUIDMap.get(getOwnerUUID(stack))));
                tooltip.add(EnumColor.GREY + LangUtils.localize("gui.security") + ": " +
                        SecurityUtils.getSecurityDisplay(stack, Side.CLIENT));
                if (SecurityUtils.isOverridden(stack, Side.CLIENT)) {
                    tooltip.add(EnumColor.RED + "(" + LangUtils.localize("gui.overridden") + ")");
                }
            }
            if (block instanceof BlockExtraWasteBarrel) {
                GasStack gas = ItemDataUtils.getLegacyGas(stack, "gasStored");
                if (gas != null) {
                    tooltip.add(EnumColor.PINK + LangUtils.localizeGasStack(gas) + ": " + EnumColor.GREY + gas.amount);
                }
            }
            if (getEnergyCapacity(stack) > 0 && stack.getCount() == 1) {
                tooltip.add(EnumColor.BRIGHT_GREEN + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY +
                        MekanismUtils.getEnergyDisplay(StorageUtils.getStoredEnergyForDisplay(stack)));
            }
            if (chemicalTank) {
                GasStack gas = stack.getCount() == 1 ? getGasStack(stack) : null;
                if (gas != null) {
                    tooltip.add(EnumColor.PINK + LangUtils.localizeGasStack(gas) + ": " + EnumColor.GREY + gas.amount);
                }
            }
            FluidStack fluid = stack.getCount() == 1 ? getFluidStack(stack) : null;
            if (fluid != null && !fluidTank) {
                tooltip.add(EnumColor.PINK + LangUtils.localizeFluidStack(fluid) + ": " + EnumColor.GREY + fluid.amount + "mB");
            }
            if (!(block instanceof BlockExtraWasteBarrel)) {
                NBTTagList inventory = getInventory(stack);
                tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.inventory") + ": " + EnumColor.GREY +
                        LangUtils.transYesNo(inventory != null && inventory.tagCount() != 0));
            }
            if (Upgrade.hasUpgradeData(ItemDataUtils.getDataMapIfPresent(stack))) {
                Upgrade.buildComponentMap(ItemDataUtils.getDataMapIfPresent(stack)).forEach((upgrade, amount) ->
                        tooltip.add(upgrade.getColor() + "- " + upgrade.getName() +
                                (upgrade.canMultiply() ? ": " + EnumColor.GREY + "x" + amount : "")));
            }
        } else {
            tooltip.addAll(MekanismUtils.splitTooltip(getDescriptionText(block), stack));
        }
    }

    private String getDescriptionText(Block block) {
        if (block instanceof BlockExtraFactory factory) {
            return factory.getRecipeType().getType().getDescription();
        }
        String path = getRegistryName() == null ? "" : getRegistryName().getPath();
        if (block instanceof BlockExtraMoreMachine) {
            String machineKey = moreMachineName(baseName(path));
            if (machineKey != null) {
                return LangUtils.localize("tooltip." + machineKey);
            }
        }
        String key = "description." + MekanismExtras.MODID + "." + descriptionBase(block, path);
        if (!net.minecraft.util.text.translation.I18n.canTranslate(key)) {
            key = "description." + MekanismExtras.MODID + ".machine";
        }
        return LangUtils.localize(key);
    }

    private static String descriptionBase(Block block, String path) {
        if (block instanceof BlockExtraEnergyCube) return "energy_cube";
        if (block instanceof BlockExtraFluidTank) return "fluid_tank";
        if (block instanceof BlockExtraChemicalTank) return "chemical_tank";
        if (block instanceof BlockExtraBin) return "bin";
        if (block instanceof BlockExtraWasteBarrel) return "radioactive_waste_barrel";
        if (block instanceof BlockExtraInductionCell) return "reinforced_induction_cell";
        if (block instanceof BlockExtraInductionProvider) return "reinforced_induction_provider";
        return baseName(path);
    }

    private static String baseName(String path) {
        for (String prefix : new String[]{"absolute_", "supreme_", "cosmic_", "infinite_"}) {
            if (path.startsWith(prefix)) return path.substring(prefix.length());
        }
        return path;
    }

    private static String moreMachineName(String baseName) {
        return switch (baseName) {
            case "chemical_infuser" -> "TierChemicalInfuser";
            case "chemical_oxidizer" -> "TierChemicalOxidizer";
            case "chemical_washer" -> "TierChemicalWasher";
            case "crystallizer" -> "TierChemicalCrystallizer";
            case "dissolution_chamber" -> "TierChemicalDissolutionChamber";
            case "isotopic_centrifuge" -> "TierIsotopicCentrifuge";
            case "nutritional_liquifier" -> "TierNutritionalLiquifier";
            case "organic_farm" -> "TierOrganicFarm";
            default -> null;
        };
    }
}

package com.jerry.mekextras.common.item;

import com.jerry.mekextras.common.registries.ExtraFluids;
import mekanism.api.EnumColor;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.gas.GasStack;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.gas.item.RateLimitGasHandler;
import mekanism.common.inventory.slot.gas.GasInventorySlot;
import mekanism.common.item.ItemMekanism;
import mekanism.common.item.interfaces.ILegacyGasItem;
import mekanism.common.util.LangUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/** A custom Hohlraum that accepts any naquadah reactor fuel. */
public class ItemNaquadahHohlraum extends ItemMekanism implements ILegacyGasItem {

    public static final int CAPACITY = 100;
    public static final int TRANSFER_RATE = 1;
    private static final String GAS_KEY = "stored";

    public ItemNaquadahHohlraum() {
        setMaxStackSize(1);
    }

    @Nullable
    public GasStack getContainedGas(ItemStack stack) {
        GasStack stored = GasInventorySlot.getStoredGas(stack, GAS_KEY);
        return stored != null && ExtraFluids.isHohlraumFuel(stored.getGas()) ? stored.copy() : null;
    }

    public boolean isReadyForReaction(ItemStack stack) {
        GasStack stored = getContainedGas(stack);
        int capacity = GasInventorySlot.getTankCapacity(stack, 0);
        return stored != null && stored.amount == (capacity > 0 ? capacity : CAPACITY);
    }

    public void setStoredGas(ItemStack stack, @Nullable GasStack gas) {
        GasInventorySlot.setStoredGas(stack, gas, GAS_KEY, CAPACITY);
    }

    public ItemStack getEmptyItem() {
        ItemStack stack = new ItemStack(this);
        setStoredGas(stack, null);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        GasStack gas = getContainedGas(stack);
        if (gas == null) {
            tooltip.add(LangUtils.localize("tooltip.noGas") + ".");
            tooltip.add(EnumColor.DARK_RED + LangUtils.localize("tooltip.insufficientFuel"));
            return;
        }
        tooltip.add(LangUtils.localize("tooltip.stored") + " " + gas.getGas().getLocalizedName() + ": " + gas.amount + "/" + CAPACITY);
        tooltip.add(isReadyForReaction(stack) ? EnumColor.DARK_GREEN + LangUtils.localize("tooltip.readyForReaction") + "!" :
              EnumColor.DARK_RED + LangUtils.localize("tooltip.insufficientFuel"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        GasStack gas = getContainedGas(stack);
        return gas != null && gas.amount > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        GasStack gas = getContainedGas(stack);
        return gas == null ? 1 : 1D - gas.amount / (double) CAPACITY;
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        GasStack gas = getContainedGas(stack);
        return gas == null ? MathHelper.hsvToRGB(0, 0, 1) : gas.getGas().getTint();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, RateLimitGasHandler.create(() -> TRANSFER_RATE, () -> CAPACITY,
              ConstantPredicates.alwaysFalseBi(), ConstantPredicates.alwaysTrueBi(),
              gas -> gas != null && ExtraFluids.isHohlraumFuel(gas.getGas()), GAS_KEY));
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(getEmptyItem());
        ItemStack filled = new ItemStack(this);
        setStoredGas(filled, new GasStack(ExtraFluids.NAQUADAH_URANIUM_FUEL, CAPACITY));
        items.add(filled);
    }
}

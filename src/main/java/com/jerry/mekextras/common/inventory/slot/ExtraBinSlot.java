package com.jerry.mekextras.common.inventory.slot;

import com.jerry.mekextras.common.block.BlockExtraBin;
import com.jerry.mekextras.common.tier.BTier;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.common.block.states.BlockStateBasic.BasicBlockType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Inventory slot used by the extra bins. Its stack limit, locking and serialization behavior mirror Mekanism's
 * {@code BinInventorySlot}; only the capacity provider is different.
 */
public class ExtraBinSlot extends BasicInventorySlot {

    private static final java.util.function.BiPredicate<ItemStack, AutomationType> ALWAYS = (stack, automationType) -> true;

    private final Supplier<BTier> tierSupplier;
    private ItemStack lockStack = ItemStack.EMPTY;

    private ExtraBinSlot(@Nullable IContentsListener listener, Supplier<BTier> tierSupplier) {
        super(Integer.MAX_VALUE, ALWAYS, ALWAYS,
              stack -> BasicBlockType.get(stack) != BasicBlockType.BIN && !(net.minecraft.block.Block.getBlockFromItem(stack.getItem()) instanceof BlockExtraBin),
              listener, 0, 0);
        this.tierSupplier = tierSupplier;
        obeyStackLimit = false;
    }

    public static ExtraBinSlot create(@Nullable IContentsListener listener, Supplier<BTier> tierSupplier) {
        return new ExtraBinSlot(listener, tierSupplier);
    }

    @Override
    public ItemStack insertItem(ItemStack stack, Action action, AutomationType automationType) {
        if (isEmpty() && isLocked() && !ItemHandlerHelper.canItemStacksStack(lockStack, stack)) {
            return stack;
        }
        return super.insertItem(stack, action, automationType);
    }

    @Override
    public int getLimit(ItemStack stack) {
        return tierSupplier.get().getStorage();
    }

    @Nullable
    @Override
    public InventoryContainerSlot createContainerSlot() {
        return null;
    }

    public boolean setLocked(boolean lock) {
        if (isLocked() == lock || (lock && isEmpty())) {
            return false;
        }
        lockStack = lock ? StackUtils.size(current, 1) : ItemStack.EMPTY;
        return true;
    }

    public void setLockStack(ItemStack stack) {
        lockStack = stack.isEmpty() ? ItemStack.EMPTY : StackUtils.size(stack, 1);
    }

    public boolean isLocked() {
        return !lockStack.isEmpty();
    }

    public ItemStack getRenderStack() {
        return isLocked() ? getLockStack() : getStack();
    }

    public ItemStack getLockStack() {
        return lockStack;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        if (isLocked()) {
            nbt.setTag(NBTConstants.LOCK_STACK, lockStack.writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(NBTConstants.LOCK_STACK, NBT.TAG_COMPOUND)) {
            lockStack = new ItemStack(nbt.getCompoundTag(NBTConstants.LOCK_STACK));
        } else {
            lockStack = ItemStack.EMPTY;
        }
        super.deserializeNBT(nbt);
    }
}
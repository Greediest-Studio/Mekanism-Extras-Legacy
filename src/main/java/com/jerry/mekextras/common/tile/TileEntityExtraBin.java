package com.jerry.mekextras.common.tile;

import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.inventory.slot.ExtraBinSlot;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.BTier;
import io.netty.buffer.ByteBuf;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.Coord4D;
import mekanism.api.IConfigurable;
import mekanism.api.IContentsListener;
import mekanism.api.TileNetworkList;
import mekanism.common.PacketHandler;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IBaseTierProvider;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.inventory.TransitRequest.TransitResponse;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.base.ILogisticalTransporter;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StackUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

/**
 * Tiered storage bin matching Mekanism CE Unofficial's bin behavior, including locking, quick deposit,
 * auto-ejection, redstone output and the configurator capability.
 */
public class TileEntityExtraBin extends TileEntityContainerBlock implements IActiveState, IComparatorSupport, IBaseTierProvider,
      IExtraTierMachine, IConfigurable {

    public static final int MAX_DELAY = 10;

    public boolean isActive;
    public boolean clientActive;
    public int addTicks;
    public int delayTicks;
    public int clientAmount;
    public int cacheCount;
    public ItemStack itemType = ItemStack.EMPTY;
    public ItemStack topStack = ItemStack.EMPTY;
    public ItemStack bottomStack = ItemStack.EMPTY;
    public ItemStack clientLockStack = ItemStack.EMPTY;

    private BTier tier;
    private ExtraBinSlot binSlot;
    private int prevCount;

    public TileEntityExtraBin() {
        this(BTier.ABSOLUTE);
    }

    public TileEntityExtraBin(BTier tier) {
        super("ExtraBin");
        this.tier = tier;
        initializeInventorySlots();
    }

    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = createInventorySlotHelper();
        binSlot = builder.addSlot(ExtraBinSlot.create(this::onContentsChanged, () -> tier));
        return builder.build();
    }

    public BTier getTier() {
        return tier;
    }

    public ExtraBinSlot getBinSlot() {
        return binSlot;
    }

    public ItemStack getRenderStack() {
        return binSlot == null ? ItemStack.EMPTY : binSlot.getRenderStack();
    }

    public int getItemCount() {
        return binSlot == null ? 0 : binSlot.getCount();
    }

    public void setItemCount(int count) {
        if (binSlot == null) {
            return;
        }
        if (count <= 0) {
            binSlot.setEmpty();
        } else if (!itemType.isEmpty()) {
            binSlot.setStackUnchecked(StackUtils.size(itemType, Math.min(count, tier.getStorage())));
        }
        sortStacks();
        markNoUpdateSync();
    }

    public boolean isValid(ItemStack stack) {
        return binSlot != null && !stack.isEmpty() && stack.getCount() > 0 && binSlot.isItemValid(stack) &&
              binSlot.insertItem(stack, Action.SIMULATE, AutomationType.MANUAL).getCount() < stack.getCount();
    }

    public ItemStack add(ItemStack stack, boolean simulate) {
        if (binSlot == null) {
            return stack;
        }
        ItemStack remainder = binSlot.insertItem(stack, Action.get(!simulate), AutomationType.MANUAL);
        if (!simulate && remainder.getCount() != stack.getCount()) {
            sortStacks();
            markNoUpdateSync();
        }
        return remainder;
    }

    public ItemStack add(ItemStack stack) {
        return add(stack, false);
    }

    public ItemStack remove(int amount, boolean simulate) {
        if (binSlot == null) {
            return ItemStack.EMPTY;
        }
        ItemStack ret = binSlot.extractItem(amount, Action.get(!simulate), AutomationType.MANUAL);
        if (!simulate && !ret.isEmpty()) {
            sortStacks();
            markNoUpdateSync();
        }
        return ret;
    }

    public ItemStack remove(int amount) {
        return remove(amount, false);
    }

    public ItemStack removeStack() {
        return bottomStack.isEmpty() ? ItemStack.EMPTY : remove(bottomStack.getCount(), false);
    }

    private void sortStacks() {
        if (binSlot == null) {
            return;
        }
        ItemStack stored = binSlot.getStack();
        if (stored.isEmpty() || binSlot.getCount() <= 0) {
            itemType = ItemStack.EMPTY;
            topStack = ItemStack.EMPTY;
            bottomStack = ItemStack.EMPTY;
            cacheCount = 0;
            return;
        }
        itemType = StackUtils.size(stored, 1);
        int count = binSlot.getCount();
        int remaining = tier.getStorage() - count;
        topStack = remaining >= itemType.getMaxStackSize() ? ItemStack.EMPTY :
              StackUtils.size(itemType, itemType.getMaxStackSize() - remaining);
        count -= topStack.getCount();
        bottomStack = StackUtils.size(itemType, Math.min(itemType.getMaxStackSize(), count));
        count -= bottomStack.getCount();
        cacheCount = count;
    }

    @Override
    public void onAsyncUpdateServer() {
        super.onAsyncUpdateServer();
        addTicks = Math.max(0, addTicks - 1);
        sortStacks();
        if (getItemCount() != prevCount) {
            markNoUpdateSync();
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        delayTicks = Math.max(0, delayTicks - 1);
        if (delayTicks == 0 && !bottomStack.isEmpty() && isActive) {
            TileEntity below = Coord4D.get(this).offset(EnumFacing.DOWN).getTileEntity(world);
            ILogisticalTransporter transporter = CapabilityUtils.getCapability(below,
                  Capabilities.LOGISTICAL_TRANSPORTER_CAPABILITY, EnumFacing.UP);
            TransitResponse response = transporter == null ?
                  InventoryUtils.putStackInInventory(below, TransitRequest.getFromStack(bottomStack), EnumFacing.DOWN, false) :
                  TransporterUtils.insert(this, transporter, TransitRequest.getFromStack(bottomStack), null, true, 0);
            if (!response.isEmpty()) {
                binSlot.shrinkStack(response.getSendingAmount(), Action.EXECUTE);
                sortStacks();
            }
            delayTicks = MAX_DELAY;
        }
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        clientActive = isActive;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setBoolean("isActive", isActive);
        nbtTags.setInteger("tier", tier.ordinal());
    }

    @Override
    protected void readCustomNBTBeforeInventory(NBTTagCompound nbtTags) {
        clientActive = isActive = nbtTags.getBoolean("isActive");
        tier = BTier.byIndex(nbtTags.getInteger("tier"));
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        sortStacks();
        prevCount = getItemCount();
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(getItemCount());
        data.add(tier.ordinal());
        if (getItemCount() > 0) {
            data.add(itemType);
        }
        data.add(binSlot.getLockStack());
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            clientActive = isActive = dataStream.readBoolean();
            clientAmount = dataStream.readInt();
            tier = BTier.byIndex(dataStream.readInt());
            if (clientAmount > 0) {
                itemType = PacketHandler.readStack(dataStream);
                binSlot.setStackUnchecked(StackUtils.size(itemType, Math.min(clientAmount, tier.getStorage())));
            } else {
                itemType = ItemStack.EMPTY;
                binSlot.setEmpty();
            }
            clientLockStack = PacketHandler.readStack(dataStream);
            binSlot.setLockStack(clientLockStack);
            sortStacks();
            MekanismUtils.updateBlock(world, getPos());
        }
    }

    @Override
    protected void onMarkDirty() {
        if (!isRemote()) {
            mekanism.common.Mekanism.packetHandler.sendUpdatePacket(this);
            prevCount = getItemCount();
            sortStacks();
        }
    }

    public void setItemType(ItemStack stack) {
        if (stack.isEmpty()) {
            itemType = ItemStack.EMPTY;
            binSlot.setEmpty();
            sortStacks();
            return;
        }
        itemType = StackUtils.size(stack, 1);
        if (!binSlot.isEmpty()) {
            binSlot.setStackUnchecked(StackUtils.size(itemType, Math.min(getItemCount(), tier.getStorage())));
            sortStacks();
        }
    }

    public boolean toggleLock() {
        return setLocked(!binSlot.isLocked());
    }

    public boolean setLocked(boolean locked) {
        if (binSlot.setLocked(locked)) {
            clientLockStack = binSlot.getLockStack();
            if (!isRemote()) {
                mekanism.common.Mekanism.packetHandler.sendUpdatePacket(this);
                markNoUpdateSync();
                world.playSound(null, getPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 1);
            }
            return true;
        }
        return false;
    }

    public ItemStack getLockStack() {
        return binSlot.getLockStack();
    }

    public void onContentsChanged() {
        sortStacks();
        clientLockStack = binSlot.getLockStack();
        if (world != null && !world.isRemote) {
            markDirty();
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return getBlockType().getLocalizedName();
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.DOWN && facing != EnumFacing.UP;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
        if (clientActive != active) {
            mekanism.common.Mekanism.packetHandler.sendUpdatePacket(this);
            clientActive = active;
        }
    }

    @Override
    public boolean renderUpdate() {
        return true;
    }

    @Override
    public boolean lightUpdate() {
        return true;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(getItemCount(), getMaxStoredCount());
    }

    public int getMaxStoredCount() {
        return tier.getStorage();
    }

    @Override
    public BaseTier getBaseTier() {
        return tier.getBaseTier();
    }

    @Override
    public AdvancedTier getExtraTier() {
        return tier.getAdvanceTier();
    }

    @Override
    public void setExtraTier(AdvancedTier newTier) {
        tier = BTier.byIndex(newTier.ordinal());
    }

    @Override
    public Block getBlockForTier(AdvancedTier advancedTier) {
        return ExtraBlocks.getBinBlock(advancedTier);
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        setActive(!getActive());
        if (!isRemote()) {
            world.playSound(null, getPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onRightClick(EntityPlayer player, EnumFacing side) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        return capability == Capabilities.CONFIGURABLE_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (capability == Capabilities.CONFIGURABLE_CAPABILITY) {
            return Capabilities.CONFIGURABLE_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }
}
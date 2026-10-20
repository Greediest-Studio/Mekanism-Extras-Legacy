package com.jerry.mekextras.common.tile;

import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.RWBTier;
import io.netty.buffer.ByteBuf;
import mekanism.api.*;
import mekanism.api.gas.GasStack;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.ISustainedData;
import mekanism.common.base.ITankManager;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.capabilities.gas.BasicGasTank;
import mekanism.common.capabilities.holder.gas.GasTankHelper;
import mekanism.common.capabilities.holder.gas.IGasTankHolder;
import mekanism.common.lib.radiation.RadiationUtil;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.util.GasUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.TileUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * The large capacity radioactive waste barrels added by Mekanism Extras Legacy. Mirrors Mekanism's own waste barrel but
 * storage, decay amount and decay rate come from our own tier.
 */
public class TileEntityLargeCapRadioactiveWasteBarrel extends TileEntityContainerBlock implements ISustainedData, ITankManager,
      IComparatorSupport, IActiveState, IExtraTierMachine, IConfigurable, ISecurityTile {

    private static final ThreadLocal<RWBTier> PENDING_TIER = new ThreadLocal<>();

    /**
     * Creates a barrel of the given tier, safe against the fact that the base constructor already builds the gas tank holder.
     */
    public static TileEntityLargeCapRadioactiveWasteBarrel create(RWBTier tier) {
        PENDING_TIER.set(tier);
        try {
            return new TileEntityLargeCapRadioactiveWasteBarrel();
        } finally {
            PENDING_TIER.remove();
        }
    }

    private RWBTier tier = RWBTier.ABSOLUTE;
    private long lastProcessTick;
    public BasicGasTank gasTank;
    private int processTicks;
    public boolean isActive;
    public boolean clientActive;
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);

    public TileEntityLargeCapRadioactiveWasteBarrel() {
        super("radioactive_waste_barrel");
        RWBTier pending = PENDING_TIER.get();
        if (pending != null) {
            tier = pending;
        }
        initializeInventorySlots();
    }

    public RWBTier getTier() {
        return tier;
    }

    @Override
    protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        GasTankHelper builder = createGasTankHelper();
        gasTank = BasicGasTank.input((int) Math.min(Integer.MAX_VALUE, tier.getStorage()), gas -> gas != null && gas.isRadiation(), listener);
        builder.addTank(gasTank);
        return builder.build();
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        if (getWorld().getTotalWorldTime() > lastProcessTick) {
            lastProcessTick = getWorld().getTotalWorldTime();
            GasStack stored = gasTank.getGas();
            long decayAmount = tier.getDecayAmount();
            int processTicksRequired = Math.max(1, tier.getProcessTicks());
            if (decayAmount > 0 && RadiationUtil.canDecayInWasteBarrel(stored) && ++processTicks >= processTicksRequired) {
                processTicks = 0;
                gasTank.extract((int) Math.min(Integer.MAX_VALUE, decayAmount), Action.EXECUTE, AutomationType.INTERNAL);
            }
            if (getActive()) {
                GasUtils.emit(Collections.singleton(EnumFacing.DOWN), gasTank, this, getDownOutputLimit());
            }
        }
    }

    private int getDownOutputLimit() {
        TileEntity below = MekanismUtils.getTileEntity(world, pos.down());
        if (below instanceof TileEntityLargeCapRadioactiveWasteBarrel) {
            TileEntityLargeCapRadioactiveWasteBarrel barrel = (TileEntityLargeCapRadioactiveWasteBarrel) below;
            return Math.min(barrel.gasTank.getNeeded(), gasTank.getCapacity());
        }
        return gasTank.getCapacity();
    }

    @Override
    public void writeSustainedData(ItemStack itemStack) {
        writeSustainedGasTanks(itemStack);
        ItemDataUtils.setLegacyGas(itemStack, "gasStored", gasTank.getGas());
    }

    @Override
    public void readSustainedData(ItemStack itemStack) {
        if (!readSustainedGasTanks(itemStack)) {
            gasTank.setStackUnchecked(ItemDataUtils.getLegacyGas(itemStack, "gasStored"));
        }
        sanitizeAndClampGasTank();
    }

    private void sanitizeAndClampGasTank() {
        GasStack stored = gasTank.getGas();
        if (stored != null && (stored.amount <= 0 || stored.getGas() == null || !stored.getGas().isRadiation())) {
            gasTank.setEmpty();
        } else if (stored != null) {
            gasTank.setStackSize(stored.amount, Action.EXECUTE);
        }
    }

    @Override
    public Object[] getManagedTanks() {
        return new Object[]{gasTank};
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        if (!isRemote()) {
            setActive(!getActive());
            world.playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onRightClick(EntityPlayer player, EnumFacing side) {
        return EnumActionResult.PASS;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            clientActive = isActive = dataStream.readBoolean();
            TileUtils.readTankData(dataStream, gasTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(isActive);
        TileUtils.addTankData(data, gasTank);
        return data;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        RWBTier loaded = nbtTags.hasKey("tier") ? RWBTier.byIndex(nbtTags.getInteger("tier")) : tier;
        if (loaded != tier) {
            tier = loaded;
            //Rebuild the tank holder so that the stored tier's capacity is used.
            initializeContainerHolders(true);
        }
        super.readCustomNBT(nbtTags);
        securityComponent.read(nbtTags);
        clientActive = isActive = nbtTags.getBoolean("isActive");
        processTicks = Math.max(0, nbtTags.getInteger(NBTConstants.PROGRESS));
        if (!hasStoredGasTanks(nbtTags) && nbtTags.hasKey("gasTank")) {
            gasTank.read(nbtTags.getCompoundTag("gasTank"));
        }
        sanitizeAndClampGasTank();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        securityComponent.write(nbtTags);
        nbtTags.setBoolean("isActive", isActive);
        nbtTags.setInteger(NBTConstants.PROGRESS, processTicks);
        nbtTags.setInteger("tier", tier.ordinal());
    }

    @Override
    public boolean supportsAsync() {
        return false;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
        if (clientActive != active) {
            Mekanism.packetHandler.sendUpdatePacket(this);
            clientActive = active;
        }
    }

    @Override
    public boolean renderUpdate() {
        return false;
    }

    @Override
    public boolean lightUpdate() {
        return false;
    }

    @Override
    public boolean isComparatorInput(mekanism.api.transmitters.TransmissionType type) {
        return type == mekanism.api.transmitters.TransmissionType.GAS;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(gasTank.getStored(), gasTank.getMaxGas());
    }

    @Override
    protected boolean shouldDumpRadiation() {
        return true;
    }

    @Override
    public AdvancedTier getExtraTier() {
        return tier.getAdvanceTier();
    }

    @Override
    public void setExtraTier(AdvancedTier newTier) {
        //The tier is defined by the block, the machine is replaced when it is upgraded.
    }

    @Override
    public Block getBlockForTier(AdvancedTier newTier) {
        return ExtraBlocks.getWasteBarrelBlock(newTier);
    }
    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }
}
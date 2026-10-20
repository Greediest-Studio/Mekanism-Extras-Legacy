package com.jerry.mekextras.common.tile.multiblock;

import io.netty.buffer.ByteBuf;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.gas.GasStack;
import mekanism.api.Coord4D;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.EnumColor;
import mekanism.api.IConfigurable;
import mekanism.api.IContentsListener;
import mekanism.api.TileNetworkList;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.gas.IExtendedGasTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.energy.ProxiedEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.fluid.ProxiedFluidTankHolder;
import mekanism.common.capabilities.holder.gas.IGasTankHolder;
import mekanism.common.capabilities.holder.gas.ProxiedGasTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.heat.ProxiedHeatCapacitorHolder;
import mekanism.common.content.network.distribution.GasHandlerTarget;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.GasUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A reactor port that proxies the shared multiblock contents. Ports do not hold independent
 * fuel, water, steam, energy, or heat caches, so every port always observes the same state.
 */
public class TileEntityExtraReactorPort extends TileEntityExtraReactorCasing implements IConfigurable, IActiveState {

    private static final String OUTPUT_MODE_NBT = "reactorPortOutputMode";
    private static final Set<EnumFacing> OUTPUT_SIDES = Collections.unmodifiableSet(EnumSet.allOf(EnumFacing.class));
    private final GasHandlerTarget gasOutputTarget = new GasHandlerTarget(null, EnumFacing.VALUES.length);
    private boolean outputMode;
    private boolean lastFormed;
    private boolean lastOutputMode;

    public TileEntityExtraReactorPort() {
        super();
        initializeContainerHolders();
    }

    private boolean isStructureFormed() {
        return structure != null && structure.isFormed();
    }

    public boolean isOutputMode() {
        return outputMode;
    }

    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return ProxiedEnergyContainerHolder.create(
              side -> isStructureFormed(),
              side -> isStructureFormed(),
              side -> isStructureFormed() ? structure.getEnergyContainers(side) : Collections.emptyList()
        );
    }

    @Override
    protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        return ProxiedGasTankHolder.create(
              side -> !outputMode && isStructureFormed(),
              side -> outputMode && isStructureFormed(),
              this::getGasTanks,
              this::getInputGasTanks,
              side -> isStructureFormed() ? Collections.singletonList(structure.steamTank) : Collections.emptyList()
        );
    }

    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return ProxiedFluidTankHolder.create(
              side -> !outputMode && isStructureFormed(),
              side -> false,
              this::getFluidTanks,
              side -> isStructureFormed() ? Collections.singletonList(structure.waterTank) : Collections.emptyList(),
              side -> Collections.emptyList()
        );
    }

    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener) {
        return ProxiedHeatCapacitorHolder.create(
              side -> isStructureFormed(),
              side -> isStructureFormed(),
              this::getHeatCapacitors
        );
    }

    @Nonnull
    @Override
    public List<IExtendedGasTank> getGasTanks(EnumFacing side) {
        if (!isStructureFormed()) {
            return Collections.emptyList();
        }
        return outputMode ? Collections.singletonList(structure.steamTank) : getInputGasTanks(side);
    }

    @Nonnull
    private List<IExtendedGasTank> getInputGasTanks(EnumFacing side) {
        if (!isStructureFormed()) {
            return Collections.emptyList();
        }
        return Arrays.asList(structure.richNaquadahTank, structure.fuelTank, structure.richUraniumTank);
    }

    @Nonnull
    @Override
    public List<IExtendedFluidTank> getFluidTanks(EnumFacing side) {
        return isStructureFormed() ? structure.getFluidTanks(side) : Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<IHeatCapacitor> getHeatCapacitors(EnumFacing side) {
        return isStructureFormed() ? structure.getHeatCapacitors(side) : Collections.emptyList();
    }

    @Override
    protected boolean persistGasTanks() {
        return false;
    }

    @Override
    protected boolean persistFluidTanks() {
        return false;
    }

    @Override
    protected boolean persistHeatCapacitors() {
        return false;
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        boolean formed = isStructureFormed();
        if (lastFormed != formed || lastOutputMode != outputMode) {
            lastFormed = formed;
            lastOutputMode = outputMode;
            refreshPortCapabilities();
        }
        if (!outputMode || !formed) {
            return;
        }
        // Route through Mekanism's energy container capability. CableUtils.emit reflects Forge
        // wrappers against the exact receiver class, which fails for this addon's cable subclass.
        emitStoredEnergy();
        GasStack steam = structure.steamTank.getGas();
        if (steam != null && steam.amount > 0) {
            GasStack toSend = steam.copy();
            int sent = GasUtils.emit(toSend, this, OUTPUT_SIDES, gasOutputTarget);
            if (sent > 0) {
                structure.steamTank.extract(sent, Action.EXECUTE, AutomationType.INTERNAL);
            }
        }
        simulate();
        syncCachedDataFromStructure();
    }

    private void emitStoredEnergy() {
        double remaining = structure.getEnergy();
        if (remaining <= 0) {
            return;
        }
        for (EnumFacing side : OUTPUT_SIDES) {
            IStrictEnergyStorage target = CapabilityUtils.getCapability(
                  new Coord4D(getPos(), world).offset(side).getTileEntity(world),
                  Capabilities.ENERGY_STORAGE_CAPABILITY, side.getOpposite());
            if (target == null) {
                continue;
            }
            double accepted = Math.min(remaining, target.getMaxEnergy() - target.getEnergy());
            if (accepted > 0) {
                target.setEnergy(target.getEnergy() + accepted);
                structure.setEnergy(structure.getEnergy() - accepted);
                remaining = structure.getEnergy();
                if (remaining <= 0) {
                    return;
                }
            }
        }
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        if (!isRemote()) {
            outputMode = !outputMode;
            String modeText = " " + (outputMode ? EnumColor.DARK_RED : EnumColor.DARK_GREEN) + LangUtils.transOutputInput(outputMode) + ".";
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.GREY +
                  LangUtils.localize("tooltip.configurator.reactorPortEject") + modeText));
            refreshPortCapabilities();
            Mekanism.packetHandler.sendUpdatePacket(this);
            markNoUpdateSync();
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onRightClick(EntityPlayer player, EnumFacing side) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean getActive() {
        return outputMode;
    }

    @Override
    public void setActive(boolean active) {
        outputMode = active;
    }

    @Override
    public boolean renderUpdate() {
        return true;
    }

    @Override
    public boolean lightUpdate() {
        return false;
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(outputMode);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            outputMode = dataStream.readBoolean();
            refreshPortCapabilities();
        }
    }

    private void refreshPortCapabilities() {
        clearCapabilityCache();
        if (world != null) {
            world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
            MekanismUtils.updateBlock(world, getPos());
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        outputMode = nbtTags.getBoolean(OUTPUT_MODE_NBT);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setBoolean(OUTPUT_MODE_NBT, outputMode);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        return capability == Capabilities.CONFIGURABLE_CAPABILITY || super.hasCapability(capability, side);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == Capabilities.CONFIGURABLE_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, side);
    }

    public boolean isActive() {
        return structure != null && structure.isBurning();
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.UP && facing != EnumFacing.DOWN;
    }
}
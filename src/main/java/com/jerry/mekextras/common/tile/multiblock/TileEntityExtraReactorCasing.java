package com.jerry.mekextras.common.tile.multiblock;

import com.jerry.mekextras.common.ExtraMultiblockManagers;
import com.jerry.mekextras.common.content.reactor.ExtraReactorCache;
import com.jerry.mekextras.common.content.reactor.ExtraReactorUpdateProtocol;
import com.jerry.mekextras.common.content.reactor.SynchronizedExtraReactorData;
import io.netty.buffer.ByteBuf;
import mekanism.api.TileNetworkList;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.multiblock.TileEntityMultiblock;
import mekanism.common.util.LangUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

/**
 * The naquadah reactor's casing: the frame of the multiblock and the block that hosts the structure data.
 */
public class TileEntityExtraReactorCasing extends TileEntityMultiblock<SynchronizedExtraReactorData> implements ISecurityTile {

    private static final int STRUCTURE_VALIDATION_INTERVAL = 5;
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);

    public TileEntityExtraReactorCasing() {
        super("NaquadahReactorCasing");
        initializeInventorySlots();
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        long gameTime = world.getTotalWorldTime();
        boolean intervalTick = gameTime % STRUCTURE_VALIDATION_INTERVAL == 0;
        if (tryClaimReactorServerTick()) {
            if (intervalTick) {
                doUpdate();
            }
            if (structure != null) {
                structure.tick(world);
                syncCachedDataFromStructure();
            }
        } else if (intervalTick && structure == null && cachedID == null &&
              this instanceof TileEntityExtraReactorController) {
            // A fresh structure has no shared inventory ID yet. The controller is unique, so it
            // provides one deterministic formation probe even if the last placed component had
            // no neighbouring reactor casing to trigger an immediate update.
            doUpdate();
        }
    }

    private boolean tryClaimReactorServerTick() {
        if (structure != null) {
            return tryClaimStructureServerTick();
        }
        return cachedID != null && getManager().tryClaimServerTick(world, cachedID, world.getTotalWorldTime());
    }


    @Override
    protected SynchronizedExtraReactorData getNewStructure() {
        return new SynchronizedExtraReactorData();
    }

    @Override
    public ExtraReactorCache getNewCache() {
        return new ExtraReactorCache();
    }

    @Override
    protected ExtraReactorUpdateProtocol getProtocol() {
        return new ExtraReactorUpdateProtocol(this);
    }

    @Override
    public MultiblockManager<SynchronizedExtraReactorData> getManager() {
        return ExtraMultiblockManagers.reactorManager;
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.mekanism_extras.naquadah_reactor_casing.name");
    }

    public SynchronizedExtraReactorData getStructureData() {
        return structure;
    }

    public boolean isFormed() {
        return structure != null && (isRemote() ? clientHasStructure : structure.isFormed());
    }

    public double getEnergy() {
        return structure == null ? 0 : structure.getEnergy();
    }

    public double getMaxEnergy() {
        return structure == null ? 0 : structure.getMaxEnergy();
    }

    public double getLastOutput() {
        return structure == null ? 0 : structure.getLastOutput();
    }

    public int getLaserCharges() {
        return structure == null ? 0 : structure.getLaserCharges();
    }

    public int getStoredFuel() {
        return structure == null ? 0 : structure.fuelTank.getStored();
    }
    public boolean isBurning() {
        return structure != null && structure.isBurning();
    }

    public boolean hasReadyHohlraum() {
        return structure != null && structure.hasReadyHohlraum();
    }

    public double getPlasmaTemperature() {
        return structure == null ? 0 : structure.getPlasmaTemperature();
    }

    public double getIgnitionTemperature() {
        return SynchronizedExtraReactorData.IGNITION_TEMPERATURE;
    }

    public int getMinInjectionRate(boolean active) {
        return structure == null ? 0 : structure.getMinInjectionRate(active);
    }

    public double getMaxPlasmaTemperature(boolean active) {
        return structure == null ? 0 : structure.getMaxPlasmaTemperature(active);
    }

    public double getMaxCasingTemperature(boolean active) {
        return structure == null ? 0 : structure.getMaxCasingTemperature(active);
    }

    public double getCasingTemperature() {
        return structure == null ? 0 : structure.getCaseTemperature();
    }

    public double getLastTransferLoss() {
        return structure == null ? 0 : structure.getLastTransferLoss();
    }

    public double getLastEnvironmentLoss() {
        return structure == null ? 0 : structure.getLastEnvironmentLoss();
    }

    public double getPassiveGeneration(boolean active, boolean current) {
        return structure == null ? 0 : structure.getPassiveGeneration(active, current);
    }

    public int getSteamPerTick(boolean current) {
        return structure == null ? 0 : structure.getSteamPerTick(current);
    }

    public double getLaserPerCharge() {
        return SynchronizedExtraReactorData.LASER_PER_CHARGE;
    }

    public int getMaxLaserCharges() {
        return 64;
    }

    public int getFuelCapacity() {
        return SynchronizedExtraReactorData.FUEL_CAPACITY;
    }

    public int getStoredWater() {
        return structure == null ? 0 : structure.waterTank.getFluidAmount();
    }

    public int getStoredSteam() {
        return structure == null ? 0 : structure.steamTank.getStored();
    }

    public mekanism.common.capabilities.fluid.BasicFluidTank getWaterTank() {
        return structure == null ? null : structure.waterTank;
    }

    public mekanism.common.capabilities.tank.ValidatingGasTank getSteamTank() {
        return structure == null ? null : structure.steamTank;
    }

    public mekanism.common.capabilities.tank.ValidatingGasTank getRichNaquadahTank() {
        return structure == null ? null : structure.richNaquadahTank;
    }

    public mekanism.common.capabilities.tank.ValidatingGasTank getRichUraniumTank() {
        return structure == null ? null : structure.richUraniumTank;
    }

    public mekanism.common.capabilities.tank.ValidatingGasTank getFuelTank() {
        return structure == null ? null : structure.fuelTank;
    }

    public int getInjectionRate() {
        return structure == null ? 2 : structure.injectionRate;
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        SynchronizedExtraReactorData reactor = structure;
        boolean hasReactorData = reactor != null;
        data.add(hasReactorData);
        if (hasReactorData) {
            reactor.addStructureData(data);
        }
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            boolean hasReactorData = dataStream.readBoolean();
            if (hasReactorData) {
                if (structure == null) {
                    structure = getNewStructure();
                }
                structure.setFormed(clientHasStructure);
                structure.readStructureData(dataStream);
            }
        }
    }
    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public void readCustomNBT(net.minecraft.nbt.NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        securityComponent.read(nbtTags);
    }

    @Override
    public void writeCustomNBT(net.minecraft.nbt.NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        securityComponent.write(nbtTags);
    }
}

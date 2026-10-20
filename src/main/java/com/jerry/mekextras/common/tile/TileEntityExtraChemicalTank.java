package com.jerry.mekextras.common.tile;

import mekanism.common.network.PacketTileEntity;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.IUpgradeableTile;
import mekanism.common.base.IBaseTierProvider;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.ITankManager;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.Gas;
import mekanism.api.RelativeSide;
import mekanism.api.IContentsListener;
import mekanism.api.AutomationType;
import mekanism.api.TileNetworkList;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.capabilities.ExtraTankGasTank;
import mekanism.common.tile.TileEntityGasTank.GasMode;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.CTTier;
import net.minecraft.block.Block;

import io.netty.buffer.ByteBuf;
import mekanism.api.*;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.math.MathUtils;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.base.*;
import mekanism.common.capabilities.holder.gas.GasTankHelper;
import mekanism.common.capabilities.holder.gas.IGasTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.gas.GasInventorySlot;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.upgrade.GasTankUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.TileUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityExtraChemicalTank extends TileEntityContainerBlock implements IExtraTierMachine, IRedstoneControl, ISideConfiguration, ISecurityTile, IUpgradeableTile, IBaseTierProvider,
        IComputerIntegration, IComparatorSupport, ITankManager {

    private static final String[] methods = new String[]{"getMaxGas", "getStoredGas", "getGas"};
    /**
     * The type of gas stored in this tank.
     */
    public ExtraTankGasTank gasTank;

    public CTTier tier = CTTier.ABSOLUTE;

    public GasMode dumping;

    public int currentGasAmount;

    public int currentRedstoneLevel;

    /**
     * This machine's current RedstoneControl type.
     */
    public RedstoneControl controlType;

    public TileComponentEjector ejectorComponent;
    public TileComponentConfig configComponent;
    public TileComponentSecurity securityComponent;
    private GasInventorySlot drainSlot;
    private GasInventorySlot fillSlot;

    public TileEntityExtraChemicalTank() {
        super("GasTank");
        configComponent = new TileComponentConfig(this, TransmissionType.GAS, TransmissionType.ITEM);
        gasTank = ExtraTankGasTank.create(() -> (int) Math.min(Integer.MAX_VALUE, tier.getStorage()), () -> (int) Math.min(Integer.MAX_VALUE, tier.getOutput()), this::isValidGas, this);
        initializeInventorySlots();
        configComponent.setupItemIOConfig(fillSlot, drainSlot);
        configComponent.setConfig(TransmissionType.ITEM, DataType.INPUT, DataType.INPUT, DataType.OUTPUT, DataType.INPUT, DataType.INPUT, DataType.INPUT);
        configComponent.setCanEject(TransmissionType.ITEM, false);

        configComponent.setupIOConfig(TransmissionType.GAS, gasTank, RelativeSide.FRONT);
        configComponent.setEjecting(TransmissionType.GAS, true);

        dumping = GasMode.IDLE;
        controlType = RedstoneControl.DISABLED;

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS)
                .setCanEject(type -> MekanismUtils.canFunction(this) && (false || dumping != GasMode.DUMPING));

        securityComponent = new TileComponentSecurity(this);
    }

    public TileEntityExtraChemicalTank(CTTier tier) {
        this();
        this.tier = tier;
    }

    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = createInventorySlotHelper();
        drainSlot = builder.addSlot(GasInventorySlot.drain(gasTank, listener, 16, 16));
        drainSlot.setSlotType(ContainerSlotType.OUTPUT);
        drainSlot.setSlotOverlay(SlotOverlay.PLUS);
        fillSlot = builder.addSlot(GasInventorySlot.fill(gasTank, listener, 16, 48));
        fillSlot.setSlotType(ContainerSlotType.INPUT);
        fillSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        GasTankHelper builder = createGasTankHelper();
        builder.addTank(gasTank);
        return builder.build();
    }

    @Override
    public void onAsyncUpdateServer() {
        super.onAsyncUpdateServer();
        drainSlot.drainTank();
        fillSlot.fillTank();
        Mekanism.EXECUTE_MANAGER.addSyncTask(() -> {
            handTank();
            int newGasAmount = gasTank.getStored();
            if (newGasAmount != currentGasAmount) {
                MekanismUtils.saveChunk(this);
            }
            currentGasAmount = newGasAmount;
            int newRedstoneLevel = getRedstoneLevel();
            if (newRedstoneLevel != currentRedstoneLevel) {
                markNoUpdateSync();
                currentRedstoneLevel = newRedstoneLevel;
            }
        });
    }

    public void handTank() {
        if (true) {
            if (dumping == GasMode.DUMPING) {
                gasTank.extract((int) Math.min(Integer.MAX_VALUE, tier.getStorage() / 400), Action.EXECUTE, AutomationType.INTERNAL);
            } else if (dumping == GasMode.DUMPING_EXCESS) {
                int target = MathUtils.clampToInt(gasTank.getMaxGas() * MekanismConfig.current().general.dumpExcessKeepRatio.val());
                int stored = gasTank.getStored();
                if (target < stored) {
                    gasTank.extract((int) Math.min(Integer.MAX_VALUE, Math.min(stored - target, tier.getOutput())), Action.EXECUTE, AutomationType.INTERNAL);
                }
            }
        }
    }

    public boolean isValidGas(Gas gas) {
        return gas != null && (false || !gas.isRadiation());
    }


    @Override
    public boolean canInstallUpgrade(BaseTier upgradeTier) {
        //Mekanism's own tier installers are not used for our tiers, the extra tier installers replace the block instead.
        return false;
    }

    @Override
    public BaseTier getBaseTier() {
        return tier.getBaseTier();
    }

    @Nullable
    @Override
    public IUpgradeData getUpgradeData(BaseTier upgradeTier) {
        if (!canInstallUpgrade(upgradeTier)) {
            return null;
        }
        return new GasTankUpgradeData(upgradeTier, facing, clientFacing, ticker, redstone, redstoneLastTick, doAutoSync, getControlType(),
                dumping, gasTank.getGas(), currentGasAmount, currentRedstoneLevel, writeUpgradeComponentData(), drainSlot.serializeNBT(),
                fillSlot.serializeNBT());
    }

    @Nonnull
    private NBTTagCompound writeUpgradeComponentData() {
        NBTTagCompound componentData = new NBTTagCompound();
        configComponent.write(componentData);
        ejectorComponent.write(componentData);
        securityComponent.write(componentData);
        return componentData;
    }

    @Override
    public boolean parseUpgradeData(IUpgradeData upgradeData) {
        if (upgradeData instanceof GasTankUpgradeData data && data.getUpgradeTier().ordinal() == tier.ordinal() + 1) {
            facing = data.facing;
            clientFacing = data.clientFacing;
            ticker = data.ticker;
            redstone = data.redstone;
            redstoneLastTick = data.redstoneLastTick;
            doAutoSync = data.doAutoSync;
            tier = CTTier.values()[data.getUpgradeTier().ordinal()];
            setControlType(data.controlType);
            dumping = data.dumping;
            currentGasAmount = data.currentGasAmount;
            currentRedstoneLevel = data.currentRedstoneLevel;
            configComponent.read(data.componentData);
            ejectorComponent.read(data.componentData);
            securityComponent.read(data.componentData);
            ejectorComponent.setOutputData(configComponent, TransmissionType.GAS);
            drainSlot.deserializeNBT(data.drainSlot);
            fillSlot.deserializeNBT(data.fillSlot);
            gasTank.setGas(data.stored);
            sanitizeAndClampTank();
            MekanismUtils.updateBlock(world, getPos());
            Mekanism.packetHandler.sendUpdatePacket(this);
            markNoUpdateSync();
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public String getName() {
        return getBlockType().getLocalizedName();
    }

    @Override
    public Object[] getManagedTanks() {
        return new Object[]{gasTank};
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int type = dataStream.readInt();
            if (type == 0) {
                int index = (dumping.ordinal() + 1) % GasMode.values().length;
                dumping = GasMode.values()[index];
            }
            if (type == 1) {
                gasTank.setEmpty();
            }
            playersUsing.forEach(player -> Mekanism.packetHandler.sendTo(new TileEntityMessage(this), (EntityPlayerMP) player));

            return;
        }
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            CTTier prevTier = tier;
            tier = MekanismUtils.getByIndex(CTTier.values(), dataStream.readInt(), tier);
            TileUtils.readTankData(dataStream, gasTank);
            dumping = MekanismUtils.getByIndex(GasMode.values(), dataStream.readInt(), dumping);
            controlType = MekanismUtils.getByIndex(RedstoneControl.values(), dataStream.readInt(), controlType);
            if (prevTier != tier) {
                MekanismUtils.updateBlock(world, getPos());
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        tier = MekanismUtils.getByIndex(CTTier.values(), nbtTags.getInteger("tier"), tier);
        if (!hasStoredGasTanks(nbtTags) && nbtTags.hasKey("gasTank")) {
            gasTank.read(nbtTags.getCompoundTag("gasTank"));
        }
        sanitizeAndClampTank();
        dumping = MekanismUtils.getByIndex(GasMode.values(), nbtTags.getInteger("dumping"), dumping);
        controlType = MekanismUtils.getByIndex(RedstoneControl.values(), nbtTags.getInteger("controlType"), controlType);
    }

    private void sanitizeAndClampTank() {
        GasStack stored = gasTank.getGas();
        if (stored == null || stored.getGas() == null || stored.amount <= 0 || !isValidGas(stored.getGas())) {
            gasTank.setEmpty();
        } else {
            gasTank.setStackSize(stored.amount, Action.EXECUTE);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
        nbtTags.setInteger("dumping", dumping.ordinal());
        nbtTags.setInteger("controlType", controlType.ordinal());
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        TileUtils.addTankData(data, gasTank);
        data.add(dumping.ordinal());
        data.add(controlType.ordinal());
        return data;
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.DOWN && facing != EnumFacing.UP;
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
    public RedstoneControl getControlType() {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type) {
        controlType = type;
    }

    @Override
    public boolean canPulse() {
        return false;
    }

    @Override
    public TileComponentEjector getEjector() {
        return ejectorComponent;
    }

    @Override
    public TileComponentConfig getConfig() {
        return configComponent;
    }

    @Override
    public EnumFacing getOrientation() {
        return facing;
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public String[] getMethods() {
        return methods;
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        return switch (method) {
            case 0 -> new Object[]{gasTank.getMaxGas()};
            case 1 -> new Object[]{gasTank.getStored()};
            case 2 -> new Object[]{gasTank.getGas()};
            default -> throw new NoSuchMethodException();
        };
    }



    @Override
    protected boolean shouldDumpRadiation() {
        return true;
    }

    @Override
    public int getBlockGuiID(Block block, int metadata) {
        return 10;
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
        return ExtraBlocks.getChemicalTankBlock(newTier);
    }
}



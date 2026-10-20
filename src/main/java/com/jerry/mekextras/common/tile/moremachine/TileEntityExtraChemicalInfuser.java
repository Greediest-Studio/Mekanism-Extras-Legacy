package com.jerry.mekextras.common.tile.moremachine;

import io.netty.buffer.ByteBuf;
import mekanism.api.EnumColor;
import mekanism.api.IContentsListener;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.*;
import mekanism.api.text.TextComponentGroup;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.base.IGuiProvider;
import mekanism.common.base.ISustainedData;
import mekanism.common.base.ITankManager;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.capabilities.holder.gas.GasTankHelper;
import mekanism.common.capabilities.holder.gas.IGasTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.gas.GasInventorySlot;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.cache.CachedRecipe;
import mekanism.common.recipe.cache.ChemicalPairCachedRecipe;
import mekanism.common.recipe.cache.inputs.InputHelper;
import mekanism.common.recipe.cache.outputs.OutputHelper;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;
import mekanism.common.recipe.outputs.GasOutput;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.upgrade.TierUpgradeData;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.prefab.TileEntityBasicMachine;
import mekanism.common.util.*;
import mekceumoremachine.common.capability.ResizableGasTank;
import mekanism.multiblockmachine.common.registries.MultiblockMachineBlocks;
import mekceumoremachine.common.MEKCeuMoreMachine;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import mekceumoremachine.common.tile.interfaces.ITierMachine;
import mekceumoremachine.common.upgrade.FirstChemicalInfuserUpgradeData;
import mekceumoremachine.common.upgrade.LargeChemicalInfuserUpgradeData;
import mekceumoremachine.common.upgrade.LargeMachineUpgradeDataApplier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@InterfaceList({
        @Interface(iface = "mekceumoremachine.common.tile.interfaces.ILargeMachine", modid = "mekanismmultiblockmachine"),
})
public class TileEntityExtraChemicalInfuser extends TileEntityBasicMachine<ChemicalPairInput, GasOutput, ChemicalInfuserRecipe> implements IExtraTierMachine, ISustainedData, Upgrade.IUpgradeInfoHandler,
        ITankManager, ITierMachine<ExtraMachineTier>, ISecurityTile {

    public TileEntityExtraChemicalInfuser(ExtraMachineTier tier) {
        this();
        this.tier = tier;
    }

    public static final int MAX_GAS = 10000;
    public ResizableGasTank leftTank;
    public ResizableGasTank rightTank;
    public ResizableGasTank centerTank;

    public ChemicalInfuserRecipe cachedRecipe;

    public double clientEnergyUsed;

    public boolean sorting;
    public ExtraMachineTier tier = ExtraMachineTier.ABSOLUTE;
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);
    private GasInventorySlot leftSlot;
    private GasInventorySlot rightSlot;
    private GasInventorySlot centerSlot;
    private EnergyInventorySlot energySlot;

    public TileEntityExtraChemicalInfuser() {
        super("cheminfuser", "TierChemicalInfuser", 0, BlockStateMachine.MachineType.CHEMICAL_INFUSER.getUsage(), 4, 1);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.ENERGY);
        initializeInventorySlots();
        configComponent.setupItemDualInputOutputConfig(leftSlot, rightSlot, centerSlot, energySlot);
        configComponent.setConfig(TransmissionType.ITEM, DataType.NONE, DataType.NONE, DataType.OUTPUT, DataType.ENERGY, DataType.INPUT_1, DataType.INPUT_2);
        configComponent.setCanEject(TransmissionType.ITEM, false);

        configComponent.setupGasDualInputOutputConfig(leftTank, rightTank, centerTank);
        configComponent.setConfig(TransmissionType.GAS, DataType.NONE, DataType.NONE, DataType.OUTPUT, DataType.NONE, DataType.INPUT_1, DataType.INPUT_2);

        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS);
    }

        protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = createInventorySlotHelper();
        leftSlot = builder.addSlot(GasInventorySlot.fill(leftTank, listener, 6, 56));
        rightSlot = builder.addSlot(GasInventorySlot.fill(rightTank, listener, 154, 56));
        centerSlot = builder.addSlot(GasInventorySlot.drain(centerTank, listener, 80, 65));
        energySlot = builder.addSlot(EnergyInventorySlot.fillOrConvert(getMainEnergyContainer(), this::getWorld, listener, 154, 14));
        leftSlot.setSlotType(ContainerSlotType.INPUT);
        leftSlot.setSlotOverlay(SlotOverlay.MINUS);
        rightSlot.setSlotType(ContainerSlotType.INPUT);
        rightSlot.setSlotOverlay(SlotOverlay.MINUS);
        centerSlot.setSlotType(ContainerSlotType.OUTPUT);
        centerSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

        protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        GasTankHelper builder = createGasTankHelper();
        builder.addTank(getOrCreateLeftTank(listener));
        builder.addTank(getOrCreateRightTank(listener));
        builder.addTank(getOrCreateCenterTank(listener));
        return builder.build();
    }

    private ResizableGasTank getOrCreateLeftTank(IContentsListener listener) {
        if (leftTank == null) {
            leftTank = ResizableGasTank.input(MAX_GAS * tier.processes, this::isValidLeftGas, getRecipeCacheListener());
        }
        return leftTank;
    }

    private ResizableGasTank getOrCreateRightTank(IContentsListener listener) {
        if (rightTank == null) {
            rightTank = ResizableGasTank.input(MAX_GAS * tier.processes, this::isValidRightGas, getRecipeCacheListener());
        }
        return rightTank;
    }

    private ResizableGasTank getOrCreateCenterTank(IContentsListener listener) {
        if (centerTank == null) {
            centerTank = ResizableGasTank.output(MAX_GAS * tier.processes, getRecipeCacheChangeListener(listener));
        }
        return centerTank;
    }

        public void onAsyncUpdateServer() {
        super.onAsyncUpdateServer();
        energySlot.fillContainerOrConvert();
        leftSlot.fillTank();
        rightSlot.fillTank();
        centerSlot.drainTank();
        clientEnergyUsed = processRecipe(getMainEnergyContainer());
        finishRecipeTick();
    }

        protected boolean supportsAsyncIdleSkipping() {
        return getClass() == TileEntityExtraChemicalInfuser.class;
    }

        protected boolean isAsyncUpdateIdle() {
        return leftTank.isEmpty() && rightTank.isEmpty() && centerTank.isEmpty() && leftSlot.isEmpty() &&
              rightSlot.isEmpty() && centerSlot.isEmpty() && energySlot.isEmpty() && clientEnergyUsed == 0 &&
              isEmptyRecipeStateSettled();
    }

    private void finishRecipeTick() {
        prevEnergy = getEnergy();
    }

    public int getUpgradedUsage(ChemicalInfuserRecipe recipe) {
        int possibleProcess = Math.min((int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED)), MekanismConfig.current().mekce.MAXspeedmachines.val());
        possibleProcess *= tier.processes;
        return Math.max(possibleProcess, 1);
    }

        public ChemicalPairInput getInput() {
        return new ChemicalPairInput(leftTank.getGas(), rightTank.getGas());
    }

        public ChemicalInfuserRecipe getRecipe() {
        refreshRecipeLookupCache();
        ChemicalPairInput input = getInput();
        if (cachedRecipe == null || !input.testEquality(cachedRecipe.getInput())) {
            cachedRecipe = RecipeHandler.getChemicalInfuserRecipe(getInput());
        }
        return cachedRecipe;
    }

        protected void clearRecipeLookupCache() {
        super.clearRecipeLookupCache();
        cachedRecipe = null;
    }

        public ChemicalInfuserRecipe getRecipe(int cacheIndex) {
        return getRecipe();
    }

        public boolean canOperate(ChemicalInfuserRecipe recipe) {
        return recipe != null && recipe.canOperate(leftTank, rightTank, centerTank);
    }

        public CachedRecipe<ChemicalInfuserRecipe> createNewCachedRecipe(ChemicalInfuserRecipe recipe, int cacheIndex) {
        return new ChemicalPairCachedRecipe<>(recipe, this::shouldRecheckAllRecipeErrors,
              InputHelper.getGasInputHandler(leftTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_LEFT_INPUT),
              InputHelper.getGasInputHandler(rightTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_RIGHT_INPUT),
              OutputHelper.getGasOutputHandler(centerTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE), recipe.getInput(), recipe.getOutput())
              .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
              .setActive(active -> {
                  if (active || prevEnergy >= getEnergy()) {
                      setActive(active);
                  }
              })
              .setEnergyRequirements(() -> energyPerTick, getMainEnergyContainer())
              .setRequiredTicks(() -> ticksRequired)
              .setBaselineMaxOperations(() -> getUpgradedUsage(recipe))
              .setOperatingTicksChanged(ticks -> operatingTicks = ticks)
              .setErrorsChanged(this::onRecipeErrorsChanged)
              .setOnFinish(() -> {
                  operatingTicks = 0;
                  onCachedRecipeFinish();
              });
    }

        public Map<ChemicalPairInput, ChemicalInfuserRecipe> getRecipes() {
        return RecipeHandler.Recipe.CHEMICAL_INFUSER.get();
    }

    private boolean isValidLeftGas(Gas gas) {
        return isValidGas(gas, rightTank);
    }

    private boolean isValidRightGas(Gas gas) {
        return isValidGas(gas, leftTank);
    }

    private boolean isValidGas(Gas gas, GasTank otherTank) {
        if (gas == null) {
            return false;
        }
        Gas otherGas = otherTank == null ? null : otherTank.getGasType();
        for (ChemicalPairInput input : getRecipes().keySet()) {
            if (otherGas == null && (input.leftGas.getGas() == gas || input.rightGas.getGas() == gas)) {
                return true;
            } else if (otherGas != null && ((input.leftGas.getGas() == gas && input.rightGas.getGas() == otherGas) ||
                  (input.rightGas.getGas() == gas && input.leftGas.getGas() == otherGas))) {
                return true;
            }
        }
        return false;
    }

        public void handlePacketData(ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int type = dataStream.readInt();
            if (type == 0) {
                sorting = !sorting;
                markNoUpdateSync();
            }
            return;
        }
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            ExtraMachineTier prevTier = tier;
            tier = ExtraMachineTier.byIndex(dataStream.readInt());
            sorting = dataStream.readBoolean();
            leftTank.setMaxGas(tier.processes * MAX_GAS);
            rightTank.setMaxGas(tier.processes * MAX_GAS);
            centerTank.setMaxGas(tier.processes * MAX_GAS);
            clientEnergyUsed = dataStream.readDouble();
            TileUtils.readTankData(dataStream, leftTank);
            TileUtils.readTankData(dataStream, rightTank);
            TileUtils.readTankData(dataStream, centerTank);
            if (prevTier != tier) {
                MekanismUtils.updateBlock(world, getPos());
            }
        }
    }

        public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        data.add(sorting);
        data.add(clientEnergyUsed);
        TileUtils.addTankData(data, leftTank);
        TileUtils.addTankData(data, rightTank);
        TileUtils.addTankData(data, centerTank);
        return data;
    }

        public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        securityComponent.read(nbtTags);
        tier = ExtraMachineTier.byIndex(nbtTags.getInteger("tier"));
        sorting = nbtTags.getBoolean("sorting");
        leftTank.setMaxGas(tier.processes * MAX_GAS);
        rightTank.setMaxGas(tier.processes * MAX_GAS);
        centerTank.setMaxGas(tier.processes * MAX_GAS);
        leftTank.read(nbtTags.getCompoundTag("leftTank"));
        rightTank.read(nbtTags.getCompoundTag("rightTank"));
        centerTank.read(nbtTags.getCompoundTag("centerTank"));
    }

        public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        securityComponent.write(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
        nbtTags.setBoolean("sorting", sorting);
        nbtTags.setTag("leftTank", leftTank.write(new NBTTagCompound()));
        nbtTags.setTag("rightTank", rightTank.write(new NBTTagCompound()));
        nbtTags.setTag("centerTank", centerTank.write(new NBTTagCompound()));
    }

        public void writeSustainedData(ItemStack itemStack) {
        if (leftTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "leftTank", leftTank.getGas().write(new NBTTagCompound()));
        }
        if (rightTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "rightTank", rightTank.getGas().write(new NBTTagCompound()));
        }
        if (centerTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "centerTank", centerTank.getGas().write(new NBTTagCompound()));
        }
    }

        public void readSustainedData(ItemStack itemStack) {
        leftTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "leftTank")));
        rightTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "rightTank")));
        centerTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "centerTank")));
    }

        public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

        public Object[] getManagedTanks() {
        return new Object[]{leftTank, rightTank, centerTank};
    }

        public String[] getMethods() {
        return new String[0];
    }

        public Object[] invoke(int method, Object[] args) throws NoSuchMethodException {
        return new Object[0];
    }

        public boolean applyTierUpgrade(BaseTier upgradeTier) {
        if (!tier.canUpgradeTo(upgradeTier)) {
            return false;
        }
        tier = ExtraMachineTier.ABSOLUTE;
        leftTank.setMaxGas(tier.processes * MAX_GAS);
        rightTank.setMaxGas(tier.processes * MAX_GAS);
        centerTank.setMaxGas(tier.processes * MAX_GAS);
        upgradeComponent.getSupportedTypes().forEach(this::recalculateUpgradables);
        Mekanism.packetHandler.sendUpdatePacket(this);
        markNoUpdateSync();
        return true;
    }

        public ExtraMachineTier getTier() {
        return tier;
    }

    @Nonnull
        public String getName() {
        return getBlockType().getLocalizedName();
    }

        public int getBlockGuiID(Block block, int metadata) {
        return 6;
    }

        public IGuiProvider guiProvider() {
        return MEKCeuMoreMachine.proxy;
    }

        public double getMaxEnergy() {
        return upgradeComponent.isUpgradeInstalled(Upgrade.ENERGY) ? MekanismUtils.getMaxEnergy(this, getTierEnergy()) : getTierEnergy();
    }

    public double getTierEnergy() {
        return BlockStateMachine.MachineType.CHEMICAL_INFUSER.getStorage() * tier.processes;
    }

    public boolean isUpgrade = true;

                        

        

    /**
     * @author sddsd2332
     * @reason 取消在升级的时候进行辐射判断
     */
        public boolean shouldDumpRadiation() {
        return isUpgrade;
    }
    // ---- extra tier support ----

    @Override
    public AdvancedTier getExtraTier() {
        return tier.getAdvanceTier();
    }

    @Override
    public void setExtraTier(AdvancedTier extraTier) {
        //The tier of this machine is defined by its block, upgrading replaces the machine with the next block.
    }

    @Override
    public Block getBlockForTier(AdvancedTier extraTier) {
        return ExtraBlocks.getMoreMachineBlock("chemical_infuser", extraTier);
    }
    public boolean isSorting() {
        return sorting;
    }
    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }
}
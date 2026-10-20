package com.jerry.mekextras.common.tile.moremachine;

import io.netty.buffer.ByteBuf;
import mekanism.api.EnumColor;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.*;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.*;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.gas.GasTankHelper;
import mekanism.common.capabilities.holder.gas.IGasTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.gas.GasInventorySlot;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.cache.CachedRecipe;
import mekanism.common.recipe.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.recipe.cache.TwoInputCachedRecipe;
import mekanism.common.recipe.cache.inputs.InputHelper;
import mekanism.common.recipe.cache.outputs.OutputHelper;
import mekanism.common.recipe.inputs.GasAndFluidInput;
import mekanism.common.recipe.machines.WasherRecipe;
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
import mekceumoremachine.common.capability.ResizableFluidTank;
import mekceumoremachine.common.capability.ResizableGasTank;
import mekanism.multiblockmachine.common.registries.MultiblockMachineBlocks;
import mekceumoremachine.common.MEKCeuMoreMachine;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import mekceumoremachine.common.tile.interfaces.ITierMachine;
import mekceumoremachine.common.upgrade.FirstChemicalWasherUpgradeData;
import mekceumoremachine.common.upgrade.LargeChemicalWasherUpgradeData;
import mekceumoremachine.common.upgrade.LargeMachineUpgradeDataApplier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@InterfaceList({
        @Interface(iface = "mekceumoremachine.common.tile.interfaces.ILargeMachine", modid = "mekanismmultiblockmachine"),
})
public class TileEntityExtraChemicalWasher extends TileEntityBasicMachine<GasAndFluidInput, GasOutput, WasherRecipe> implements IExtraTierMachine, ISustainedData, Upgrade.IUpgradeInfoHandler, ITankManager, ITierMachine<ExtraMachineTier>, ISecurityTile {

    public TileEntityExtraChemicalWasher(ExtraMachineTier tier) {
        this();
        this.tier = tier;
    }

    public static final int MAX_GAS = 10000;
    public static final int MAX_FLUID = 10000;
    //TODO
    public static int WATER_USAGE = 5;

    public ResizableFluidTank fluidTank;
    public ResizableGasTank inputTank;
    public ResizableGasTank outputTank;

    public WasherRecipe cachedRecipe;
    public double clientEnergyUsed;
    private int currentRedstoneLevel;
    public boolean sorting;
    public ExtraMachineTier tier = ExtraMachineTier.ABSOLUTE;
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);
    private FluidInventorySlot inputSlot;
    private OutputInventorySlot outputSlot;
    private GasInventorySlot gasSlot;
    private EnergyInventorySlot energySlot;

    public TileEntityExtraChemicalWasher() {
        super("washer", "TierChemicalWasher", 0, MachineType.CHEMICAL_WASHER.getUsage(), 4, 1);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.FLUID, TransmissionType.GAS);

        initializeInventorySlots();
        configComponent.setupItemIOConfig(Collections.singletonList(inputSlot), Arrays.asList(gasSlot, outputSlot), energySlot, true);
        configComponent.setConfig(TransmissionType.ITEM, DataType.INPUT, DataType.INPUT, DataType.INPUT, DataType.ENERGY, DataType.INPUT, DataType.OUTPUT);
        configComponent.setCanEject(TransmissionType.ITEM, false);

        configComponent.setupFluidInputConfig(fluidTank);

        configComponent.setupIOConfig(TransmissionType.GAS, inputTank, outputTank, RelativeSide.RIGHT);
        configComponent.setConfig(TransmissionType.GAS, DataType.INPUT, DataType.INPUT, DataType.INPUT, DataType.INPUT, DataType.INPUT, DataType.OUTPUT);

        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS)
              .setCanTankEject(tank -> tank != inputTank);
    }

        protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = createInventorySlotHelper();
        inputSlot = builder.addSlot(FluidInventorySlot.fill(fluidTank, listener, 180, 71));
        inputSlot.setSlotType(ContainerSlotType.INPUT);
        inputSlot.setSlotOverlay(SlotOverlay.INPUT);
        outputSlot = builder.addSlot(OutputInventorySlot.at(getRecipeCacheChangeListener(listener), 180, 102));
        outputSlot.setSlotOverlay(SlotOverlay.OUTPUT);
        gasSlot = builder.addSlot(GasInventorySlot.drain(outputTank, listener, 152, 56));
        gasSlot.setSlotOverlay(SlotOverlay.MINUS);
        energySlot = builder.addSlot(EnergyInventorySlot.fillOrConvert(getMainEnergyContainer(), this::getWorld, listener, 152, 14));
        return builder.build();
    }

        protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = createFluidTankHelper();
        builder.addTank(getOrCreateFluidTank(listener));
        return builder.build();
    }

        protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        GasTankHelper builder = createGasTankHelper();
        builder.addTank(getOrCreateInputTank(listener));
        builder.addTank(getOrCreateOutputTank(listener));
        return builder.build();
    }

    private ResizableFluidTank getOrCreateFluidTank(IContentsListener listener) {
        if (fluidTank == null) {
            fluidTank = ResizableFluidTank.input(MAX_FLUID * tier.processes,
                  fluid -> fluid != null && fluid.getFluid() == FluidRegistry.WATER, getRecipeCacheListener());
        }
        return fluidTank;
    }

    private ResizableGasTank getOrCreateInputTank(IContentsListener listener) {
        if (inputTank == null) {
            inputTank = ResizableGasTank.input(MAX_GAS * tier.processes, gas -> RecipeHandler.Recipe.CHEMICAL_WASHER.containsRecipe(gas), getRecipeCacheListener());
        }
        return inputTank;
    }

    private ResizableGasTank getOrCreateOutputTank(IContentsListener listener) {
        if (outputTank == null) {
            outputTank = ResizableGasTank.output(MAX_GAS * tier.processes, getRecipeCacheChangeListener(listener));
        }
        return outputTank;
    }

        public void onAsyncUpdateServer() {
        super.onAsyncUpdateServer();
        energySlot.fillContainerOrConvert();
        inputSlot.fillTank(outputSlot);
        gasSlot.drainTank();
        clientEnergyUsed = processRecipe(getMainEnergyContainer());
        finishRecipeTick();
    }

        protected boolean supportsAsyncIdleSkipping() {
        return getClass() == TileEntityExtraChemicalWasher.class;
    }

        protected boolean isAsyncUpdateIdle() {
        return inputTank.isEmpty() && fluidTank.isEmpty() && outputTank.isEmpty() && inputSlot.isEmpty() &&
              outputSlot.isEmpty() && gasSlot.isEmpty() && energySlot.isEmpty() && clientEnergyUsed == 0 &&
              currentRedstoneLevel == getRedstoneLevel() && isEmptyRecipeStateSettled();
    }

    private void finishRecipeTick() {
        prevEnergy = getEnergy();
        int newRedstoneLevel = getRedstoneLevel();
        if (newRedstoneLevel != currentRedstoneLevel) {
            updateComparatorOutputLevelSync();
            currentRedstoneLevel = newRedstoneLevel;
        }
    }

        public WasherRecipe getRecipe() {
        refreshRecipeLookupCache();
        GasAndFluidInput input = getInput();
        if (cachedRecipe == null || !input.testEquality(cachedRecipe.getInput())) {
            cachedRecipe = RecipeHandler.getChemicalWasherRecipe(getInput());
        }
        return cachedRecipe;
    }

        protected void clearRecipeLookupCache() {
        super.clearRecipeLookupCache();
        cachedRecipe = null;
    }

        public GasAndFluidInput getInput() {
        return new GasAndFluidInput(inputTank.getGas(), fluidTank.getFluid());
    }

        public boolean canOperate(WasherRecipe recipe) {
        return recipe != null && recipe.canOperate(inputTank, fluidTank, outputTank);
    }

        public WasherRecipe getRecipe(int cacheIndex) {
        return getRecipe();
    }

        public CachedRecipe<WasherRecipe> createNewCachedRecipe(WasherRecipe recipe, int cacheIndex) {
        return new TwoInputCachedRecipe<>(recipe, this::shouldRecheckAllRecipeErrors,
              InputHelper.getGasInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT),
              InputHelper.getFluidInputHandler(fluidTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT),
              OutputHelper.getGasOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE),
              () -> recipe.getInput().ingredientGas, () -> recipe.getInput().ingredientFluid,
              (gas, fluid) -> gas != null && gas.isGasEqual(recipe.getInput().ingredientGas)
                    && fluid != null && fluid.isFluidEqual(recipe.getInput().ingredientFluid),
              (gas, fluid) -> recipe.getOutput().output.copy(), gas -> gas == null || gas.amount <= 0,
              fluid -> fluid == null || fluid.amount <= 0, output -> output == null || output.amount <= 0)
              .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
              .setActive(active -> {
                  if (active || prevEnergy >= getEnergy()) {
                      setActive(active);
                  }
              })
              .setEnergyRequirements(() -> energyPerTick, getMainEnergyContainer())
              .setRequiredTicks(() -> ticksRequired)
              .setBaselineMaxOperations(this::getUpgradedUsage)
              .setOperatingTicksChanged(ticks -> operatingTicks = ticks)
              .setErrorsChanged(this::onRecipeErrorsChanged)
              .setOnFinish(() -> {
                  operatingTicks = 0;
                  onCachedRecipeFinish();
              });
    }

        public Map<GasAndFluidInput, WasherRecipe> getRecipes() {
        return RecipeHandler.Recipe.CHEMICAL_WASHER.get();
    }

    public int getUpgradedUsage() {
        int possibleProcess = Math.min((int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED)), MekanismConfig.current().mekce.MAXspeedmachines.val());
        possibleProcess *= tier.processes;
        return Math.max(possibleProcess, 1);
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
            fluidTank.setCapacity(tier.processes * MAX_FLUID);
            inputTank.setMaxGas(tier.processes * MAX_GAS);
            outputTank.setMaxGas(tier.processes * MAX_GAS);
            clientEnergyUsed = dataStream.readDouble();
            TileUtils.readTankData(dataStream, fluidTank);
            TileUtils.readTankData(dataStream, inputTank);
            TileUtils.readTankData(dataStream, outputTank);
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
        TileUtils.addTankData(data, fluidTank);
        TileUtils.addTankData(data, inputTank);
        TileUtils.addTankData(data, outputTank);
        return data;
    }

        public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        securityComponent.read(nbtTags);
        tier = ExtraMachineTier.byIndex(nbtTags.getInteger("tier"));
        sorting = nbtTags.getBoolean("sorting");
        fluidTank.setCapacity(tier.processes * MAX_FLUID);
        inputTank.setMaxGas(tier.processes * MAX_GAS);
        outputTank.setMaxGas(tier.processes * MAX_GAS);
        fluidTank.readFromNBT(nbtTags.getCompoundTag("leftTank"));
        inputTank.read(nbtTags.getCompoundTag("rightTank"));
        outputTank.read(nbtTags.getCompoundTag("centerTank"));
    }

        public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        securityComponent.write(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
        nbtTags.setBoolean("sorting", sorting);
        nbtTags.setTag("leftTank", fluidTank.writeToNBT(new NBTTagCompound()));
        nbtTags.setTag("rightTank", inputTank.write(new NBTTagCompound()));
        nbtTags.setTag("centerTank", outputTank.write(new NBTTagCompound()));
    }

        public void writeSustainedData(ItemStack itemStack) {
        if (fluidTank.getFluid() != null) {
            ItemDataUtils.setCompound(itemStack, "fluidTank", fluidTank.getFluid().writeToNBT(new NBTTagCompound()));
        }
        if (inputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "inputTank", inputTank.getGas().write(new NBTTagCompound()));
        }
        if (outputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "outputTank", outputTank.getGas().write(new NBTTagCompound()));
        }
    }

        public void readSustainedData(ItemStack itemStack) {
        fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(ItemDataUtils.getCompound(itemStack, "fluidTank")));
        inputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "inputTank")));
        outputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "outputTank")));
    }

        public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

        public Object[] getManagedTanks() {
        return new Object[]{fluidTank, inputTank, outputTank};
    }

        public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(inputTank.getStored(), inputTank.getMaxGas());
    }

    public boolean isComparatorInput(mekanism.api.transmitters.TransmissionType type) {
        return type == mekanism.api.transmitters.TransmissionType.GAS;
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
        fluidTank.setCapacity(tier.processes * MAX_FLUID);
        inputTank.setMaxGas(tier.processes * MAX_GAS);
        outputTank.setMaxGas(tier.processes * MAX_GAS);
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
        return 8;
    }

        public IGuiProvider guiProvider() {
        return MEKCeuMoreMachine.proxy;
    }

        public double getMaxEnergy() {
        return upgradeComponent.isUpgradeInstalled(Upgrade.ENERGY) ? MekanismUtils.getMaxEnergy(this, getTierEnergy()) : getTierEnergy();
    }

    public double getTierEnergy() {
        return MachineType.CHEMICAL_WASHER.getStorage() * tier.processes;
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
        return ExtraBlocks.getMoreMachineBlock("chemical_washer", extraTier);
    }
    public boolean isSorting() {
        return sorting;
    }
    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }
}
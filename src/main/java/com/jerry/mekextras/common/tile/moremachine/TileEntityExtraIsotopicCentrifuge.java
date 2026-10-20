package com.jerry.mekextras.common.tile.moremachine;

import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.*;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.*;
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
import mekanism.common.recipe.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.recipe.cache.OneInputCachedRecipe;
import mekanism.common.recipe.cache.inputs.InputHelper;
import mekanism.common.recipe.cache.outputs.OutputHelper;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.IsotopicRecipe;
import mekanism.common.recipe.outputs.GasOutput;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.prefab.TileEntityBasicMachine;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.*;
import mekceumoremachine.common.capability.ResizableGasTank;
import mekceumoremachine.common.MEKCeuMoreMachine;
import mekceumoremachine.common.block.MachineStructureOffsets;
import com.jerry.mekextras.api.IExtraTierMachine;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.tier.ExtraMachineTier;
import mekceumoremachine.common.tile.interfaces.ITierMachine;
import mekceumoremachine.common.upgrade.FirstIsotopicCentrifugeUpgradeData;
import mekceumoremachine.common.upgrade.LargeMachineUpgradeDataApplier;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class TileEntityExtraIsotopicCentrifuge extends TileEntityBasicMachine<GasInput, GasOutput, IsotopicRecipe> implements IExtraTierMachine, ISustainedData, IBoundingBlock, Upgrade.IUpgradeInfoHandler, ITankManager, ITierMachine<ExtraMachineTier>, ISpecialSelectionWireframeTile, ISecurityTile {

    public TileEntityExtraIsotopicCentrifuge(ExtraMachineTier tier) {
        this();
        this.tier = tier;
    }
    private static final ISpecialSelectionWireframeTile.SelectionTransform[] SELECTION_ROTATE_SOUTH = {
            ISpecialSelectionWireframeTile.SelectionTransform.rotateY(180, 0.5D, 0.5D, 0.5D)
    };
    private static final ISpecialSelectionWireframeTile.SelectionTransform[] SELECTION_ROTATE_WEST = {
            ISpecialSelectionWireframeTile.SelectionTransform.rotateY(90, 0.5D, 0.5D, 0.5D)
    };
    private static final ISpecialSelectionWireframeTile.SelectionTransform[] SELECTION_ROTATE_EAST = {
            ISpecialSelectionWireframeTile.SelectionTransform.rotateY(270, 0.5D, 0.5D, 0.5D)
    };

    public static final int MAX_GAS = 10000;
    public ResizableGasTank inputTank;
    public ResizableGasTank outputTank;
    public IsotopicRecipe cachedRecipe;
    public double clientEnergyUsed;
    private int currentRedstoneLevel;
    public float prevScale;
    public int updateDelay;
    public boolean needsPacket;
    public boolean sorting;
    public ExtraMachineTier tier = ExtraMachineTier.ABSOLUTE;
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);
    private GasInventorySlot inputSlot;
    private GasInventorySlot outputSlot;
    private EnergyInventorySlot energySlot;

    public TileEntityExtraIsotopicCentrifuge() {
        super("washer", "TierIsotopicCentrifuge", 0, BlockStateMachine.MachineType.ISOTOPIC_CENTRIFUGE.getUsage(), 3, 1);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);
        initializeInventorySlots();
        configComponent.setupItemIOConfig(inputSlot, outputSlot, energySlot);
        configComponent.setConfig(TransmissionType.ITEM, DataType.INPUT, DataType.EMPTY, DataType.OUTPUT, DataType.ENERGY, DataType.INPUT, DataType.INPUT);
        configComponent.setCanEject(TransmissionType.ITEM, false);
        configComponent.setupIOConfig(TransmissionType.GAS, inputTank, outputTank, RelativeSide.FRONT, false, true);
        configComponent.setConfig(TransmissionType.GAS, DataType.INPUT, DataType.EMPTY, DataType.OUTPUT, DataType.INPUT, DataType.INPUT, DataType.INPUT);

        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS);
    }

        protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = createInventorySlotHelper();
        inputSlot = builder.addSlot(GasInventorySlot.fill(inputTank, listener, 5, 56));
        outputSlot = builder.addSlot(GasInventorySlot.drain(outputTank, listener, 155, 56));
        energySlot = builder.addSlot(EnergyInventorySlot.fillOrConvert(getMainEnergyContainer(), this::getWorld, listener, 155, 14));
        inputSlot.setSlotType(ContainerSlotType.INPUT);
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

        protected IGasTankHolder getInitialGasTanks(IContentsListener listener) {
        GasTankHelper builder = createGasTankHelper();
        builder.addTank(getOrCreateInputTank(listener));
        builder.addTank(getOrCreateOutputTank(listener));
        return builder.build();
    }

    private ResizableGasTank getOrCreateInputTank(IContentsListener listener) {
        if (inputTank == null) {
            inputTank = ResizableGasTank.input(MAX_GAS * tier.processes, gas -> RecipeHandler.Recipe.ISOTOPIC_CENTRIFUGE.containsRecipe(gas), getRecipeCacheListener());
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
        if (updateDelay > 0 && --updateDelay == 0) {
            needsPacket = true;
        }
        energySlot.fillContainerOrConvert();
        inputSlot.fillTank();
        outputSlot.drainTank();
        clientEnergyUsed = processRecipe(getMainEnergyContainer());
        finishRecipeTick();
    }

        protected boolean supportsAsyncIdleSkipping() {
        return getClass() == TileEntityExtraIsotopicCentrifuge.class;
    }

        protected boolean isAsyncUpdateIdle() {
        return inputTank.isEmpty() && outputTank.isEmpty() && inputSlot.isEmpty() && outputSlot.isEmpty() &&
              energySlot.isEmpty() && updateDelay == 0 && !needsPacket && clientEnergyUsed == 0 &&
              currentRedstoneLevel == getRedstoneLevel() && isEmptyRecipeStateSettled();
    }

    private void finishRecipeTick() {
        prevEnergy = getEnergy();
        int newRedstoneLevel = getRedstoneLevel();
        if (newRedstoneLevel != currentRedstoneLevel) {
            updateComparatorOutputLevelSync();
            currentRedstoneLevel = newRedstoneLevel;
        }
        if (needsPacket) {
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
        needsPacket = false;
    }

        public void setEnergy(double energy) {
        super.setEnergy(energy);
    }

        public void onUpdateClient() {
        if (updateDelay > 0) {
            updateDelay--;
            if (updateDelay == 0) {
                MekanismUtils.updateBlock(world, getPos());
            }
        }
        float targetScale = (float) (outputTank.getGas() != null ? outputTank.getGas().amount : 0) / outputTank.getMaxGas();
        if (Math.abs(prevScale - targetScale) > 0.01) {
            prevScale = (9 * prevScale + targetScale) / 10;
        }
    }

        public IsotopicRecipe getRecipe() {
        refreshRecipeLookupCache();
        GasInput input = getInput();
        if (cachedRecipe == null || !input.testEquality(cachedRecipe.getInput())) {
            cachedRecipe = RecipeHandler.getIsotopicRecipe(getInput());
        }
        return cachedRecipe;
    }

        protected void clearRecipeLookupCache() {
        super.clearRecipeLookupCache();
        cachedRecipe = null;
    }

        public GasInput getInput() {
        return new GasInput(inputTank.getGas());
    }

        public boolean canOperate(IsotopicRecipe recipe) {
        return recipe != null && recipe.canOperate(inputTank, outputTank);
    }

        public IsotopicRecipe getRecipe(int cacheIndex) {
        return getRecipe();
    }

        public CachedRecipe<IsotopicRecipe> createNewCachedRecipe(IsotopicRecipe recipe, int cacheIndex) {
        return new OneInputCachedRecipe<>(recipe, this::shouldRecheckAllRecipeErrors,
              InputHelper.getGasInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT),
              OutputHelper.getGasOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE),
              () -> recipe.getInput().ingredient, input -> input != null && input.isGasEqual(recipe.getInput().ingredient),
              input -> recipe.getOutput().output.copy(), input -> input == null || input.amount <= 0, output -> output == null || output.amount <= 0)
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

        public Map<GasInput, IsotopicRecipe> getRecipes() {
        return RecipeHandler.Recipe.ISOTOPIC_CENTRIFUGE.get();
    }

    public int getUpgradedUsage(IsotopicRecipe recipe) {
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
            inputTank.setMaxGas(tier.processes * MAX_GAS);
            outputTank.setMaxGas(tier.processes * MAX_GAS);
            clientEnergyUsed = dataStream.readDouble();
            TileUtils.readTankData(dataStream, inputTank);
            TileUtils.readTankData(dataStream, outputTank);
            if (updateDelay == 0) {
                updateDelay = MekanismConfig.current().general.UPDATE_DELAY.val();
                MekanismUtils.updateBlock(world, getPos());
            }
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
        TileUtils.addTankData(data, inputTank);
        TileUtils.addTankData(data, outputTank);
        return data;
    }

        public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        securityComponent.read(nbtTags);
        tier = ExtraMachineTier.byIndex(nbtTags.getInteger("tier"));
        sorting = nbtTags.getBoolean("sorting");
        inputTank.setMaxGas(tier.processes * MAX_GAS);
        outputTank.setMaxGas(tier.processes * MAX_GAS);
        inputTank.read(nbtTags.getCompoundTag("inputTank"));
        outputTank.read(nbtTags.getCompoundTag("outputTank"));
    }

        public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        securityComponent.write(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
        nbtTags.setBoolean("sorting", sorting);
        nbtTags.setTag("inputTank", inputTank.write(new NBTTagCompound()));
        nbtTags.setTag("outputTank", outputTank.write(new NBTTagCompound()));
    }

        public void writeSustainedData(ItemStack itemStack) {
        if (inputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "inputTank", inputTank.getGas().write(new NBTTagCompound()));
        }
        if (outputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "outputTank", outputTank.getGas().write(new NBTTagCompound()));
        }
    }

        public void readSustainedData(ItemStack itemStack) {
        inputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "inputTank")));
        outputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "outputTank")));
    }

        public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

        public Object[] getManagedTanks() {
        return new Object[]{inputTank, outputTank};
    }

        public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(inputTank.getStored(), inputTank.getMaxGas());
    }

    public boolean isComparatorInput(mekanism.api.transmitters.TransmissionType type) {
        return type == mekanism.api.transmitters.TransmissionType.GAS;
    }

        public void collectBoundingBlocks(java.util.function.BiConsumer<BlockPos, Boolean> consumer) {
        MachineStructureOffsets.collectOneAbove(getPos(), consumer);
    }

        public void onPlace() {
        tryPlaceBoundingBlocks(world, Coord4D.get(this));
    }

        public void onBreak() {
        removeBoundingBlocks(world, getPos());
    }

        public String[] getMethods() {
        return new String[0];
    }

        public Object[] invoke(int method, Object[] args) throws NoSuchMethodException {
        return new Object[0];
    }

        public void setActive(boolean active) {
        if (getActive() == active) {
            return;
        }
        super.setActive(active);
        if (updateDelay == 0) {
            Mekanism.packetHandler.sendUpdatePacket(this);
            updateDelay = 10;
        }
    }

    public double getTierEnergy() {
        return BlockStateMachine.MachineType.ISOTOPIC_CENTRIFUGE.getStorage() * tier.processes;
    }

        public int getBlockGuiID(Block block, int metadata) {
        return 2;
    }

        public IGuiProvider guiProvider() {
        return MEKCeuMoreMachine.proxy;
    }

        public boolean applyTierUpgrade(BaseTier upgradeTier) {
        if (!tier.canUpgradeTo(upgradeTier)) {
            return false;
        }
        tier = ExtraMachineTier.ABSOLUTE;
        inputTank.setMaxGas(tier.processes * MAX_GAS);
        outputTank.setMaxGas(tier.processes * MAX_GAS);
        upgradeComponent.getSupportedTypes().forEach(this::recalculateUpgradables);
        Mekanism.packetHandler.sendUpdatePacket(this);
        markNoUpdateSync();
        return true;
    }

            public double getMaxEnergy() {
        return upgradeComponent.isUpgradeInstalled(Upgrade.ENERGY) ? MekanismUtils.getMaxEnergy(this, getTierEnergy()) : getTierEnergy();
    }

    @Nonnull
        public String getName() {
        return getBlockType().getLocalizedName();
    }

        public ExtraMachineTier getTier() {
        return tier;
    }

        public boolean shouldDumpRadiation() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<?> getSelectionWireframeModelClass() {
        return mekceumoremachine.client.model.machine.ModelTierIsotopicCentrifuge.class;
    }

        public boolean shouldApplyDefaultSelectionWireframeFacingRotation(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

        public ISpecialSelectionWireframeTile.SelectionTransform[] getSelectionWireframeTransforms(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing currentFacing = facing == null ? EnumFacing.NORTH : facing;
        return switch (currentFacing) {
            case SOUTH -> SELECTION_ROTATE_SOUTH;
            case WEST -> SELECTION_ROTATE_WEST;
            case EAST -> SELECTION_ROTATE_EAST;
            default -> ISpecialSelectionWireframeTile.SelectionTransform.EMPTY;
        };
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
        return ExtraBlocks.getMoreMachineBlock("isotopic_centrifuge", extraTier);
    }
    public boolean isSorting() {
        return sorting;
    }
    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }
}
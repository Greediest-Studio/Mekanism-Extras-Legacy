package com.jerry.mekextras.common.content.reactor;

import com.jerry.mekextras.common.item.ItemNaquadahHohlraum;
import com.jerry.mekextras.common.registries.ExtraFluids;
import io.netty.buffer.ByteBuf;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.TileNetworkList;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.capabilities.tank.ValidatingGasTank;
import mekanism.common.multiblock.SynchronizedData;
import mekanism.common.util.TileUtils;
import mekanism.generators.common.slot.ReactorInventorySlot;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/** The naquadah reactor's shared structure state and Hohlraum ignition cycle. */
public class SynchronizedExtraReactorData extends SynchronizedData<SynchronizedExtraReactorData> implements IEnergyContainer {

    public static final int FUEL_CAPACITY = 1000;
    public static final int FUEL_PER_TICK = 1;
    public static final double BASE_OUTPUT = 2000000D;
    public static final double ENERGY_CAPACITY = 2000000000D;
    public static final double LASER_PER_CHARGE = 50000000D;
    public static final double IGNITION_TEMPERATURE = 400000000D;
    public static final double MAX_PLASMA_TEMPERATURE = 2000000000D;
    public static final int WATER_CAPACITY = 100000;
    public static final int STEAM_CAPACITY = 1000000;
    public static final double ENERGY_PER_REACTOR_FUEL = 10_000_000D;
    public static final double THERMOCOUPLE_EFFICIENCY = 0.1D;
    public static final double CASE_AIR_CONDUCTIVITY = 0.1D;
    public static final double PLASMA_CASE_CONDUCTIVITY = 0.2D;
    public static final double WATER_HEATING_RATIO = 0.3D;
    public static final double BURN_RATIO = 1D;
    public static final double STEAM_ENERGY_EFFICIENCY = 0.8D;
    public static final double WATER_THERMAL_ENTHALPY = 40D;
    public static final float REACTOR_DAMAGE = 50_000F;
    private static final double CASING_HEAT_CAPACITY = 1D;
    private static final double CASE_INVERSE_INSULATION = 100_000D;

    public final ValidatingGasTank richNaquadahTank = new ValidatingGasTank(FUEL_CAPACITY,
          gas -> gas == ExtraFluids.RICH_NAQUADAH_FUEL, this::isFormed);
    public final ValidatingGasTank fuelTank = new ValidatingGasTank(FUEL_CAPACITY,
          gas -> gas == ExtraFluids.NAQUADAH_URANIUM_FUEL || gas == ExtraFluids.FLUORINATED_NAQUADAH_URANIUM_FUEL,
          this::isFormed);
    public final ValidatingGasTank richUraniumTank = new ValidatingGasTank(FUEL_CAPACITY,
          gas -> gas == ExtraFluids.RICH_URANIUM_FUEL, this::isFormed);
    public final BasicFluidTank waterTank = BasicFluidTank.input(WATER_CAPACITY, stack -> stack.getFluid() == FluidRegistry.WATER, this::isFormed);
    public final ValidatingGasTank steamTank = new ValidatingGasTank(STEAM_CAPACITY, gas -> gas == ExtraFluids.POLONIUM_CONTAINING_STEAM, this::isFormed);
    public final ReactorInventorySlot hohlraumSlot;
    private final VariableHeatCapacitor casingHeatCapacitor;

    public double energy;
    public double laserBuffer;
    public int laserCharges;
    public double lastOutput;
    public double plasmaTemperature;
    public boolean burning;
    public int injectionRate = 2;

    public SynchronizedExtraReactorData() {
        gasTanks.add(richNaquadahTank);
        gasTanks.add(fuelTank);
        gasTanks.add(richUraniumTank);
        gasTanks.add(steamTank);
        fluidTanks.add(waterTank);
        energyContainers.add(this);
        casingHeatCapacitor = VariableHeatCapacitor.create(CASING_HEAT_CAPACITY,
              () -> 1D / Math.max(CASE_AIR_CONDUCTIVITY, 0.000001D), () -> CASE_INVERSE_INSULATION,
              () -> HeatAPI.AMBIENT_TEMP, null);
        heatHandlers.add(casingHeatCapacitor);
        hohlraumSlot = ReactorInventorySlot.at(stack -> stack.getItem() instanceof ItemNaquadahHohlraum, null, 85, 39);
        inventorySlots.add(hohlraumSlot);
    }

    public void addLaserEnergy(double amount) {
        int previousCharges = laserCharges;
        laserBuffer += Math.max(0, amount);
        while (laserBuffer >= LASER_PER_CHARGE && laserCharges < 64) {
            laserBuffer -= LASER_PER_CHARGE;
            laserCharges++;
        }
        int addedCharges = laserCharges - previousCharges;
        if (addedCharges > 0) {
            plasmaTemperature = Math.min(MAX_PLASMA_TEMPERATURE,
                  plasmaTemperature + addedCharges * LASER_PER_CHARGE);
        }
    }

    /**
     * Rebuilds the minimum plasma temperature represented by stored laser charges when a
     * structure is restored. Laser charges and temperature are persisted separately, so older
     * saves or an interrupted save can otherwise restore the charges while leaving the reactor
     * permanently cold.
     */
    public void restoreLaserChargeTemperature() {
        double chargeTemperature = Math.min(MAX_PLASMA_TEMPERATURE,
              Math.max(0, laserCharges) * LASER_PER_CHARGE);
        plasmaTemperature = Math.max(plasmaTemperature, chargeTemperature);
        updateCasingHeat();
    }

    public boolean isBurning() {
        return isFormed() && burning;
    }

    public boolean hasReadyHohlraum() {
        return !hohlraumSlot.isEmpty() && hohlraumSlot.getStack().getItem() instanceof ItemNaquadahHohlraum hohlraum &&
              hohlraum.isReadyForReaction(hohlraumSlot.getStack());
    }

    public void tick(World world) {
        sanitizeTankContents();
        if (!isFormed()) {
            burning = false;
            lastOutput = 0;
            return;
        }
        if (burning) {
            killEntitiesInside(world);
            injectFuel();
            if (!burnFromFuel()) {
                burning = false;
                lastOutput = 0;
            }
        }
        if (!burning) {
            // Test the ignition line before passive cooling so a charge that lands exactly on
            // 400 MK is still able to vaporise the Hohlraum on the following tick.
            if (plasmaTemperature >= IGNITION_TEMPERATURE && hasReadyHohlraum()) {
                igniteHohlraum();
            }
            if (!burning) {
                plasmaTemperature = Math.max(0, plasmaTemperature - 1000000D);
            }
        }
        updateCasingHeat();
    }

    private void injectFuel() {
        int amountNeeded = fuelTank.getNeeded();
        int amountAvailable = 2 * Math.min(richNaquadahTank.getStored(), richUraniumTank.getStored());
        int amountToInject = Math.min(amountNeeded, Math.min(amountAvailable, Math.max(0, injectionRate)));
        amountToInject -= amountToInject % 2;
        if (amountToInject <= 0) {
            return;
        }
        int half = amountToInject / 2;
        GasStack richNaquadah = richNaquadahTank.extract(half, Action.EXECUTE, AutomationType.INTERNAL);
        GasStack richUranium = richUraniumTank.extract(half, Action.EXECUTE, AutomationType.INTERNAL);
        if (richNaquadah == null || richUranium == null || richNaquadah.amount <= 0 || richUranium.amount <= 0) {
            return;
        }
        int consumed = Math.min(richNaquadah.amount, richUranium.amount);
        if (richNaquadah.amount > consumed) {
            richNaquadahTank.insert(richNaquadah.withAmount(richNaquadah.amount - consumed), Action.EXECUTE, AutomationType.INTERNAL);
        }
        if (richUranium.amount > consumed) {
            richUraniumTank.insert(richUranium.withAmount(richUranium.amount - consumed), Action.EXECUTE, AutomationType.INTERNAL);
        }
        fuelTank.insert(new GasStack(ExtraFluids.NAQUADAH_URANIUM_FUEL, consumed * 2), Action.EXECUTE, AutomationType.INTERNAL);
    }

    private boolean burnFromFuel() {
        if (fuelTank.getStored() <= 0 || plasmaTemperature < IGNITION_TEMPERATURE ||
              injectionRate < getMinInjectionRate(hasCoolingWater())) {
            return false;
        }
        GasStack drained = fuelTank.extract(Math.max(1, injectionRate), Action.EXECUTE, AutomationType.INTERNAL);
        if (drained == null || drained.amount <= 0) {
            return false;
        }
        double multiplier = 1 + laserCharges * 0.5D;
        if (laserCharges > 0) {
            laserCharges--;
        }
        double produced = Math.min(BASE_OUTPUT * multiplier * drained.amount, Math.max(0, ENERGY_CAPACITY - energy));
        energy += produced;
        lastOutput = produced;
        updatePlasmaTemperature();
        vaporizeWater();
        return true;
    }

    private void igniteHohlraum() {
        ItemStack stack = hohlraumSlot.getStack();
        if (!(stack.getItem() instanceof ItemNaquadahHohlraum hohlraum)) {
            return;
        }
        GasStack fuel = hohlraum.getContainedGas(stack);
        if (fuel == null || !hohlraum.isReadyForReaction(stack)) {
            return;
        }
        fuelTank.insert(fuel, Action.EXECUTE, AutomationType.INTERNAL);
        // A Hohlraum is an ignition catalyst, matching Mekanism's reactor behaviour. Existing
        // fuel in the shared tank may be a different valid reactor fuel, and a full tank must not
        // leave a ready Hohlraum permanently stuck in the ignition slot.
        if (fuelTank.getStored() > 0) {
            hohlraumSlot.setEmpty();
            burning = true;
        }
    }

    private void updatePlasmaTemperature() {
        boolean waterCooled = hasCoolingWater();
        double target = getMaxPlasmaTemperature(waterCooled);
        double response = Math.min(0.1D, 0.04D + Math.max(1, injectionRate) / 2000D);
        plasmaTemperature = HeatAPI.sanitizeTemperature(
              plasmaTemperature + (target - plasmaTemperature) * response);
    }

    private void vaporizeWater() {
        int available = waterTank.getFluidAmount();
        if (available <= 0 || steamTank.getNeeded() <= 0) {
            return;
        }
        double temperatureDifference = Math.max(0D, getCaseTemperature() - HeatAPI.AMBIENT_TEMP);
        double caseWaterHeat = WATER_HEATING_RATIO * temperatureDifference;
        double heatLimited = STEAM_ENERGY_EFFICIENCY * caseWaterHeat / WATER_THERMAL_ENTHALPY;
        int heatLimitedAmount = heatLimited <= 0D || !Double.isFinite(heatLimited) ? 0 :
              (int) Math.min(Integer.MAX_VALUE, Math.floor(heatLimited));
        int amount = Math.min(Math.max(0, injectionRate), Math.min(heatLimitedAmount, Math.min(available, steamTank.getNeeded())));
        if (amount <= 0) {
            return;
        }
        FluidStack drained = waterTank.extract(amount, Action.EXECUTE, AutomationType.INTERNAL);
        if (drained != null && drained.amount > 0) {
            steamTank.insert(new GasStack(ExtraFluids.POLONIUM_CONTAINING_STEAM, drained.amount), Action.EXECUTE, AutomationType.INTERNAL);
        }
    }

    private void killEntitiesInside(World world) {
        if (world == null || world.isRemote || minLocation == null || maxLocation == null) {
            return;
        }
        AxisAlignedBB interior = new AxisAlignedBB(
              minLocation.x + 1, minLocation.y + 1, minLocation.z + 1,
              maxLocation.x, maxLocation.y, maxLocation.z);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, interior);
        for (Entity entity : entities) {
            entity.attackEntityFrom(DamageSource.MAGIC, REACTOR_DAMAGE);
        }
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double value) {
        energy = Math.max(0, Math.min(value, ENERGY_CAPACITY));
    }

    @Override
    public double getMaxEnergy() {
        return ENERGY_CAPACITY;
    }

    public double getLastOutput() {
        return lastOutput;
    }

    public int getLaserCharges() {
        return laserCharges;
    }

    public double getPlasmaTemperature() {
        return plasmaTemperature;
    }

    private double getCoolingConductivity(boolean active) {
        return active ? WATER_HEATING_RATIO : 0D;
    }

    private boolean hasCoolingWater() {
        return waterTank.getFluidAmount() > 0;
    }

    public int getMinInjectionRate(boolean active) {
        double cooling = getCoolingConductivity(active);
        double denominator = ENERGY_PER_REACTOR_FUEL * BURN_RATIO *
              (PLASMA_CASE_CONDUCTIVITY + cooling + CASE_AIR_CONDUCTIVITY) -
              PLASMA_CASE_CONDUCTIVITY * (cooling + CASE_AIR_CONDUCTIVITY);
        if (denominator <= 0) {
            return Integer.MAX_VALUE - 1;
        }
        double minimum = IGNITION_TEMPERATURE * BURN_RATIO * PLASMA_CASE_CONDUCTIVITY *
              (cooling + CASE_AIR_CONDUCTIVITY) / denominator;
        return Math.max(0, 2 * (int) Math.ceil(minimum / 2D));
    }

    public double getMaxPlasmaTemperature(boolean active) {
        double cooling = getCoolingConductivity(active);
        double denominator = cooling + CASE_AIR_CONDUCTIVITY;
        if (denominator <= 0) {
            return MAX_PLASMA_TEMPERATURE;
        }
        int rate = Math.max(1, injectionRate);
        return Math.min(MAX_PLASMA_TEMPERATURE, rate * ENERGY_PER_REACTOR_FUEL / PLASMA_CASE_CONDUCTIVITY *
              (PLASMA_CASE_CONDUCTIVITY + denominator) / denominator);
    }

    public double getMaxCasingTemperature(boolean active) {
        double cooling = getCoolingConductivity(active);
        double denominator = cooling + CASE_AIR_CONDUCTIVITY;
        if (denominator <= 0) {
            return MAX_PLASMA_TEMPERATURE;
        }
        int rate = Math.max(1, injectionRate);
        return Math.min(MAX_PLASMA_TEMPERATURE, rate * ENERGY_PER_REACTOR_FUEL / denominator);
    }

    public double getCaseTemperature() {
        return HeatAPI.sanitizeTemperature(casingHeatCapacitor.getTemperature());
    }

    private void updateCasingHeat() {
        double target = getEquilibriumCaseTemperature();
        casingHeatCapacitor.setHeat(HeatAPI.multiplyHeat(target, casingHeatCapacitor.getHeatCapacity()));
    }

    private double getEquilibriumCaseTemperature() {
        boolean activeCooling = hasCoolingWater();
        return Math.max(0D, Math.min(plasmaTemperature, getMaxCasingTemperature(activeCooling)));
    }

    public VariableHeatCapacitor getHeatCapacitor() {
        return casingHeatCapacitor;
    }

    @Nonnull
    public List<IHeatCapacitor> getHeatCapacitors(EnumFacing side) {
        return isFormed() ? Collections.singletonList(casingHeatCapacitor) : Collections.emptyList();
    }

    public void setPlasmaTemperature(double temperature) {
        plasmaTemperature = HeatAPI.sanitizeTemperature(Math.max(0D, Math.min(temperature, MAX_PLASMA_TEMPERATURE)));
        updateCasingHeat();
    }

    public static int sanitizeInjectionRate(int rate) {
        int even = Math.round(rate / 2F) * 2;
        return Math.max(2, Math.min(98, even));
    }

    public void setInjectionRate(int rate) {
        injectionRate = sanitizeInjectionRate(rate);
    }

    public double getLastTransferLoss() {
        return PLASMA_CASE_CONDUCTIVITY * Math.max(0D, plasmaTemperature - getCaseTemperature());
    }

    public double getLastEnvironmentLoss() {
        return CASE_AIR_CONDUCTIVITY * getCaseTemperature();
    }

    public double getPassiveGeneration(boolean active, boolean current) {
        double temperature = current ? getCaseTemperature() : getMaxCasingTemperature(active);
        return THERMOCOUPLE_EFFICIENCY * CASE_AIR_CONDUCTIVITY * Math.max(0D, temperature);
    }

    public int getSteamPerTick(boolean current) {
        double temperature = current ? getCaseTemperature() : getMaxCasingTemperature(true);
        double steam = STEAM_ENERGY_EFFICIENCY * WATER_HEATING_RATIO * Math.max(0D, temperature) /
              WATER_THERMAL_ENTHALPY;
        return steam <= 0D || !Double.isFinite(steam) ? 0 : (int) Math.min(Integer.MAX_VALUE, Math.floor(steam));
    }

    public TileNetworkList addStructureData(TileNetworkList data) {
        data.add(energy);
        data.add(lastOutput);
        data.add(laserCharges);
        TileUtils.addTankData(data, richNaquadahTank);
        TileUtils.addTankData(data, fuelTank);
        TileUtils.addTankData(data, richUraniumTank);
        data.add(burning);
        data.add(plasmaTemperature);
        data.add(injectionRate);
        data.add(getCaseTemperature());
        TileUtils.addTankData(data, waterTank);
        TileUtils.addTankData(data, steamTank);
        return data;
    }

    public void readStructureData(ByteBuf dataStream) {
        try {
            energy = dataStream.readDouble();
            lastOutput = dataStream.readDouble();
            laserCharges = dataStream.readInt();
            TileUtils.readTankData(dataStream, richNaquadahTank);
            TileUtils.readTankData(dataStream, fuelTank);
            TileUtils.readTankData(dataStream, richUraniumTank);
            burning = dataStream.readBoolean();
            plasmaTemperature = dataStream.readDouble();
            injectionRate = sanitizeInjectionRate(dataStream.readInt());
            double caseTemperature = dataStream.readDouble();
            double caseCapacity = casingHeatCapacitor.getHeatCapacity();
            casingHeatCapacitor.setHeat(HeatAPI.multiplyHeat(caseTemperature, caseCapacity));
            TileUtils.readTankData(dataStream, waterTank);
            TileUtils.readTankData(dataStream, steamTank);
            sanitizeTankContents();
        } catch (Exception ignored) {
        }
    }

    private void sanitizeTankContents() {
        GasStack richNaquadah = richNaquadahTank.getGas();
        if (richNaquadah != null && richNaquadah.getGas() != ExtraFluids.RICH_NAQUADAH_FUEL) {
            richNaquadahTank.setGas(null);
        }
        GasStack fuel = fuelTank.getGas();
        if (fuel != null && fuel.getGas() != ExtraFluids.NAQUADAH_URANIUM_FUEL &&
              fuel.getGas() != ExtraFluids.FLUORINATED_NAQUADAH_URANIUM_FUEL) {
            fuelTank.setGas(null);
        }
        GasStack richUranium = richUraniumTank.getGas();
        if (richUranium != null && richUranium.getGas() != ExtraFluids.RICH_URANIUM_FUEL) {
            richUraniumTank.setGas(null);
        }
        FluidStack water = waterTank.getFluid();
        if (water != null && water.getFluid() != FluidRegistry.WATER) {
            waterTank.setFluid(null);
        }
        GasStack steam = steamTank.getGas();
        if (steam != null && steam.getGas() != ExtraFluids.POLONIUM_CONTAINING_STEAM) {
            steamTank.setGas(null);
        }
    }
}

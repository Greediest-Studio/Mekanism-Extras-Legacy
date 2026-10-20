package com.jerry.mekextras.common.content.reactor;

import mekanism.api.gas.GasStack;
import mekanism.api.heat.HeatAPI;
import mekanism.common.multiblock.MultiblockCache;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

public class ExtraReactorCache extends MultiblockCache<SynchronizedExtraReactorData> {

    public GasStack richNaquadahFuel;
    public GasStack fuel;
    public GasStack richUraniumFuel;
    public GasStack steam;
    public FluidStack water;
    public double energy;
    public double lastOutput;
    public double laserBuffer;
    public double plasmaTemperature;
    public double caseTemperature = HeatAPI.AMBIENT_TEMP;
    public int laserCharges;
    public boolean burning;
    public int injectionRate = 2;
    private boolean persistCaseTemperature = true;

    @Override
    public void validateMerge(MultiblockCache<SynchronizedExtraReactorData> incoming) throws IOException {
        super.validateMerge(incoming);
        ExtraReactorCache other = (ExtraReactorCache) incoming;
        validateGasMerge(richNaquadahFuel, other.richNaquadahFuel);
        validateGasMerge(fuel, other.fuel);
        validateGasMerge(richUraniumFuel, other.richUraniumFuel);
        validateGasMerge(steam, other.steam);
        validateFluidMerge(water, other.water);
        validateSum(energy, other.energy, SynchronizedExtraReactorData.ENERGY_CAPACITY, "reactor energy");
        validateSum(Math.max(0, laserBuffer), Math.max(0, other.laserBuffer), Double.MAX_VALUE, "laser buffer");
        validateSum(Math.max(0, plasmaTemperature), Math.max(0, other.plasmaTemperature),
              SynchronizedExtraReactorData.MAX_PLASMA_TEMPERATURE, "plasma temperature");
    }

    @Override
    public void validateCapacity(SynchronizedExtraReactorData target) throws IOException {
        super.validateCapacity(target);
        requireMerge(richNaquadahFuel == null || richNaquadahFuel.amount <= target.richNaquadahTank.getCapacity(),
              "Rich naquadah fuel exceeds target capacity");
        requireMerge(fuel == null || fuel.amount <= target.fuelTank.getCapacity(), "Reactor fuel exceeds target capacity");
        requireMerge(richUraniumFuel == null || richUraniumFuel.amount <= target.richUraniumTank.getCapacity(),
              "Rich uranium fuel exceeds target capacity");
        requireMerge(steam == null || steam.amount <= target.steamTank.getCapacity(), "Reactor steam exceeds target capacity");
        requireMerge(water == null || water.amount <= target.waterTank.getCapacity(), "Reactor water exceeds target capacity");
    }

    @Override
    public void apply(SynchronizedExtraReactorData data) {
        applyInventory(data);
        data.richNaquadahTank.setGas(limitGas(richNaquadahFuel, data.richNaquadahTank.getCapacity()));
        data.fuelTank.setGas(limitGas(fuel, data.fuelTank.getCapacity()));
        data.richUraniumTank.setGas(limitGas(richUraniumFuel, data.richUraniumTank.getCapacity()));
        data.steamTank.setGas(steam == null ? null : steam.copy());
        data.waterTank.setFluid(water == null ? null : water.copy());
        data.energy = sanitize(energy, 0, SynchronizedExtraReactorData.ENERGY_CAPACITY);
        data.lastOutput = sanitize(lastOutput, 0, SynchronizedExtraReactorData.BASE_OUTPUT * 32D);
        data.laserBuffer = sanitize(laserBuffer, 0, Double.MAX_VALUE);
        data.plasmaTemperature = sanitize(plasmaTemperature, 0, SynchronizedExtraReactorData.MAX_PLASMA_TEMPERATURE);
        data.laserCharges = Math.max(0, Math.min(64, laserCharges));
        data.setInjectionRate(injectionRate);
        data.restoreLaserChargeTemperature();
        data.burning = burning && data.fuelTank.getStored() > 0 && data.plasmaTemperature >= SynchronizedExtraReactorData.IGNITION_TEMPERATURE;
        data.getHeatCapacitor().setHeat(HeatAPI.multiplyHeat(sanitize(caseTemperature, 0, HeatAPI.MAX_HEAT),
              data.getHeatCapacitor().getHeatCapacity()));
    }

    @Override
    public void sync(SynchronizedExtraReactorData data) {
        syncInventory(data);
        richNaquadahFuel = syncGasStack(richNaquadahFuel, data.richNaquadahTank.getGas());
        fuel = syncGasStack(fuel, data.fuelTank.getGas());
        richUraniumFuel = syncGasStack(richUraniumFuel, data.richUraniumTank.getGas());
        steam = syncGasStack(steam, data.steamTank.getGas());
        water = syncFluidStack(water, data.waterTank.getFluid());
        energy = data.energy;
        lastOutput = data.lastOutput;
        laserBuffer = data.laserBuffer;
        plasmaTemperature = data.plasmaTemperature;
        caseTemperature = data.getCaseTemperature();
        persistCaseTemperature = true;
        laserCharges = data.laserCharges;
        burning = data.burning;
        injectionRate = data.injectionRate;
    }

    private static GasStack limitGas(GasStack stack, int capacity) {
        if (stack == null) {
            return null;
        }
        return stack.copy().withAmount(Math.min(Math.max(0, stack.amount), capacity));
    }

    private static double sanitize(double value, double minimum, double maximum) {
        return Double.isFinite(value) ? Math.max(minimum, Math.min(value, maximum)) : minimum;
    }

    @Override
    public void load(NBTTagCompound nbtTags) {
        loadInventory(nbtTags);
        richNaquadahFuel = nbtTags.hasKey("reactorRichNaquadahFuel") ?
              GasStack.readFromNBT(nbtTags.getCompoundTag("reactorRichNaquadahFuel")) : null;
        richUraniumFuel = nbtTags.hasKey("reactorRichUraniumFuel") ?
              GasStack.readFromNBT(nbtTags.getCompoundTag("reactorRichUraniumFuel")) : null;
        GasStack storedFuel = nbtTags.hasKey("reactorFuel") ? GasStack.readFromNBT(nbtTags.getCompoundTag("reactorFuel")) : null;
        boolean splitFuelFormat = nbtTags.hasKey("reactorRichNaquadahFuel") || nbtTags.hasKey("reactorRichUraniumFuel");
        if (splitFuelFormat || storedFuel == null) {
            fuel = storedFuel;
        } else if (storedFuel.getGas() == com.jerry.mekextras.common.registries.ExtraFluids.RICH_NAQUADAH_FUEL) {
            richNaquadahFuel = storedFuel;
            fuel = null;
        } else if (storedFuel.getGas() == com.jerry.mekextras.common.registries.ExtraFluids.RICH_URANIUM_FUEL) {
            richUraniumFuel = storedFuel;
            fuel = null;
        } else {
            fuel = storedFuel;
        }
        richNaquadahFuel = limitGas(richNaquadahFuel, SynchronizedExtraReactorData.FUEL_CAPACITY);
        fuel = limitGas(fuel, SynchronizedExtraReactorData.FUEL_CAPACITY);
        richUraniumFuel = limitGas(richUraniumFuel, SynchronizedExtraReactorData.FUEL_CAPACITY);
        steam = nbtTags.hasKey("reactorSteam") ? GasStack.readFromNBT(nbtTags.getCompoundTag("reactorSteam")) : null;
        water = nbtTags.hasKey("reactorWater") ? FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("reactorWater")) : null;
        energy = nbtTags.getDouble("reactorEnergy");
        lastOutput = nbtTags.getDouble("reactorLastOutput");
        laserBuffer = nbtTags.getDouble("reactorLaserBuffer");
        plasmaTemperature = nbtTags.getDouble("reactorPlasmaTemperature");
        persistCaseTemperature = nbtTags.hasKey("reactorCaseTemperature");
        caseTemperature = persistCaseTemperature ? nbtTags.getDouble("reactorCaseTemperature") : HeatAPI.AMBIENT_TEMP;
        laserCharges = nbtTags.getInteger("reactorLaserCharges");
        burning = nbtTags.getBoolean("reactorBurning");
        injectionRate = nbtTags.hasKey("reactorInjectionRate") ? nbtTags.getInteger("reactorInjectionRate") : 2;
    }

    @Override
    public void save(NBTTagCompound nbtTags) {
        saveInventory(nbtTags);
        if (richNaquadahFuel != null) {
            nbtTags.setTag("reactorRichNaquadahFuel", richNaquadahFuel.write(new NBTTagCompound()));
        }
        if (fuel != null) {
            nbtTags.setTag("reactorFuel", fuel.write(new NBTTagCompound()));
        }
        if (richUraniumFuel != null) {
            nbtTags.setTag("reactorRichUraniumFuel", richUraniumFuel.write(new NBTTagCompound()));
        }
        if (steam != null) {
            nbtTags.setTag("reactorSteam", steam.write(new NBTTagCompound()));
        }
        if (water != null) {
            nbtTags.setTag("reactorWater", water.writeToNBT(new NBTTagCompound()));
        }
        nbtTags.setDouble("reactorEnergy", sanitize(energy, 0, SynchronizedExtraReactorData.ENERGY_CAPACITY));
        nbtTags.setDouble("reactorLastOutput", sanitize(lastOutput, 0, SynchronizedExtraReactorData.BASE_OUTPUT * 32D));
        nbtTags.setDouble("reactorLaserBuffer", sanitize(laserBuffer, 0, Double.MAX_VALUE));
        nbtTags.setDouble("reactorPlasmaTemperature", sanitize(plasmaTemperature, 0, SynchronizedExtraReactorData.MAX_PLASMA_TEMPERATURE));
        if (persistCaseTemperature) {
            nbtTags.setDouble("reactorCaseTemperature", caseTemperature);
        }
        nbtTags.setInteger("reactorLaserCharges", Math.max(0, Math.min(64, laserCharges)));
        nbtTags.setBoolean("reactorBurning", burning);
        if (injectionRate != 2) {
            nbtTags.setInteger("reactorInjectionRate", injectionRate);
        }
    }
}

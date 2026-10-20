package com.jerry.mekextras.common.tile.multiblock;

import mekanism.api.EnumColor;
import mekanism.api.IConfigurable;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

/**
 * The lead coated laser focus matrix: collects laser energy for the reactor structure.
 */
public class TileEntityExtraReactorLaserFocusMatrix extends TileEntityExtraReactorCasing
      implements ILaserReceptor, IConfigurable {

    @Override
    public void receiveLaserEnergy(double energy, EnumFacing side) {
        if (structure != null && Double.isFinite(energy) && energy > 0) {
            structure.addLaserEnergy(energy);
        }
    }

    @Override
    public boolean canLasersDig() {
        return false;
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        return creativeHeat(player);
    }

    @Override
    public EnumActionResult onRightClick(EntityPlayer player, EnumFacing side) {
        return creativeHeat(player);
    }

    private EnumActionResult creativeHeat(EntityPlayer player) {
        if (isRemote() || !player.isCreative()) {
            return EnumActionResult.PASS;
        }
        if (structure == null || !structure.isFormed()) {
            return EnumActionResult.PASS;
        }
        structure.setPlasmaTemperature(1_000_000_000D);
        player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.GREY +
              LangUtils.localize("reactor.laser.creative_heat")));
        Mekanism.packetHandler.sendUpdatePacket(this);
        markNoUpdateSync();
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        return capability == Capabilities.LASER_RECEPTOR_CAPABILITY || capability == Capabilities.CONFIGURABLE_CAPABILITY ||
              super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (capability == Capabilities.LASER_RECEPTOR_CAPABILITY) {
            return Capabilities.LASER_RECEPTOR_CAPABILITY.cast(this);
        }
        if (capability == Capabilities.CONFIGURABLE_CAPABILITY) {
            return Capabilities.CONFIGURABLE_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }
}
package com.jerry.mekextras.common.tile.multiblock;

import com.jerry.mekextras.common.content.reactor.SynchronizedExtraReactorData;
import io.netty.buffer.ByteBuf;
import mekanism.api.EnumColor;
import mekanism.api.IConfigurable;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

/**
 * Redstone logic adapter for the naquadah reactor.
 */
public class TileEntityExtraReactorLogicAdapter extends TileEntityExtraReactorCasing
      implements IActiveState, IConfigurable, IComparatorSupport {

    private ReactorLogic logicMode = ReactorLogic.DISABLED;
    private int currentRedstoneLevel;

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        int newRedstoneLevel = getRedstoneLevel();
        if (newRedstoneLevel != currentRedstoneLevel) {
            currentRedstoneLevel = newRedstoneLevel;
            world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
            updateComparatorOutputLevelSync();
        }
    }

    public boolean checkMode() {
        if (structure == null || !structure.isFormed()) {
            return false;
        }
        return switch (logicMode) {
            case DISABLED -> false;
            case READY -> structure.getPlasmaTemperature() >= SynchronizedExtraReactorData.IGNITION_TEMPERATURE;
            case CAPACITY -> structure.getEnergy() >= structure.getMaxEnergy();
            case DEPLETED -> structure.fuelTank.getStored() == 0;
        };
    }

    @Override
    public int getRedstoneLevel() {
        return checkMode() ? 15 : 0;
    }

    public int getRedstoneLevel(EnumFacing side) {
        return getRedstoneLevel();
    }

    public String getStatusTranslationKey() {
        return checkMode() ? "gui.outputting" : "gui.idle";
    }

    public ReactorLogic getLogicMode() {
        return logicMode;
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        if (!isRemote()) {
            logicMode = logicMode.next();
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.GREY +
                  LangUtils.localize("reactor.logic.mode") + ": " + EnumColor.AQUA + logicMode.getLabel()));
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
        return checkMode();
    }

    @Override
    public void setActive(boolean active) {
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
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        logicMode = ReactorLogic.byIndex(nbtTags.getInteger("reactorLogicMode"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("reactorLogicMode", logicMode.ordinal());
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(logicMode.ordinal());
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int type = dataStream.readInt();
            if (type == 0) {
                logicMode = ReactorLogic.byIndex(dataStream.readInt());
                Mekanism.packetHandler.sendUpdatePacket(this);
                markNoUpdateSync();
            }
            return;
        }
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            logicMode = ReactorLogic.byIndex(dataStream.readInt());
        }
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.UP && facing != EnumFacing.DOWN;
    }

    @Nonnull
    @Override
    public String getName() {
        return getBlockType().getLocalizedName();
    }

    public enum ReactorLogic {
        DISABLED("reactor.logic.disabled", "reactor.logic.disabled.desc", new ItemStack(Items.GUNPOWDER)),
        READY("reactor.logic.ready", "reactor.logic.ready.desc", new ItemStack(Items.REDSTONE)),
        CAPACITY("reactor.logic.capacity", "reactor.logic.capacity.desc", new ItemStack(Items.REDSTONE)),
        DEPLETED("reactor.logic.depleted", "reactor.logic.depleted.desc", new ItemStack(Items.REDSTONE));

        private static final ReactorLogic[] MODES = values();
        private final String labelKey;
        private final String descriptionKey;
        private final ItemStack renderStack;

        ReactorLogic(String labelKey, String descriptionKey, ItemStack renderStack) {
            this.labelKey = labelKey;
            this.descriptionKey = descriptionKey;
            this.renderStack = renderStack;
        }

        public String getLabel() {
            return LangUtils.localize(labelKey);
        }

        public String getDescription() {
            return LangUtils.localize(descriptionKey);
        }

        public ItemStack getRenderStack() {
            return renderStack;
        }

        public ReactorLogic next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        public static ReactorLogic byIndex(int index) {
            return index < 0 || index >= MODES.length ? DISABLED : MODES[index];
        }
    }
}

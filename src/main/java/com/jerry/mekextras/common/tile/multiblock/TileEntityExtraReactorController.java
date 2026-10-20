package com.jerry.mekextras.common.tile.multiblock;

import io.netty.buffer.ByteBuf;
import mekanism.api.IConfigurable;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * The reactor controller. Its visual active state is synchronized independently from the
 * shared multiblock packet so every component receives the same formed state.
 */
public class TileEntityExtraReactorController extends TileEntityExtraReactorCasing implements IConfigurable, IActiveState {

    private boolean formedVisual;
    private boolean burningVisual;

    public void setInjectionRate(int rate) {
        if (structure != null) {
            structure.setInjectionRate(rate);
            Mekanism.packetHandler.sendUpdatePacket(this);
            markNoUpdateSync();
        }
    }

    public int getComparatorLevel() {
        double max = getMaxEnergy();
        return max <= 0 ? 0 : Math.max(0, Math.min(15, (int) Math.ceil(15 * getEnergy() / max)));
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        boolean formed = isFormed();
        if (formedVisual != formed) {
            formedVisual = formed;
            markNoUpdateSync();
        }
        boolean burning = isBurning();
        if (burningVisual != burning) {
            burningVisual = burning;
            markNoUpdateSync();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-4, -8, -4), getPos().add(5, 1, 5));
    }

    @Override
    public EnumActionResult onSneakRightClick(EntityPlayer player, EnumFacing side) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onRightClick(EntityPlayer player, EnumFacing side) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean getActive() {
        return formedVisual;
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
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(formedVisual);
        data.add(getInjectionRate());
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int type = dataStream.readInt();
            if (type == 1 && structure != null) {
                structure.setInjectionRate(dataStream.readInt());
                Mekanism.packetHandler.sendUpdatePacket(this);
                markNoUpdateSync();
            }
            return;
        }
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            formedVisual = dataStream.readBoolean();
            if (structure != null) {
                structure.setInjectionRate(dataStream.readInt());
            } else {
                dataStream.readInt();
            }
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
}

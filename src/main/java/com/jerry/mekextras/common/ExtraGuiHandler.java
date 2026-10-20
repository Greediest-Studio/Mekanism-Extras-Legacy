package com.jerry.mekextras.common;

import com.jerry.mekextras.MekanismExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ExtraGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return MekanismExtras.proxy.getServerGui(ID, player, world, new BlockPos(x, y, z));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return MekanismExtras.proxy.getClientGui(ID, player, world, new BlockPos(x, y, z));
    }
}

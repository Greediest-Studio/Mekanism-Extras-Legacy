package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter.ReactorLogic;
import mekanism.api.EnumColor;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiElementHolder;
import mekanism.client.gui.element.scroll.GuiScrollBar;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.generators.client.gui.element.button.ReactorLogicButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorLogicAdapter extends GuiMekanismTile<TileEntityExtraReactorLogicAdapter, ContainerNull> {

    private static final int DISPLAY_COUNT = 4;
    private GuiScrollBar scrollBar;

    public GuiExtraReactorLogicAdapter(InventoryPlayer inventory, TileEntityExtraReactorLogicAdapter tile) {
        super(tile, new ContainerNull(inventory.player, tile));
        xSize += 20;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addButton(new GuiElementHolder(this, 26, 31, 130, 90));
        scrollBar = addButton(new GuiScrollBar(this, 156, 31, 90, () -> ReactorLogic.values().length, () -> DISPLAY_COUNT));
        for (int i = 0; i < DISPLAY_COUNT; i++) {
            addButton(new ReactorLogicButton<>(this, 27, 32 + 22 * i, i, scrollBar::getCurrentSelection, ReactorLogic::values,
                  tileEntity::getLogicMode, mode -> new TextComponentString(EnumColor.WHITE + mode.getLabel()), ReactorLogic::getDescription,
                  ReactorLogic::getRenderStack, mode -> mode == ReactorLogic.DISABLED ? EnumColor.RED : EnumColor.DARK_GREEN, this::changeLogic));
        }
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(new TextComponentString(tileEntity.getName()), 6);
        drawCenteredTextScaledBound(new TextComponentString(LangUtils.localize("reactor.logic.mode") + ": " + EnumColor.RED +
              tileEntity.getLogicMode().getLabel()), xSize - 8, 123, titleTextColor());
        drawCenteredTextScaledBound(new TextComponentString(LangUtils.localize("gui.status") + ": " + EnumColor.RED +
              LangUtils.localize(tileEntity.getStatusTranslationKey())), xSize - 8, 136, titleTextColor());
        super.drawForegroundText(mouseX, mouseY);
    }

    private void changeLogic(ReactorLogic type) {
        Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, TileNetworkList.withContents(0, type.ordinal())));
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        int delta = Mouse.getEventDWheel();
        if (delta != 0 && scrollBar != null) {
            scrollBar.adjustScroll(delta);
        }
    }
}

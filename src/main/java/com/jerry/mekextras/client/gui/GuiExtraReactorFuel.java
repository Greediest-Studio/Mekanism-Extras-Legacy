package com.jerry.mekextras.client.gui;

import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab;
import com.jerry.mekextras.client.gui.element.GuiExtraReactorTab.ReactorTab;
import com.jerry.mekextras.common.content.reactor.SynchronizedExtraReactorData;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.function.BiPredicate;

@SideOnly(Side.CLIENT)
public class GuiExtraReactorFuel extends GuiExtraReactorInfo {

    private GuiTextField injectionRateField;

    public GuiExtraReactorFuel(InventoryPlayer inventory, TileEntityExtraReactorController tile) {
        super(inventory, tile);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        if (tileEntity.getRichNaquadahTank() != null) {
            addButton(new GuiGasGauge(this, tileEntity.getRichNaquadahTank(), GuiGasGauge.Type.SMALL, 30, 64));
        }
        if (tileEntity.getFuelTank() != null) {
            addButton(new GuiGasGauge(this, tileEntity.getFuelTank(), GuiGasGauge.Type.STANDARD, 84, 50));
        }
        if (tileEntity.getRichUraniumTank() != null) {
            addButton(new GuiGasGauge(this, tileEntity.getRichUraniumTank(), GuiGasGauge.Type.SMALL, 138, 64));
        }
        addButton(new GuiProgress(() -> tileEntity.isBurning() ? 1 : 0, ProgressType.SMALL_RIGHT, this, 52, 76));
        addButton(new GuiProgress(() -> tileEntity.isBurning() ? 1 : 0, ProgressType.SMALL_LEFT, this, 106, 76));
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.HEAT));
        addButton(new GuiExtraReactorTab(this, tileEntity, ReactorTab.STATS));
        injectionRateField = addButton(new GuiTextField(this, 0, 103, 115, 26, 11)
              .setMaxLength(2)
              .setInputValidator(isDigitOrTextboxKey())
              .setEnterHandler(this::setInjection));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        String rate = LangUtils.localize("gui.reactor.injectionRate") + ": " + tileEntity.getInjectionRate();
        drawScrollingString(new TextComponentString(rate), 0, 35, TextAlignment.CENTER, titleTextColor(), 16, false);
        drawScrollingString(new TextComponentString(LangUtils.localize("gui.editrate") + ":"), 4, 117,
              TextAlignment.RIGHT, titleTextColor(), 99, 2, false);
        super.drawForegroundText(mouseX, mouseY);
    }

    private BiPredicate<Character, Integer> isDigitOrTextboxKey() {
        return (character, keyCode) -> Character.isDigit(character) || keyCode == Keyboard.KEY_BACK ||
              keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT ||
              keyCode == Keyboard.KEY_HOME || keyCode == Keyboard.KEY_END;
    }

    private void setInjection() {
        if (injectionRateField != null && !injectionRateField.isEmpty()) {
            int rate = SynchronizedExtraReactorData.sanitizeInjectionRate(Integer.parseInt(injectionRateField.getText()));
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, TileNetworkList.withContents(1, rate)));
            injectionRateField.clear();
        }
    }
}

package com.jerry.mekextras.common.util;

/**
 * Shared geometry for the multi-process MoreMachine GUIs added by Extras.
 *
 * <p>Mekanism's {@code GuiMekanism} renders its base background through a nine-slice texture using the current
 * {@code xSize}, so widening the GUI automatically widens the background without copying a separate background texture.
 */
public final class ExtraMachineGuiLayout {

    public static final int BASE_WIDTH = 176;
    public static final int PROCESS_START_X = 27;
    public static final int PROCESS_SPACING = 19;
    private static final int PROCESS_RIGHT_MARGIN = 34;
    private static final int PLAYER_INVENTORY_WIDTH = 162;

    private ExtraMachineGuiLayout() {
    }

    public static int guiWidth(int processes) {
        int count = Math.max(1, processes);
        int lastProcessX = PROCESS_START_X + (count - 1) * PROCESS_SPACING;
        return Math.max(BASE_WIDTH, lastProcessX + PROCESS_RIGHT_MARGIN);
    }

    public static int processX(int index) {
        return PROCESS_START_X + Math.max(0, index) * PROCESS_SPACING;
    }

    public static int powerBarX(int processes) {
        return guiWidth(processes) - 12;
    }

    public static int inventoryX(int processes) {
        return Math.max(8, (guiWidth(processes) - PLAYER_INVENTORY_WIDTH) / 2);
    }
}
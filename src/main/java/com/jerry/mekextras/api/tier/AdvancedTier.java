package com.jerry.mekextras.api.tier;

import mekanism.api.EnumColor;
import mekanism.common.tier.BaseTier;

import java.util.Locale;

public enum AdvancedTier implements IAdvancedTier {

    FINAL("Final", 0x004E00, EnumColor.DARK_GREEN),
    ARCANE("Arcane", 0x012579, EnumColor.DARK_BLUE),
    ABSOLUTE("Absolute", 0xE8E983, EnumColor.BRIGHT_GREEN),
    SUPREME("Supreme", 0xE65249, EnumColor.DARK_RED),
    COSMIC("Cosmic", 0xFACEF7, EnumColor.AQUA),
    INFINITE("Infinite", 0xDFD9AB, EnumColor.PURPLE);

    private static final int[] COSMIC_COLORS = {0xF516EF, 0xF761F2, 0xF99DF5, 0xFAC0F6, 0xFACEF7};
    private static final int[] COSMIC_ORDER = {0, 1, 2, 3, 4, 3, 2, 1};
    private static final int[] INFINITE_COLORS = {0xDCC0AB, 0xD6A1B8, 0xCF9FD5, 0xA9B0DC, 0xA9D5C7, 0xDFD9AB};
    private static final int[] INFINITE_ORDER = {0, 1, 2, 3, 4, 5};
    private static final AdvancedTier[] TIERS = values();

    private final String name;
    private final int rgb;
    private final EnumColor color;

    AdvancedTier(String name, int rgb, EnumColor color) {
        this.name = name;
        this.rgb = rgb;
        this.color = color;
    }

    public String getSimpleName() {
        return name;
    }

    public String getLowerName() {
        return name.toLowerCase(Locale.ROOT);
    }

    /**
     * @return the RGB tint used for this tier's blocks, items and textures
     */
    public int getRgb() {
        return rgb;
    }

    /** Client display colour, including the configured animated Cosmic and Infinite gradients. */
    public int getDisplayColor(float ticks) {
        if (this == COSMIC) {
            return animate(ticks, COSMIC_COLORS, COSMIC_ORDER, 6.0F);
        }
        if (this == INFINITE) {
            return animate(ticks, INFINITE_COLORS, INFINITE_ORDER, 7.0F);
        }
        return rgb;
    }

    private static int animate(float ticks, int[] palette, int[] order, float frameTicks) {
        float position = Math.max(0, ticks) / frameTicks;
        int frame = (int) Math.floor(position) % order.length;
        int nextFrame = (frame + 1) % order.length;
        float progress = position - (float) Math.floor(position);
        return lerpColor(palette[order[frame]], palette[order[nextFrame]], progress);
    }

    private static int lerpColor(int from, int to, float progress) {
        int red = Math.round(((from >> 16) & 0xFF) + ((((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * progress));
        int green = Math.round(((from >> 8) & 0xFF) + ((((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * progress));
        int blue = Math.round((from & 0xFF) + (((to & 0xFF) - (from & 0xFF)) * progress));
        return red << 16 | green << 8 | blue;
    }

    public EnumColor getColor() {
        return color;
    }

    public boolean isObtainable() {
        return true;
    }

    @Override
    public AdvancedTier getAdvanceTier() {
        return this;
    }

    @Override
    public BaseTier getBaseTier() {
        // Every AdvancedTier is beyond Mekanism's Ultimate tier.
        return BaseTier.ULTIMATE;
    }

    public static AdvancedTier byIndex(int index) {
        return index >= 0 && index < TIERS.length ? TIERS[index] : FINAL;
    }

    public static AdvancedTier byName(String name) {
        for (AdvancedTier tier : TIERS) {
            if (tier.getSimpleName().equalsIgnoreCase(name)) {
                return tier;
            }
        }
        return FINAL;
    }
}


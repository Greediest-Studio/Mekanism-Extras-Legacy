package com.jerry.mekextras.api;

/**
 * Upgrades added by Mekanism Extras. Mekanism 1.12.2 uses a fixed upgrade enum, so these are handled by our own machines.
 */
public enum ExtraUpgrade {

    IONIC_MEMBRANE("ionic_membrane"),
    CREATIVE("creative");

    private final String name;

    ExtraUpgrade(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTranslationKey() {
        return "upgrade.mekanism_extras." + name;
    }

    public String getDescriptionKey() {
        return getTranslationKey() + ".desc";
    }

    public static ExtraUpgrade byIndex(int index) {
        ExtraUpgrade[] values = values();
        return index >= 0 && index < values.length ? values[index] : IONIC_MEMBRANE;
    }
}


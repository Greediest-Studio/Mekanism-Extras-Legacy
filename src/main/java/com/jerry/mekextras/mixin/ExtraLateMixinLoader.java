package com.jerry.mekextras.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public final class ExtraLateMixinLoader implements ILateMixinLoader {

    private static final String CONFIG = "mixins.mekanism_extras.json";


    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList(CONFIG);
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return CONFIG.equals(mixinConfig) && Loader.isModLoaded("mekanism");
    }
}
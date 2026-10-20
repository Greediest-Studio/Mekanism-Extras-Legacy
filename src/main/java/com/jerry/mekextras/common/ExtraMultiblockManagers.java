package com.jerry.mekextras.common;

import com.jerry.mekextras.common.content.matrix.SynchronizedExtraMatrixData;
import com.jerry.mekextras.common.content.reactor.SynchronizedExtraReactorData;
import mekanism.common.multiblock.MultiblockManager;

/**
 * Multiblock managers owned by Mekanism Extras Legacy.
 */
public class ExtraMultiblockManagers {

    public static final MultiblockManager<SynchronizedExtraMatrixData> matrixManager = new MultiblockManager<>("mekanismExtrasInductionMatrix");

    public static final MultiblockManager<SynchronizedExtraReactorData> reactorManager = new MultiblockManager<>("mekanismExtrasNaquadahReactor");

    private ExtraMultiblockManagers() {
    }
}
package com.jerry.mekextras.common.block.states;

import com.jerry.mekextras.common.block.BlockExtraMachine;
import mekanism.common.block.states.BlockStateFacing;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;

public class BlockStateExtraMachine extends BlockStateContainer {

    public static final PropertyBool activeProperty = PropertyBool.create("active");

    public BlockStateExtraMachine(BlockExtraMachine block) {
        super(block, BlockStateFacing.facingProperty, activeProperty);
    }

}


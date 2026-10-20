package com.jerry.mekextras.common.item;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.common.block.BlockExtraBin;
import com.jerry.mekextras.common.inventory.slot.ExtraBinSlot;
import com.jerry.mekextras.common.tier.BTier;
import mekanism.api.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Item block of the tiered bins. The tooltip mirrors Mekanism's own bin tooltip.
 */
public class ItemBlockExtraBin extends ItemBlockExtraMachine {

    public ItemBlockExtraBin(Block block) {
        super(block);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        BTier tier = getBinTier();
        ExtraBinSlot slot = readSlot(stack, tier);
        if (slot != null && !slot.isEmpty()) {
            tooltip.add(EnumColor.BRIGHT_GREEN + slot.getStack().getDisplayName());
            String amount = slot.getCount() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : String.valueOf(slot.getCount());
            tooltip.add(EnumColor.PURPLE + LangUtils.localize("tooltip.itemAmount") + ": " + EnumColor.GREY + amount);
        } else {
            tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty"));
        }
        if (slot != null && slot.isLocked()) {
            tooltip.add(EnumColor.PINK + LangUtils.localize("tooltip.locked") + ": " + EnumColor.GREY + slot.getLockStack().getDisplayName());
        }
        int cap = tier.getStorage();
        tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": " + EnumColor.GREY +
              (cap == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : cap) + " " + LangUtils.localize("transmission.Items"));

        if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) +
                    EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails") + ".");
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) +
                    EnumColor.GREY + " " + LangUtils.localize("tooltip.and") + " " + EnumColor.AQUA +
                    GameSettings.getKeyDisplayString(MekanismKeyHandler.handModeSwitchKey.getKeyCode()) + EnumColor.GREY + " " +
                    LangUtils.localize("tooltip.forDesc") + ".");
        } else if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.handModeSwitchKey)) {
            String descriptionKey = "description." + MekanismExtras.MODID + "." + getRegistryName().getPath();
            if (!net.minecraft.util.text.translation.I18n.canTranslate(descriptionKey)) {
                descriptionKey = "description." + MekanismExtras.MODID + ".machine";
            }
            tooltip.addAll(mekanism.common.util.MekanismUtils.splitTooltip(LangUtils.localize(descriptionKey), stack));
        }
    }

    private BTier getBinTier() {
        Block block = getBlock();
        return block instanceof BlockExtraBin ? ((BlockExtraBin) block).getTier() : BTier.ABSOLUTE;
    }

    @Nullable
    private ExtraBinSlot readSlot(ItemStack stack, BTier tier) {
        if (!ItemDataUtils.hasData(stack, "Items")) {
            return null;
        }
        NBTTagList list = ItemDataUtils.getList(stack, "Items");
        if (list == null || list.tagCount() == 0) {
            return null;
        }
        ExtraBinSlot slot = ExtraBinSlot.create(null, () -> tier);
        slot.deserializeNBT(list.getCompoundTagAt(0));
        return slot;
    }
}


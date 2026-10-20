package com.jerry.mekextras.common.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ExtraConfig {

    public static Configuration config;

    public static boolean enableNaquadahOre;
    public static int naquadahSmallVeinsPerChunk;
    public static int naquadahSmallMinHeight;
    public static int naquadahSmallMaxHeight;
    public static int naquadahSmallVeinSize;
    public static boolean enableEndNaquadahOre;
    public static int endNaquadahVeinsPerChunk;
    public static int endNaquadahMinHeight;
    public static int endNaquadahMaxHeight;
    public static int endNaquadahVeinSize;

    public static void init(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "mekanism_extras.cfg"));
        config.load();
        sync();
    }

    public static void sync() {
        //These keys were generated before world generation was aligned with the upstream mod.
        config.getCategory("world").remove("naquadahOreVeinsPerChunk");
        config.getCategory("world").remove("naquadahOreMaxHeight");
        Property oreEnabled = config.get("world", "enableNaquadahOre", true, "Enable naquadah ore generation in the overworld.");
        Property smallVeins = config.get("world", "naquadahSmallVeinsPerChunk", 8, "Number of small naquadah veins generated per overworld chunk.", 0, 64);
        Property smallMin = config.get("world", "naquadahSmallMinHeight", 1, "Minimum Y level for small naquadah veins. The upstream mod uses the bottom bedrock range.", 1, 255);
        Property smallMax = config.get("world", "naquadahSmallMaxHeight", 4, "Maximum Y level for small naquadah veins.", 1, 255);
        Property smallSize = config.get("world", "naquadahSmallVeinSize", 4, "Maximum number of blocks in a small naquadah vein.", 1, 32);
        Property endOre = config.get("world", "enableEndNaquadahOre", true, "Enable naquadah ore generation in the end.");
        Property endVeins = config.get("world", "endNaquadahVeinsPerChunk", 24, "Number of naquadah veins generated per End chunk.", 0, 64);
        Property endMin = config.get("world", "endNaquadahMinHeight", 10, "Minimum Y level for End naquadah veins.", 1, 255);
        Property endMax = config.get("world", "endNaquadahMaxHeight", 30, "Maximum Y level for End naquadah veins.", 1, 255);
        Property endSize = config.get("world", "endNaquadahVeinSize", 6, "Maximum number of blocks in an End naquadah vein.", 1, 32);

        enableNaquadahOre = oreEnabled.getBoolean();
        naquadahSmallVeinsPerChunk = smallVeins.getInt();
        naquadahSmallMinHeight = smallMin.getInt();
        naquadahSmallMaxHeight = Math.max(naquadahSmallMinHeight, smallMax.getInt());
        naquadahSmallVeinSize = smallSize.getInt();
        enableEndNaquadahOre = endOre.getBoolean();
        endNaquadahVeinsPerChunk = endVeins.getInt();
        endNaquadahMinHeight = endMin.getInt();
        endNaquadahMaxHeight = Math.max(endNaquadahMinHeight, endMax.getInt());
        endNaquadahVeinSize = endSize.getInt();

        if (config.hasChanged()) {
            config.save();
        }
    }
}

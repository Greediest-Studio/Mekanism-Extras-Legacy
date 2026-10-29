package com.jerry.mekextras;

import com.jerry.mekextras.client.ClientProxy;
import com.jerry.mekextras.common.CommonProxy;
import com.jerry.mekextras.common.CreativeTabExtra;
import com.jerry.mekextras.common.ExtraGuiHandler;
import com.jerry.mekextras.common.config.ExtraConfig;
import com.jerry.mekextras.common.registries.ExtraBlocks;
import com.jerry.mekextras.common.registries.ExtraFluids;
import com.jerry.mekextras.common.registries.ExtraItems;
import com.jerry.mekextras.common.recipe.ExtraSupremeRecipes;
import com.jerry.mekextras.common.registries.ExtraUpgrades;
import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.common.network.PacketSimpleGui;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = MekanismExtras.MODID, name = MekanismExtras.NAME, version = MekanismExtras.VERSION,
      acceptedMinecraftVersions = "[1.12.2]",
      dependencies = "required-after:mekanism;required-after:mekceumoremachine",
      customProperties = {
            @Mod.CustomProperty(k = "license", v = "MIT"),
            @Mod.CustomProperty(k = "issueTrackerUrl", v = "https://github.com/Jerry-Modding/Mekanism-Extras/issues"),
            @Mod.CustomProperty(k = "iconFile", v = "assets/mekanism_extras/logo.png")
      })
@Mod.EventBusSubscriber
public class MekanismExtras implements IModule {

    public static final String MODID = "mekanism_extras";
    public static final String NAME = "Mekanism Extras Legacy";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "com.jerry.mekextras.client.ClientProxy", serverSide = "com.jerry.mekextras.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MekanismExtras.MODID)
    public static MekanismExtras instance;

    public static Version versionNumber = new Version(1, 0, 0);

    public static CreativeTabExtra tabExtra = new CreativeTabExtra();

    public static Logger logger;

    static {
        // Gases, infuse types, slurries and their fluid counterparts have to exist before Mekanism finishes loading.
        ExtraFluids.register();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ExtraBlocks.registerBlocks(event.getRegistry());
        com.jerry.mekextras.common.registries.ExtraFluidBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ExtraItems.registerItems(event.getRegistry());
        ExtraBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        ExtraSupremeRecipes.register(event.getRegistry());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerRenders();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ExtraConfig.init(event);
        ExtraBlocks.registerWorldGenerators();
        ExtraUpgrades.init();
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register this mod as a Mekanism module so that Mekanism knows about our tiers and upgrades.
        Mekanism.modulesLoaded.add(this);
        // Register this mod's GUI provider in the simple packet protocol.
        PacketSimpleGui.handlers.add(proxy);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ExtraGuiHandler());

        ExtraItems.registerOreDict();
        ExtraItems.registerAdvancedRecipes();
        ExtraItems.registerNaquadahProcessing();
        ExtraItems.registerInfuseObjects();
        ExtraItems.registerSmelting();

        proxy.registerTileEntities();
        proxy.registerTESRs();
        proxy.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID) || event.getModID().equals(Mekanism.MODID)) {
            ExtraConfig.sync();
        }
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Extras";
    }

    @Override
    public void writeConfig(ByteBuf dataStream, MekanismConfig config) {
    }

    @Override
    public void readConfig(ByteBuf dataStream, MekanismConfig destConfig) {
    }

    @Override
    public void resetClient() {
    }
}

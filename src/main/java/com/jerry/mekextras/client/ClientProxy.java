package com.jerry.mekextras.client;

import com.jerry.mekextras.client.gui.GuiExtraChemicalTank;
import mekanism.client.MekanismClient;
import com.jerry.mekextras.client.gui.GuiExtraEnergyCube;
import com.jerry.mekextras.client.gui.GuiExtraChemicalInfuser;
import com.jerry.mekextras.client.gui.GuiExtraChemicalOxidizer;
import com.jerry.mekextras.client.gui.GuiExtraChemicalWasher;
import com.jerry.mekextras.client.gui.GuiExtraIsotopicCentrifuge;
import com.jerry.mekextras.client.gui.GuiExtraCrystallizer;
import com.jerry.mekextras.client.gui.GuiExtraDissolutionChamber;
import com.jerry.mekextras.client.gui.GuiExtraFactory;
import com.jerry.mekextras.client.gui.GuiExtraNutritionalLiquifier;
import com.jerry.mekextras.client.gui.GuiExtraOrganicFarm;
import com.jerry.mekextras.client.gui.GuiExtraInductionMatrix;
import com.jerry.mekextras.client.gui.GuiExtraMatrixStats;
import com.jerry.mekextras.client.gui.GuiExtraFluidTank;
import com.jerry.mekextras.client.render.item.ExtraRenderTierChemicalDissolutionChamberItem;
import com.jerry.mekextras.client.render.item.ExtraRenderTierIsotopicCentrifugeItem;
import com.jerry.mekextras.client.render.item.ExtraRenderTierNutritionalLiquifierItem;
import com.jerry.mekextras.client.render.item.RenderExtraEnergyCubeItem;
import com.jerry.mekextras.client.render.item.RenderExtraFluidTankItem;
import com.jerry.mekextras.client.render.tileentity.RenderExtraBin;
import com.jerry.mekextras.client.render.tileentity.RenderExtraEnergyCube;
import com.jerry.mekextras.client.render.tileentity.RenderExtraFluidTank;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderLogisticalTransporter;
import com.jerry.mekextras.client.render.tileentity.RenderExtraMachine;
import com.jerry.mekextras.client.render.tileentity.RenderExtraNaquadahReactor;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderTierChemicalDissolutionChamber;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderTierNutritionalLiquifier;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderTierIsotopicCentrifuge;
import com.jerry.mekextras.common.CommonProxy;
import com.jerry.mekextras.common.block.BlockExtraEnergyCube;
import com.jerry.mekextras.common.block.BlockExtraFluidTank;
import com.jerry.mekextras.common.item.ItemBlockExtraMachine;
import com.jerry.mekextras.common.tile.TileEntityExtraBin;
import com.jerry.mekextras.common.tile.TileEntityExtraChemicalTank;
import com.jerry.mekextras.common.tile.TileEntityExtraEnergyCube;
import com.jerry.mekextras.common.tile.TileEntityExtraFluidTank;
import com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory;
import com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalInfuser;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalOxidizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraChemicalWasher;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraIsotopicCentrifuge;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraCrystallizer;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraDissolutionChamber;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraNutritionalLiquifier;
import com.jerry.mekextras.common.tile.moremachine.TileEntityExtraOrganicFarm;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraInductionCasing;
import com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController;
import com.jerry.mekextras.common.block.BlockExtraTransmitter;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraMechanicalPipe;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraPressurizedTube;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraThermodynamicConductor;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraLogisticalTransporter;
import com.jerry.mekextras.common.tile.transmitter.TileEntityExtraUniversalCable;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderMechanicalPipe;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderPressurizedTube;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderThermodynamicConductor;
import com.jerry.mekextras.client.render.tileentity.ExtraRenderUniversalCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import mekanism.client.render.item.ItemLayerWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import mekanism.common.block.states.BlockStateTransmitter.TransmitterType;
import mekanism.common.config.MekanismConfig;
import com.jerry.mekextras.api.tier.AdvancedTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private static final Logger LOGGER = LogManager.getLogger("MekanismExtras");

    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void ensureSelectionWireframeNamespace() {
        String[] configured = MekanismConfig.current().client.jsonSelectionBoxModIdWhitelist.get();
        if (configured != null) {
            for (String entry : configured) {
                if ("mekanism_extras".equalsIgnoreCase(entry)) {
                    return;
                }
            }
        }
        int length = configured == null ? 0 : configured.length;
        String[] updated = java.util.Arrays.copyOf(configured == null ? new String[0] : configured, length + 1);
        updated[length] = "mekanism_extras";
        MekanismConfig.current().client.jsonSelectionBoxModIdWhitelist.set(updated);
    }

    @Override
    public void init() {
        MekanismConfig.current().client.enableSelectionWireframeRendering.set(true);
        ensureSelectionWireframeNamespace();
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
            if (tintIndex == 0 && state.getBlock() instanceof BlockExtraFluidTank) {
                float ticks = world instanceof net.minecraft.world.World actualWorld ?
                      actualWorld.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks() : MekanismClient.ticksPassed;
                return ((BlockExtraFluidTank) state.getBlock()).getTier().getAdvanceTier().getDisplayColor(ticks);
            }
            return -1;
        }, com.jerry.mekextras.common.registries.ExtraBlocks.FINAL_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.ARCANE_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.ABSOLUTE_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.SUPREME_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.COSMIC_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.INFINITE_FLUID_TANK);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof ItemBlockExtraMachine && ((ItemBlockExtraMachine) stack.getItem()).getBlock() instanceof BlockExtraFluidTank) {
                return ((BlockExtraFluidTank) ((ItemBlockExtraMachine) stack.getItem()).getBlock()).getTier().getAdvanceTier()
                      .getDisplayColor(getItemAnimationTicks());
            }
            return -1;
        }, com.jerry.mekextras.common.registries.ExtraBlocks.FINAL_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.ARCANE_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.ABSOLUTE_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.SUPREME_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.COSMIC_FLUID_TANK,
                com.jerry.mekextras.common.registries.ExtraBlocks.INFINITE_FLUID_TANK);
    }

    private static float getItemAnimationTicks() {
        return (System.currentTimeMillis() % 600_000L) / 50.0F;
    }

    @Override
    public void registerTESRs() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraUniversalCable.class, new ExtraRenderUniversalCable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraMechanicalPipe.class, new ExtraRenderMechanicalPipe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraPressurizedTube.class, new ExtraRenderPressurizedTube());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraThermodynamicConductor.class, new ExtraRenderThermodynamicConductor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraLogisticalTransporter.class, new ExtraRenderLogisticalTransporter());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraBin.class, new RenderExtraBin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraEnergyCube.class, new RenderExtraEnergyCube());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraFluidTank.class, RenderExtraFluidTank.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraChemicalTank.class, new RenderExtraMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraDissolutionChamber.class, ExtraRenderTierChemicalDissolutionChamber.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraNutritionalLiquifier.class, ExtraRenderTierNutritionalLiquifier.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraIsotopicCentrifuge.class, ExtraRenderTierIsotopicCentrifuge.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtraReactorController.class, new RenderExtraNaquadahReactor());
    }

    @Override
    public void registerRenders() {
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation name = item.getRegistryName();
            if (name != null && name.getNamespace().equals("mekanism_extras")) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));

                if (item instanceof ItemBlockExtraMachine) {
                    Block block = ((ItemBlockExtraMachine) item).getBlock();
                    String path = name.getPath();
                    if (block instanceof BlockExtraEnergyCube) {
                        item.setTileEntityItemStackRenderer(new RenderExtraEnergyCubeItem());
                    } else if (block instanceof BlockExtraFluidTank) {
                        item.setTileEntityItemStackRenderer(new RenderExtraFluidTankItem());
                    } else if (path.endsWith("_dissolution_chamber")) {
                        item.setTileEntityItemStackRenderer(new ExtraRenderTierChemicalDissolutionChamberItem());
                    } else if (path.endsWith("_nutritional_liquifier")) {
                        item.setTileEntityItemStackRenderer(new ExtraRenderTierNutritionalLiquifierItem());
                    } else if (path.endsWith("_isotopic_centrifuge")) {
                        item.setTileEntityItemStackRenderer(new ExtraRenderTierIsotopicCentrifugeItem());
                    }
                }
            }
        }
        // Some item blocks override their model location, make sure every block item is covered as well.
        for (Block block : ForgeRegistries.BLOCKS) {
            if (block instanceof BlockExtraTransmitter) {
                ModelLoader.setCustomStateMapper(block, TRANSMITTER_MAPPER);
            }
            ResourceLocation name = block.getRegistryName();
            if (name != null && name.getNamespace().equals("mekanism_extras")) {
                Item item = Item.getItemFromBlock(block);
                if (item != null) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));
                }
            }
        }
    }

    /**
     * Transmitters use their own state mapper so that each of our blocks resolves to its own model file
     * (mirroring Mekanism's TransmitterStateMapper, but keyed on the block's fixed tier).
     */
    private static final StateMapperBase TRANSMITTER_MAPPER = new StateMapperBase() {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            BlockExtraTransmitter block = (BlockExtraTransmitter) state.getBlock();
            String name = block.getExtraTier().getLowerName() + "_" + block.getTransmitterType().getName();
            return new ModelResourceLocation(new ResourceLocation("mekanism_extras", name), "normal");
        }
    };

    /**
     * Mirrors Mekanism's own item model handling: the energy cube and fluid tank items are rendered by dedicated
     * ItemStack renderers so that their inventory, held and dropped appearance matches Mekanism.
     */
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        RenderExtraFluidTankItem.resetDisplayInts();
        int boundModels = 0;
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof ItemBlockExtraMachine)) {
                continue;
            }
            ResourceLocation name = item.getRegistryName();
            if (name == null || !name.getNamespace().equals("mekanism_extras")) {
                continue;
            }
            Block block = ((ItemBlockExtraMachine) item).getBlock();
            String path = name.getPath();
            boolean customRenderer = block instanceof BlockExtraEnergyCube || block instanceof BlockExtraFluidTank ||
                  path.endsWith("_dissolution_chamber") || path.endsWith("_nutritional_liquifier") ||
                  path.endsWith("_isotopic_centrifuge");
            if (!customRenderer) {
                continue;
            }
            ModelResourceLocation location = new ModelResourceLocation(name, "inventory");
            IBakedModel baked = registry.getObject(location);
            if (baked == null) {
                LOGGER.warn("Missing baked inventory model for {}", location);
                continue;
            }
            ItemLayerWrapper wrapper = new ItemLayerWrapper(baked);
            registry.putObject(location, wrapper);
            boundModels++;
            if (block instanceof BlockExtraEnergyCube) {
                RenderExtraEnergyCubeItem.registerModel(item, wrapper);
            } else if (block instanceof BlockExtraFluidTank) {
                RenderExtraFluidTankItem.registerModel(item, wrapper);
            } else if (path.endsWith("_dissolution_chamber")) {
                ExtraRenderTierChemicalDissolutionChamberItem.model = wrapper;
            } else if (path.endsWith("_nutritional_liquifier")) {
                ExtraRenderTierNutritionalLiquifierItem.model = wrapper;
            } else if (path.endsWith("_isotopic_centrifuge")) {
                ExtraRenderTierIsotopicCentrifugeItem.model = wrapper;
            }
        }
        LOGGER.info("Bound {} Mekanism Extras TESR inventory models", boundModels);
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityExtraEnergyCube) {
            return new GuiExtraEnergyCube(player.inventory, (TileEntityExtraEnergyCube) tile);
        }
        if (tile instanceof TileEntityExtraFluidTank) {
            return new GuiExtraFluidTank(player.inventory, (TileEntityExtraFluidTank) tile);
        }
        if (tile instanceof TileEntityExtraInductionCasing) {
            return new GuiExtraInductionMatrix(player.inventory, (TileEntityExtraInductionCasing) tile);
        }
        if (tile instanceof TileEntityExtraCrystallizer) {
            return new GuiExtraCrystallizer(player.inventory, (TileEntityExtraCrystallizer) tile);
        }
        if (tile instanceof TileEntityExtraDissolutionChamber) {
            return new GuiExtraDissolutionChamber(player.inventory, (TileEntityExtraDissolutionChamber) tile);
        }
        if (tile instanceof TileEntityExtraChemicalWasher) {
            return new GuiExtraChemicalWasher(player.inventory, (TileEntityExtraChemicalWasher) tile);
        }
        if (tile instanceof TileEntityExtraChemicalInfuser) {
            return new GuiExtraChemicalInfuser(player.inventory, (TileEntityExtraChemicalInfuser) tile);
        }
        if (tile instanceof TileEntityExtraIsotopicCentrifuge) {
            return new GuiExtraIsotopicCentrifuge(player.inventory, (TileEntityExtraIsotopicCentrifuge) tile);
        }
        if (tile instanceof TileEntityExtraChemicalOxidizer) {
            return new GuiExtraChemicalOxidizer(player.inventory, (TileEntityExtraChemicalOxidizer) tile);
        }
        if (tile instanceof TileEntityExtraNutritionalLiquifier) {
            return new GuiExtraNutritionalLiquifier(player.inventory, (TileEntityExtraNutritionalLiquifier) tile);
        }
        if (tile instanceof TileEntityExtraOrganicFarm) {
            return new GuiExtraOrganicFarm(player.inventory, (TileEntityExtraOrganicFarm) tile);
        }
        if (tile instanceof TileEntityExtraFactory) {
            return new GuiExtraFactory(player.inventory, (TileEntityExtraFactory) tile);
        }
        if (tile instanceof TileEntityExtraChemicalTank) {
            return new GuiExtraChemicalTank(player.inventory, (TileEntityExtraChemicalTank) tile);
        }
        if (tile instanceof com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorController reactor && reactor.isFormed()) {
            return new com.jerry.mekextras.client.gui.GuiExtraReactorController(player.inventory, reactor);
        }
        if (tile instanceof com.jerry.mekextras.common.tile.multiblock.TileEntityExtraReactorLogicAdapter adapter) {
            return new com.jerry.mekextras.client.gui.GuiExtraReactorLogicAdapter(player.inventory, adapter);
        }
        if (tile instanceof TileEntityAdvancedElectricPump) {
            return new com.jerry.mekextras.client.gui.GuiExtraAdvancedElectricPump(player.inventory, (TileEntityAdvancedElectricPump) tile);
        }
        if (mekanism.common.Mekanism.proxy != null) {
            Object gui = mekanism.common.Mekanism.proxy.getClientGui(ID, player, world, pos);
            if (gui != null) {
                return gui;
            }
        }
        return null;
    }
}

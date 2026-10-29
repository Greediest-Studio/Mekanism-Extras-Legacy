package com.jerry.mekextras.common.registries;

import com.jerry.mekextras.MekanismExtras;
import com.jerry.mekextras.api.ExtraUpgrade;
import com.jerry.mekextras.api.tier.AdvancedTier;
import com.jerry.mekextras.api.tier.ExtraAlloyTier;
import com.jerry.mekextras.common.item.ItemAlloyRadiance;
import com.jerry.mekextras.common.item.ItemExtraAlloy;
import com.jerry.mekextras.common.item.ItemExtraTierInstaller;
import com.jerry.mekextras.common.item.ItemExtraUpgrade;
import com.jerry.mekextras.common.item.ItemNaquadahHohlraum;
import com.jerry.mekextras.common.tier.ExtraQIODriveTier;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismFluids;
import mekanism.common.MekanismItems;
import mekanism.common.item.ItemMekanism;
import mekanism.common.content.qio.QIODriveDefinition;
import mekanism.common.item.ItemQIODrive;
import mekanism.common.recipe.RecipeHandler;
import com.jerry.mekextras.common.recipe.ExtraDissolutionRecipe;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ExtraItems {

    //QIO drives. Registered with Mekanism's extensible QIO drive system, so they mount into QIO drive arrays and
    //behave exactly like Mekanism's own drives.
    public static final QIODriveDefinition COLLAPSE_DRIVE_DEFINITION = QIODriveDefinition.builder(MekanismExtras.MODID, ExtraQIODriveTier.COLLAPSE.getName())
          .baseTier(ExtraQIODriveTier.COLLAPSE.getBaseTier())
          .maxCount(ExtraQIODriveTier.COLLAPSE.getCount())
          .maxTypes(ExtraQIODriveTier.COLLAPSE.getTypes())
          .register();
    public static final QIODriveDefinition GAMMA_DRIVE_DEFINITION = QIODriveDefinition.builder(MekanismExtras.MODID, ExtraQIODriveTier.GAMMA.getName())
          .baseTier(ExtraQIODriveTier.GAMMA.getBaseTier())
          .maxCount(ExtraQIODriveTier.GAMMA.getCount())
          .maxTypes(ExtraQIODriveTier.GAMMA.getTypes())
          .register();
    public static final QIODriveDefinition BLACK_HOLE_DRIVE_DEFINITION = QIODriveDefinition.builder(MekanismExtras.MODID, ExtraQIODriveTier.BLACK_HOLE.getName())
          .baseTier(ExtraQIODriveTier.BLACK_HOLE.getBaseTier())
          .maxCount(ExtraQIODriveTier.BLACK_HOLE.getCount())
          .maxTypes(ExtraQIODriveTier.BLACK_HOLE.getTypes())
          .register();
    public static final QIODriveDefinition SINGULARITY_DRIVE_DEFINITION = QIODriveDefinition.builder(MekanismExtras.MODID, ExtraQIODriveTier.SINGULARITY.getName())
          .baseTier(ExtraQIODriveTier.SINGULARITY.getBaseTier())
          .maxCount(ExtraQIODriveTier.SINGULARITY.getCount())
          .maxTypes(ExtraQIODriveTier.SINGULARITY.getTypes())
          .register();

    public static final Item COLLAPSE_QIO_DRIVE = new ItemQIODrive(COLLAPSE_DRIVE_DEFINITION).setRarity(EnumRarity.UNCOMMON);
    public static final Item GAMMA_QIO_DRIVE = new ItemQIODrive(GAMMA_DRIVE_DEFINITION).setRarity(EnumRarity.RARE);
    public static final Item BLACK_HOLE_QIO_DRIVE = new ItemQIODrive(BLACK_HOLE_DRIVE_DEFINITION).setRarity(EnumRarity.EPIC);
    public static final Item SINGULARITY_QIO_DRIVE = new ItemQIODrive(SINGULARITY_DRIVE_DEFINITION).setRarity(EnumRarity.EPIC);

    //Upgrades (the Stack upgrade is provided by Mekanism CE's multiblock machine module as mekanismmultiblockmachine:threadupgrade)
    public static final Item IONIC_MEMBRANE_UPGRADE = new ItemExtraUpgrade(ExtraUpgrade.IONIC_MEMBRANE, EnumRarity.RARE);
    public static final Item CREATIVE_UPGRADE = new ItemExtraUpgrade(ExtraUpgrade.CREATIVE, EnumRarity.EPIC);

    //Tier installers
    public static final Item FINAL_TIER_INSTALLER = new ItemExtraTierInstaller(null, AdvancedTier.FINAL);
    public static final Item ARCANE_TIER_INSTALLER = new ItemExtraTierInstaller(AdvancedTier.FINAL, AdvancedTier.ARCANE);
    public static final Item ABSOLUTE_TIER_INSTALLER = new ItemExtraTierInstaller(AdvancedTier.ARCANE, AdvancedTier.ABSOLUTE);
    public static final Item SUPREME_TIER_INSTALLER = new ItemExtraTierInstaller(AdvancedTier.ABSOLUTE, AdvancedTier.SUPREME);
    public static final Item COSMIC_TIER_INSTALLER = new ItemExtraTierInstaller(AdvancedTier.SUPREME, AdvancedTier.COSMIC);
    public static final Item INFINITE_TIER_INSTALLER = new ItemExtraTierInstaller(AdvancedTier.COSMIC, AdvancedTier.INFINITE);

    //Control circuits
    public static final Item FINAL_CONTROL_CIRCUIT = circuit(AdvancedTier.FINAL);
    public static final Item ARCANE_CONTROL_CIRCUIT = circuit(AdvancedTier.ARCANE);
    public static final Item ABSOLUTE_CONTROL_CIRCUIT = circuit(AdvancedTier.ABSOLUTE);
    public static final Item SUPREME_CONTROL_CIRCUIT = circuit(AdvancedTier.SUPREME);
    public static final Item COSMIC_CONTROL_CIRCUIT = circuit(AdvancedTier.COSMIC);
    public static final Item INFINITE_CONTROL_CIRCUIT = circuit(AdvancedTier.INFINITE);

    //Alloys
    public static final Item RADIANCE_ALLOY = new ItemAlloyRadiance().setRarity(EnumRarity.COMMON);
    public static final Item FANTASY_ALLOY = new ItemExtraAlloy(ExtraAlloyTier.FANTASY, EnumRarity.COMMON);
    public static final Item PHANTOM_ALLOY = new ItemExtraAlloy(ExtraAlloyTier.PHANTOM, EnumRarity.UNCOMMON);
    public static final Item THERMONUCLEAR_ALLOY = new ItemExtraAlloy(ExtraAlloyTier.THERMONUCLEAR, EnumRarity.UNCOMMON);
    public static final Item SHINING_ALLOY = new ItemExtraAlloy(ExtraAlloyTier.SHINING, EnumRarity.RARE);
    public static final Item SPECTRUM_ALLOY = new ItemExtraAlloy(ExtraAlloyTier.SPECTRUM, EnumRarity.EPIC);

    //Enriched materials
    public static final Item ENRICHED_OSMIUM = new ItemMekanism();
    public static final Item ENRICHED_LEAD = new ItemMekanism();
    //Naquadah processing chain
    public static final Item CLUMP_NAQUADAH = new ItemMekanism();
    public static final Item DIRTY_DUST_NAQUADAH = new ItemMekanism();
    public static final Item DUST_NAQUADAH = new ItemMekanism();
    public static final Item SHARD_NAQUADAH = new ItemMekanism();
    public static final Item CRYSTAL_NAQUADAH = new ItemMekanism();
    public static final Item INGOT_NAQUADAH = new ItemMekanism();
    public static final Item NUGGET_NAQUADAH = new ItemMekanism();
    public static final Item NAQUADAH_HOHLRAUM = new ItemNaquadahHohlraum();

    private ExtraItems() {
    }

    private static Item circuit(AdvancedTier tier) {
        return new ItemMekanism().setNameColor(tier.getColor());
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        register(registry, COLLAPSE_QIO_DRIVE, "qio_drive_" + ExtraQIODriveTier.COLLAPSE.getName());
        register(registry, GAMMA_QIO_DRIVE, "qio_drive_" + ExtraQIODriveTier.GAMMA.getName());
        register(registry, BLACK_HOLE_QIO_DRIVE, "qio_drive_" + ExtraQIODriveTier.BLACK_HOLE.getName());
        register(registry, SINGULARITY_QIO_DRIVE, "qio_drive_" + ExtraQIODriveTier.SINGULARITY.getName());

        register(registry, IONIC_MEMBRANE_UPGRADE, "upgrade_" + ExtraUpgrade.IONIC_MEMBRANE.getName());
        register(registry, CREATIVE_UPGRADE, "upgrade_" + ExtraUpgrade.CREATIVE.getName());

        register(registry, FINAL_TIER_INSTALLER, AdvancedTier.FINAL.getLowerName() + "_tier_installer");
        register(registry, ARCANE_TIER_INSTALLER, AdvancedTier.ARCANE.getLowerName() + "_tier_installer");
        register(registry, ABSOLUTE_TIER_INSTALLER, AdvancedTier.ABSOLUTE.getLowerName() + "_tier_installer");
        register(registry, SUPREME_TIER_INSTALLER, AdvancedTier.SUPREME.getLowerName() + "_tier_installer");
        register(registry, COSMIC_TIER_INSTALLER, AdvancedTier.COSMIC.getLowerName() + "_tier_installer");
        register(registry, INFINITE_TIER_INSTALLER, AdvancedTier.INFINITE.getLowerName() + "_tier_installer");

        register(registry, FINAL_CONTROL_CIRCUIT, AdvancedTier.FINAL.getLowerName() + "_control_circuit");
        register(registry, ARCANE_CONTROL_CIRCUIT, AdvancedTier.ARCANE.getLowerName() + "_control_circuit");
        register(registry, ABSOLUTE_CONTROL_CIRCUIT, AdvancedTier.ABSOLUTE.getLowerName() + "_control_circuit");
        register(registry, SUPREME_CONTROL_CIRCUIT, AdvancedTier.SUPREME.getLowerName() + "_control_circuit");
        register(registry, COSMIC_CONTROL_CIRCUIT, AdvancedTier.COSMIC.getLowerName() + "_control_circuit");
        register(registry, INFINITE_CONTROL_CIRCUIT, AdvancedTier.INFINITE.getLowerName() + "_control_circuit");

        register(registry, RADIANCE_ALLOY, "alloy_radiance");
        register(registry, FANTASY_ALLOY, "alloy_" + ExtraAlloyTier.FANTASY.getName());
        register(registry, PHANTOM_ALLOY, "alloy_" + ExtraAlloyTier.PHANTOM.getName());
        register(registry, THERMONUCLEAR_ALLOY, "alloy_" + ExtraAlloyTier.THERMONUCLEAR.getName());
        register(registry, SHINING_ALLOY, "alloy_" + ExtraAlloyTier.SHINING.getName());
        register(registry, SPECTRUM_ALLOY, "alloy_" + ExtraAlloyTier.SPECTRUM.getName());

        register(registry, ENRICHED_OSMIUM, "enriched_osmium");
        register(registry, ENRICHED_LEAD, "enriched_lead");
        register(registry, CLUMP_NAQUADAH, "clump_naquadah");
        register(registry, DIRTY_DUST_NAQUADAH, "dirty_dust_naquadah");
        register(registry, DUST_NAQUADAH, "dust_naquadah");
        register(registry, SHARD_NAQUADAH, "shard_naquadah");
        register(registry, CRYSTAL_NAQUADAH, "crystal_naquadah");
        register(registry, INGOT_NAQUADAH, "ingot_naquadah");
        register(registry, NUGGET_NAQUADAH, "nugget_naquadah");
        register(registry, NAQUADAH_HOHLRAUM, "naquadah_hohlraum");
    }

    public static Item register(IForgeRegistry<Item> registry, Item item, String name) {
        item.setTranslationKey(MekanismExtras.MODID + "." + name);
        item.setRegistryName(new ResourceLocation(MekanismExtras.MODID, name));
        item.setCreativeTab(MekanismExtras.tabExtra);
        registry.register(item);
        return item;
    }

    public static void registerOreDict() {
        OreDictionary.registerOre("enrichedOsmium", ENRICHED_OSMIUM);
        OreDictionary.registerOre("enrichedLead", ENRICHED_LEAD);
        OreDictionary.registerOre("clumpNaquadah", CLUMP_NAQUADAH);
        OreDictionary.registerOre("dirtyDustNaquadah", DIRTY_DUST_NAQUADAH);
        OreDictionary.registerOre("dustNaquadah", DUST_NAQUADAH);
        OreDictionary.registerOre("shardNaquadah", SHARD_NAQUADAH);
        OreDictionary.registerOre("crystalNaquadah", CRYSTAL_NAQUADAH);
        OreDictionary.registerOre("ingotNaquadah", INGOT_NAQUADAH);
        OreDictionary.registerOre("nuggetNaquadah", NUGGET_NAQUADAH);
        OreDictionary.registerOre("blockNaquadah", ExtraBlocks.NAQUADAH_BLOCK);
        OreDictionary.registerOre("oreNaquadah", ExtraBlocks.NAQUADAH_ORE);
        OreDictionary.registerOre("oreNaquadah", ExtraBlocks.END_NAQUADAH_ORE);

    }

    /**
     * Enriched infusion materials are consumed by the Metallurgic Infuser exactly like the upstream chemical
     * conversion recipes.
     */
    public static void registerInfuseObjects() {
        InfuseRegistry.registerInfuseObject(new ItemStack(ENRICHED_LEAD), new InfuseObject(ExtraFluids.LEAD, 80));
        for (ItemStack leadDust : OreDictionary.getOres("dustLead", false)) {
            InfuseRegistry.registerInfuseObject(leadDust, new InfuseObject(ExtraFluids.LEAD, 10));
        }
    }

    public static ItemStack stack(Item item) {
        return new ItemStack(item);
    }

    public static void registerNaquadahProcessing() {
        for (ItemStack ore : OreDictionary.getOres("oreNaquadah", false)) {
            RecipeHandler.addPurificationChamberRecipe(ore, new ItemStack(CLUMP_NAQUADAH, 3));
            RecipeHandler.addChemicalInjectionChamberRecipe(ore, MekanismFluids.HydrogenChloride, new ItemStack(SHARD_NAQUADAH, 4));
            RecipeHandler.addChemicalDissolutionChamberRecipe(ore, new GasStack(ExtraFluids.DIRTY_NAQUADAH, 1000));
            RecipeHandler.addEnrichmentChamberRecipe(ore, new ItemStack(DUST_NAQUADAH, 2));
        }
        RecipeHandler.addPurificationChamberRecipe(new ItemStack(SHARD_NAQUADAH), new ItemStack(CLUMP_NAQUADAH));
        RecipeHandler.addChemicalInjectionChamberRecipe(new ItemStack(CRYSTAL_NAQUADAH), MekanismFluids.HydrogenChloride, new ItemStack(SHARD_NAQUADAH));
        RecipeHandler.addChemicalCrystallizerRecipe(new GasStack(ExtraFluids.CLEAN_NAQUADAH, 200), new ItemStack(CRYSTAL_NAQUADAH));
        RecipeHandler.addCrusherRecipe(new ItemStack(CLUMP_NAQUADAH), new ItemStack(DIRTY_DUST_NAQUADAH));
        RecipeHandler.addEnrichmentChamberRecipe(new ItemStack(DIRTY_DUST_NAQUADAH), new ItemStack(DUST_NAQUADAH));
        RecipeHandler.addCrusherRecipe(new ItemStack(INGOT_NAQUADAH), new ItemStack(DUST_NAQUADAH));
        RecipeHandler.addChemicalWasherRecipe(new GasStack(ExtraFluids.DIRTY_NAQUADAH, 1),
              new FluidStack(FluidRegistry.WATER, 1), new GasStack(ExtraFluids.CLEAN_NAQUADAH, 1));
    }

    /**
     * 1.12.2 does not know JSON smelting recipes, so the naquadah processing chain is registered in code.
     */
    public static void registerSmelting() {
        net.minecraft.item.crafting.FurnaceRecipes recipes = net.minecraft.item.crafting.FurnaceRecipes.instance();
        ItemStack ingot = new ItemStack(INGOT_NAQUADAH);
        recipes.addSmeltingRecipe(new ItemStack(DUST_NAQUADAH), ingot, 0.3F);
        recipes.addSmeltingRecipe(new ItemStack(ExtraBlocks.NAQUADAH_ORE), ingot, 0.3F);
        recipes.addSmeltingRecipe(new ItemStack(ExtraBlocks.END_NAQUADAH_ORE), ingot, 0.3F);
    }
    public static void registerAdvancedRecipes() {
        RecipeHandler.addMetallurgicInfuserRecipe(ExtraFluids.LEAD, 40,
              new ItemStack(mekanism.generators.common.GeneratorsBlocks.ReactorGlass, 1, 1),
              new ItemStack(ExtraBlocks.LEAD_COATED_LASER_FOCUS_MATRIX));
        RecipeHandler.addNucleosynthesizerRecipe(new ItemStack(MekanismBlocks.BasicBlock2, 1, 11),
              new GasStack(MekanismFluids.Antimatter, 1), new ItemStack(ExtraBlocks.NAQUADAH_REACTOR_CASING), 0, 3000);
        RecipeHandler.addMetallurgicInfuserRecipe(ExtraFluids.LEAD, 10, new ItemStack(DUST_NAQUADAH, 4),
              new ItemStack(NAQUADAH_HOHLRAUM));

        addEnrichingOreRecipe("dustOsmium", new ItemStack(ENRICHED_OSMIUM));
        addEnrichingOreRecipe("dustLead", new ItemStack(ENRICHED_LEAD));
        addChemicalOxidizerRecipes();
        addNaquadahAdvancedRecipes();
    }

    private static void addChemicalOxidizerRecipes() {
        // 1.12.2's chemical conversion is expressed as an oxidizer recipe for osmium. The former
        // progression infuse types were removed together with their unused circuit recipes.
        RecipeHandler.addChemicalOxidizerRecipe(new ItemStack(ENRICHED_OSMIUM), new GasStack(MekanismFluids.LiquidOsmium, 1600));
    }
    private static void addNaquadahAdvancedRecipes() {
        RecipeHandler.addChemicalInfuserRecipe(new GasStack(MekanismFluids.UraniumHexafluoride, 1),
              new GasStack(ExtraFluids.NAQUADAH_HEXAFLUORIDE, 1),
              new GasStack(ExtraFluids.FLUORINATED_NAQUADAH_URANIUM_FUEL, 2));
        RecipeHandler.addRecipe(RecipeHandler.Recipe.CHEMICAL_DISSOLUTION_CHAMBER,
              new ExtraDissolutionRecipe(new ItemStack(INGOT_NAQUADAH), MekanismFluids.HydrofluoricAcid, 6,
                    new GasStack(ExtraFluids.NAQUADAH_HEXAFLUORIDE, 100)));

        ItemStack osmiumDust = firstOre("dustOsmium");
        ItemStack osmiumShard = firstOre("shardOsmium");
        if (!osmiumDust.isEmpty() && !osmiumShard.isEmpty()) {
            RecipeHandler.addPRCRecipe(osmiumDust, new FluidStack(ExtraFluidBlocks.FLUORINATED_NAQUADAH_URANIUM_FUEL, 200),
                  new GasStack(MekanismFluids.LiquidOsmium, 200), osmiumShard,
                  new GasStack(ExtraFluids.NAQUADAH_URANIUM_FUEL, 200), 1000, 60);
        }
        for (ItemStack thermalSource : preferredOres("ingotNetherite", "ingotOsmium")) {
            RecipeHandler.addPRCRecipe(thermalSource, new FluidStack(FluidRegistry.LAVA, 500),
                  new GasStack(MekanismFluids.SuperheatedSodium, 500), ItemStack.EMPTY,
                  new GasStack(ExtraFluids.MOLTEN_THERMONUCLEAR, 240), 1000, 900);
        }

        addRotaryConversion(ExtraFluids.NAQUADAH_HEXAFLUORIDE, ExtraFluidBlocks.NAQUADAH_HEXAFLUORIDE);
        addRotaryConversion(ExtraFluids.FLUORINATED_NAQUADAH_URANIUM_FUEL, ExtraFluidBlocks.FLUORINATED_NAQUADAH_URANIUM_FUEL);
        addRotaryConversion(ExtraFluids.RICH_NAQUADAH_FUEL, ExtraFluidBlocks.RICH_NAQUADAH_FUEL);
        addRotaryConversion(ExtraFluids.RICH_URANIUM_FUEL, ExtraFluidBlocks.RICH_URANIUM_FUEL);
        addRotaryConversion(ExtraFluids.POLONIUM_CONTAINING_STEAM, ExtraFluidBlocks.POLONIUM_CONTAINING_SOLUTION);
        addRotaryConversion(ExtraFluids.POLONIUM208, ExtraFluidBlocks.POLONIUM208);
        RecipeHandler.addThermalEvaporationRecipe(new FluidStack(ExtraFluidBlocks.POLONIUM_CONTAINING_SOLUTION, 10),
              new FluidStack(ExtraFluidBlocks.POLONIUM208, 1));
        RecipeHandler.addSolarNeutronRecipe(new GasStack(ExtraFluids.POLONIUM208, 10),
              new GasStack(MekanismFluids.Polonium, 1));
    }

    private static void addRotaryConversion(Gas gas, net.minecraftforge.fluids.Fluid fluid) {
        RecipeHandler.addRotaryRecipe(new FluidStack(fluid, 1), new GasStack(gas, 1),
              new GasStack(gas, 1), new FluidStack(fluid, 1));
    }

    private static ItemStack firstOre(String oreName) {
        java.util.List<ItemStack> entries = OreDictionary.getOres(oreName, false);
        return entries.isEmpty() ? ItemStack.EMPTY : entries.get(0).copy();
    }

    private static java.util.List<ItemStack> preferredOres(String preferred, String fallback) {
        java.util.List<ItemStack> entries = OreDictionary.getOres(preferred, false);
        return entries.isEmpty() ? OreDictionary.getOres(fallback, false) : entries;
    }

    private static void addEnrichingOreRecipe(String oreName, ItemStack output) {
        for (ItemStack input : OreDictionary.getOres(oreName, false)) {
            RecipeHandler.addEnrichmentChamberRecipe(input, output);
        }
    }

}

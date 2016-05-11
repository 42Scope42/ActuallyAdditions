/*
 * This file ("ActuallyAdditions.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod;

import de.ellpeck.actuallyadditions.mod.achievement.InitAchievements;
import de.ellpeck.actuallyadditions.mod.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.mod.booklet.InitBooklet;
import de.ellpeck.actuallyadditions.mod.config.ConfigurationHandler;
import de.ellpeck.actuallyadditions.mod.crafting.CrusherCrafting;
import de.ellpeck.actuallyadditions.mod.crafting.InitCrafting;
import de.ellpeck.actuallyadditions.mod.crafting.ItemCrafting;
import de.ellpeck.actuallyadditions.mod.event.InitEvents;
import de.ellpeck.actuallyadditions.mod.fluids.InitFluids;
import de.ellpeck.actuallyadditions.mod.gen.InitVillager;
import de.ellpeck.actuallyadditions.mod.gen.OreGen;
import de.ellpeck.actuallyadditions.mod.inventory.GuiHandler;
import de.ellpeck.actuallyadditions.mod.items.InitForeignPaxels;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import de.ellpeck.actuallyadditions.mod.items.ItemCoffee;
import de.ellpeck.actuallyadditions.mod.items.lens.LensRecipeHandler;
import de.ellpeck.actuallyadditions.mod.material.InitArmorMaterials;
import de.ellpeck.actuallyadditions.mod.material.InitToolMaterials;
import de.ellpeck.actuallyadditions.mod.misc.DispenserHandlerFertilize;
import de.ellpeck.actuallyadditions.mod.misc.DungeonLoot;
import de.ellpeck.actuallyadditions.mod.misc.SoundHandler;
import de.ellpeck.actuallyadditions.mod.misc.WorldData;
import de.ellpeck.actuallyadditions.mod.network.PacketHandler;
import de.ellpeck.actuallyadditions.mod.ore.InitOreDict;
import de.ellpeck.actuallyadditions.mod.proxy.IProxy;
import de.ellpeck.actuallyadditions.mod.recipe.FuelHandler;
import de.ellpeck.actuallyadditions.mod.recipe.HairyBallHandler;
import de.ellpeck.actuallyadditions.mod.recipe.TreasureChestHandler;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.update.UpdateChecker;
import de.ellpeck.actuallyadditions.mod.util.FakePlayerUtil;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import java.util.Locale;

//                                                                           So that BuildCraft Oil always gets used
@Mod(modid = ModUtil.MOD_ID, name = ModUtil.NAME, version = ModUtil.VERSION, dependencies = "after:BuildCraft|Energy", guiFactory = "de.ellpeck.actuallyadditions.mod.config.GuiFactory")
public class ActuallyAdditions{

    @Instance(ModUtil.MOD_ID)
    public static ActuallyAdditions instance;

    @SidedProxy(clientSide = "de.ellpeck.actuallyadditions.mod.proxy.ClientProxy", serverSide = "de.ellpeck.actuallyadditions.mod.proxy.ServerProxy")
    public static IProxy proxy;

    static{
        //For some reason, this has to be done here
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        ModUtil.LOGGER.info("Starting PreInitialization Phase...");

        new ConfigurationHandler(event.getSuggestedConfigurationFile());
        PacketHandler.init();
        InitToolMaterials.init();
        InitArmorMaterials.init();
        InitBlocks.init();
        InitFluids.init();
        InitItems.init();
        FuelHandler.init();
        SoundHandler.init();
        UpdateChecker.init();
        InitBooklet.preInit();
        proxy.preInit(event);

        ModUtil.LOGGER.info("PreInitialization Finished.");
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        ModUtil.LOGGER.info("Starting Initialization Phase...");

        InitOreDict.init();
        InitAchievements.init();
        GuiHandler.init();
        OreGen.init();
        TileEntityBase.init();
        InitEvents.init();
        InitCrafting.init();
        DungeonLoot.init();
        proxy.init(event);

        ModUtil.LOGGER.info("Initialization Finished.");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        ModUtil.LOGGER.info("Starting PostInitialization Phase...");

        InitVillager.init();
        ItemCoffee.initIngredients();
        CrusherCrafting.init();
        ItemCrafting.initMashedFoodRecipes();
        HairyBallHandler.init();
        TreasureChestHandler.init();
        LensRecipeHandler.init();
        InitForeignPaxels.init();

        InitBooklet.postInit();
        proxy.postInit(event);

        ModUtil.LOGGER.info("PostInitialization Finished.");
        FakePlayerUtil.info();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        Util.registerDispenserHandler(InitItems.itemFertilizer, new DispenserHandlerFertilize());

        WorldData.init(event.getServer());
    }

    @EventHandler
    public void missingMapping(FMLMissingMappingsEvent event){
        for(FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()){
            if(mapping.name != null){
                String name = mapping.name.toLowerCase(Locale.ROOT);
                if(name.startsWith(ModUtil.MOD_ID+":")){
                    if(name.contains("paxel") || name.contains("itemspecial") || name.contains("blockbookstand") || name.contains("rarmor") || name.contains("bucket")){
                        mapping.ignore();
                        ModUtil.LOGGER.info("Missing Mapping "+mapping.name+" is getting ignored. This is intentional.");
                    }
                }
            }
        }
    }
}

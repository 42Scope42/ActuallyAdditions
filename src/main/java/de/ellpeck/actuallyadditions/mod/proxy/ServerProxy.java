/*
 * This file ("ServerProxy.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.proxy;

import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ServerProxy implements IProxy{

    @Override
    public void preInit(FMLPreInitializationEvent event){
        ModUtil.LOGGER.info("PreInitializing ServerProxy...");
    }

    @Override
    public void init(FMLInitializationEvent event){
        ModUtil.LOGGER.info("Initializing ServerProxy...");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event){
        ModUtil.LOGGER.info("PostInitializing ServerProxy...");
    }

    @Override
    public void addRenderRegister(ItemStack stack, ResourceLocation location){

    }

    @Override
    public void addRenderVariant(Item item, ResourceLocation... location){

    }

    @Override
    public void addColoredItem(Item item){

    }
}

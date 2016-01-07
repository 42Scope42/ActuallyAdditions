/*
 * This file ("ConfigurationHandler.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.config;

import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigurationHandler{

    public static final String ISSUES_WARNING = " [THIS COULD CAUSE ISSUES, CHANGE AT YOUR OWN RISK!]";

    public static Configuration config;

    public ConfigurationHandler(File configFile){
        ModUtil.LOGGER.info("Grabbing Configurations...");

        Util.registerEvent(this);

        if(config == null){
            config = new Configuration(configFile, true);
            loadConfig();
        }
    }

    private static void loadConfig(){
        ConfigValues.defineConfigValues(config);

        if(config.hasChanged()){
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event){
        if(event.modID.equalsIgnoreCase(ModUtil.MOD_ID)){
            loadConfig();
        }
    }
}
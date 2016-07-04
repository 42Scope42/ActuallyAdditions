/*
 * This file ("ItemSolidifiedExperience.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items;

import de.ellpeck.actuallyadditions.mod.config.values.ConfigBoolValues;
import de.ellpeck.actuallyadditions.mod.items.base.ItemBase;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemSolidifiedExperience extends ItemBase{

    public static final int SOLID_XP_AMOUNT = 8;

    public ItemSolidifiedExperience(String name){
        super(name);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityDropEvent(LivingDropsEvent event){
        if(event.getEntityLiving().worldObj != null && !event.getEntityLiving().worldObj.isRemote && event.getSource().getEntity() instanceof EntityPlayer){
            //Drop Solidified XP
            if(event.getEntityLiving() instanceof EntityCreature){
                if(Util.RANDOM.nextInt(10) <= event.getLootingLevel()*2){
                    event.getEntityLiving().entityDropItem(new ItemStack(InitItems.itemSolidifiedExperience, Util.RANDOM.nextInt(2+event.getLootingLevel())+1), 0);
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
        if(!world.isRemote){
            if(!player.isSneaking()){
                world.spawnEntityInWorld(new EntityXPOrb(world, player.posX+0.5, player.posY+0.5, player.posZ+0.5, SOLID_XP_AMOUNT));
                if(!player.capabilities.isCreativeMode){
                    stack.stackSize--;
                }
            }
            else{
                world.spawnEntityInWorld(new EntityXPOrb(world, player.posX+0.5, player.posY+0.5, player.posZ+0.5, SOLID_XP_AMOUNT*stack.stackSize));
                if(!player.capabilities.isCreativeMode){
                    stack.stackSize = 0;
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }


    @Override
    public EnumRarity getRarity(ItemStack stack){
        return EnumRarity.UNCOMMON;
    }
}

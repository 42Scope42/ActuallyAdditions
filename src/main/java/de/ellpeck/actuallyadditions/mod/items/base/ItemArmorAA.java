/*
 * This file ("ItemArmorAA.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items.base;

import de.ellpeck.actuallyadditions.mod.creative.CreativeTab;
import de.ellpeck.actuallyadditions.mod.util.ItemUtil;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemArmorAA extends ItemArmor{

    private ItemStack repairItem;
    private String name;
    private String[] textures;
    private EnumRarity rarity;

    public ItemArmorAA(String name, ArmorMaterial material, int type, ItemStack repairItem, String textureBase, EnumRarity rarity){
        super(material, 0, type);
        this.repairItem = repairItem;
        this.name = name;
        String texture = ModUtil.MOD_ID_LOWER+":textures/armor/"+textureBase;
        textures = new String[]{texture+"1.png", texture+"2.png"};
        this.rarity = rarity;

        this.register();
    }

    public ItemArmorAA(String name, ArmorMaterial material, int type, ItemStack repairItem, String textureBase){
        this(name, material, type, repairItem, textureBase, EnumRarity.RARE);
    }

    private void register(){
        this.setUnlocalizedName(ModUtil.MOD_ID_LOWER+"."+this.getBaseName());
        GameRegistry.registerItem(this, this.getBaseName());
        if(this.shouldAddCreative()){
            this.setCreativeTab(CreativeTab.instance);
        }
        else{
            this.setCreativeTab(null);
        }
    }

    protected String getBaseName(){
        return this.name;
    }

    public boolean shouldAddCreative(){
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack){
        return this.rarity;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type){
        return this.textures[slot == 2 ? 1 : 0];
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack){
        return ItemUtil.areItemsEqual(this.repairItem, stack, false);
    }
}

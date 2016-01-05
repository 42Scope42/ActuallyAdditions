/*
 * This file ("TileEntityCoffeeMachine.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.ellpeck.actuallyadditions.api.recipe.coffee.CoffeeIngredient;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import de.ellpeck.actuallyadditions.mod.items.ItemCoffee;
import de.ellpeck.actuallyadditions.mod.items.metalists.TheMiscItems;
import de.ellpeck.actuallyadditions.mod.network.gui.IButtonReactor;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileEntityCoffeeMachine extends TileEntityInventoryBase implements IButtonReactor, IEnergyReceiver, IFluidSaver, IFluidHandler, IEnergySaver{

    public static final int SLOT_COFFEE_BEANS = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_WATER_INPUT = 11;
    public static final int SLOT_WATER_OUTPUT = 12;
    public static final int CACHE_USE = 15;
    public static final int ENERGY_USED = 150;
    public static final int WATER_USE = 500;
    public static final int COFFEE_CACHE_MAX_AMOUNT = 300;
    private static final int TIME_USED = 500;
    public EnergyStorage storage = new EnergyStorage(300000);
    public FluidTank tank = new FluidTank(4*FluidContainerRegistry.BUCKET_VOLUME);
    public int coffeeCacheAmount;
    public int brewTime;
    private int lastEnergy;
    private int lastTank;
    private int lastCoffeeAmount;
    private int lastBrewTime;

    public TileEntityCoffeeMachine(){
        super(13, "coffeeMachine");
    }

    @SideOnly(Side.CLIENT)
    public int getCoffeeScaled(int i){
        return this.coffeeCacheAmount*i/COFFEE_CACHE_MAX_AMOUNT;
    }

    @SideOnly(Side.CLIENT)
    public int getWaterScaled(int i){
        return this.tank.getFluidAmount()*i/this.tank.getCapacity();
    }

    @SideOnly(Side.CLIENT)
    public int getEnergyScaled(int i){
        return this.storage.getEnergyStored()*i/this.storage.getMaxEnergyStored();
    }

    @SideOnly(Side.CLIENT)
    public int getBrewScaled(int i){
        return this.brewTime*i/TIME_USED;
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!worldObj.isRemote){
            this.storeCoffee();

            if(this.brewTime > 0 || this.isRedstonePowered){
                this.brew();
            }

            if((this.coffeeCacheAmount != this.lastCoffeeAmount || this.storage.getEnergyStored() != this.lastEnergy || this.tank.getFluidAmount() != this.lastTank || this.brewTime != this.lastBrewTime) && this.sendUpdateWithInterval()){
                this.lastCoffeeAmount = coffeeCacheAmount;
                this.lastEnergy = this.storage.getEnergyStored();
                this.lastTank = this.tank.getFluidAmount();
                this.lastBrewTime = this.brewTime;
            }
        }
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        this.storage.writeToNBT(compound);
        this.tank.writeToNBT(compound);
        compound.setInteger("Cache", this.coffeeCacheAmount);
        compound.setInteger("Time", this.brewTime);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        this.storage.readFromNBT(compound);
        this.tank.readFromNBT(compound);
        this.coffeeCacheAmount = compound.getInteger("Cache");
        this.brewTime = compound.getInteger("Time");
    }

    public void storeCoffee(){
        if(this.slots[SLOT_COFFEE_BEANS] != null && this.slots[SLOT_COFFEE_BEANS].getItem() == InitItems.itemCoffeeBean){
            int toAdd = 2;
            if(toAdd <= COFFEE_CACHE_MAX_AMOUNT-this.coffeeCacheAmount){
                this.slots[SLOT_COFFEE_BEANS].stackSize--;
                if(this.slots[SLOT_COFFEE_BEANS].stackSize <= 0){
                    this.slots[SLOT_COFFEE_BEANS] = null;
                }
                this.coffeeCacheAmount += toAdd;
            }
        }

        WorldUtil.emptyBucket(tank, slots, SLOT_WATER_INPUT, SLOT_WATER_OUTPUT, FluidRegistry.WATER);
    }

    public void brew(){
        if(!worldObj.isRemote){
            if(this.slots[SLOT_INPUT] != null && this.slots[SLOT_INPUT].getItem() == InitItems.itemMisc && this.slots[SLOT_INPUT].getItemDamage() == TheMiscItems.CUP.ordinal() && this.slots[SLOT_OUTPUT] == null && this.coffeeCacheAmount >= CACHE_USE && this.tank.getFluid() != null && this.tank.getFluid().getFluid() == FluidRegistry.WATER && this.tank.getFluidAmount() >= WATER_USE){
                if(this.storage.getEnergyStored() >= ENERGY_USED){
                    if(this.brewTime%30 == 0){
                        this.worldObj.playSoundEffect(xCoord, yCoord, zCoord, ModUtil.MOD_ID_LOWER+":coffeeMachine", 0.35F, 1.0F);
                    }

                    this.brewTime++;
                    this.storage.extractEnergy(ENERGY_USED, false);
                    if(this.brewTime >= TIME_USED){
                        this.brewTime = 0;
                        ItemStack output = new ItemStack(InitItems.itemCoffee);
                        for(int i = 3; i < this.slots.length-2; i++){
                            if(this.slots[i] != null){
                                CoffeeIngredient ingredient = ItemCoffee.getIngredientFromStack(this.slots[i]);
                                if(ingredient != null){
                                    if(ingredient.effect(output)){
                                        this.slots[i].stackSize--;
                                        if(this.slots[i].stackSize <= 0){
                                            this.slots[i] = this.slots[i].getItem().getContainerItem(this.slots[i]);
                                        }
                                    }
                                }
                            }
                        }
                        this.slots[SLOT_OUTPUT] = output.copy();
                        this.slots[SLOT_INPUT].stackSize--;
                        if(this.slots[SLOT_INPUT].stackSize <= 0){
                            this.slots[SLOT_INPUT] = null;
                        }
                        this.coffeeCacheAmount -= CACHE_USE;
                        this.tank.drain(WATER_USE, true);
                    }
                }
            }
            else{
                this.brewTime = 0;
            }
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return (i >= 3 && ItemCoffee.getIngredientFromStack(stack) != null) || (i == SLOT_COFFEE_BEANS && stack.getItem() == InitItems.itemCoffeeBean) || (i == SLOT_INPUT && stack.getItem() == InitItems.itemMisc && stack.getItemDamage() == TheMiscItems.CUP.ordinal()) || (i == SLOT_WATER_INPUT && FluidContainerRegistry.containsFluid(stack, new FluidStack(FluidRegistry.WATER, 1)));
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side){
        return slot == SLOT_OUTPUT || (slot >= 3 && slot < this.slots.length-2 && ItemCoffee.getIngredientFromStack(stack) == null) || slot == SLOT_WATER_OUTPUT;
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player){
        if(buttonID == 0 && this.brewTime <= 0){
            this.brew();
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate){
        return this.storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from){
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from){
        return this.storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from){
        return true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
        return resource.getFluid() == FluidRegistry.WATER && from != ForgeDirection.DOWN ? this.tank.fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid){
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid){
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from){
        return new FluidTankInfo[]{this.tank.getInfo()};
    }

    @Override
    public int getEnergy(){
        return this.storage.getEnergyStored();
    }

    @Override
    public void setEnergy(int energy){
        this.storage.setEnergyStored(energy);
    }

    @Override
    public FluidStack[] getFluids(){
        return new FluidStack[]{this.tank.getFluid()};
    }

    @Override
    public void setFluids(FluidStack[] fluids){
        this.tank.setFluid(fluids[0]);
    }
}

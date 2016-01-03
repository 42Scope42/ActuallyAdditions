/*
 * This file ("TileEntityFermentingBarrel.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.ellpeck.actuallyadditions.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.util.WorldUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileEntityFermentingBarrel extends TileEntityInventoryBase implements IFluidHandler, IFluidSaver{

    private static final int PROCESS_TIME = 100;
    public FluidTank canolaTank = new FluidTank(2*FluidContainerRegistry.BUCKET_VOLUME);
    public FluidTank oilTank = new FluidTank(2*FluidContainerRegistry.BUCKET_VOLUME);
    public int currentProcessTime;
    private int lastCanola;
    private int lastOil;
    private int lastProcessTime;

    public TileEntityFermentingBarrel(){
        super(4, "fermentingBarrel");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateEntity(){
        super.updateEntity();
        if(!worldObj.isRemote){
            int produce = 80;
            if(this.canolaTank.getFluidAmount() >= produce && produce <= this.oilTank.getCapacity()-this.oilTank.getFluidAmount()){
                this.currentProcessTime++;
                if(this.currentProcessTime >= PROCESS_TIME){
                    this.currentProcessTime = 0;

                    this.oilTank.fill(new FluidStack(InitBlocks.fluidOil, produce), true);
                    this.canolaTank.drain(produce, true);
                    this.markDirty();
                }
            }
            else{
                this.currentProcessTime = 0;
            }

            WorldUtil.emptyBucket(canolaTank, slots, 0, 1, InitBlocks.fluidCanolaOil);
            WorldUtil.fillBucket(oilTank, slots, 2, 3);

            if(this.oilTank.getFluidAmount() > 0){
                WorldUtil.pushFluid(worldObj, xCoord, yCoord, zCoord, ForgeDirection.DOWN, this.oilTank);
                if(!this.isRedstonePowered){
                    WorldUtil.pushFluid(worldObj, xCoord, yCoord, zCoord, ForgeDirection.NORTH, this.oilTank);
                    WorldUtil.pushFluid(worldObj, xCoord, yCoord, zCoord, ForgeDirection.EAST, this.oilTank);
                    WorldUtil.pushFluid(worldObj, xCoord, yCoord, zCoord, ForgeDirection.SOUTH, this.oilTank);
                    WorldUtil.pushFluid(worldObj, xCoord, yCoord, zCoord, ForgeDirection.WEST, this.oilTank);
                }
            }

            if((this.canolaTank.getFluidAmount() != this.lastCanola || this.oilTank.getFluidAmount() != this.lastOil || this.currentProcessTime != this.lastProcessTime) && this.sendUpdateWithInterval()){
                this.lastProcessTime = this.currentProcessTime;
                this.lastCanola = this.canolaTank.getFluidAmount();
                this.lastOil = this.oilTank.getFluidAmount();
            }
        }
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        compound.setInteger("ProcessTime", this.currentProcessTime);
        this.canolaTank.writeToNBT(compound);
        NBTTagCompound tag = new NBTTagCompound();
        this.oilTank.writeToNBT(tag);
        compound.setTag("OilTank", tag);
        super.writeSyncableNBT(compound, sync);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        this.currentProcessTime = compound.getInteger("ProcessTime");
        this.canolaTank.readFromNBT(compound);
        this.oilTank.readFromNBT((NBTTagCompound)compound.getTag("OilTank"));
        super.readSyncableNBT(compound, sync);
    }

    @SideOnly(Side.CLIENT)
    public int getProcessScaled(int i){
        return this.currentProcessTime*i/PROCESS_TIME;
    }

    @SideOnly(Side.CLIENT)
    public int getOilTankScaled(int i){
        return this.oilTank.getFluidAmount()*i/this.oilTank.getCapacity();
    }

    @SideOnly(Side.CLIENT)
    public int getCanolaTankScaled(int i){
        return this.canolaTank.getFluidAmount()*i/this.canolaTank.getCapacity();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return (i == 0 && FluidContainerRegistry.containsFluid(stack, new FluidStack(InitBlocks.fluidCanolaOil, FluidContainerRegistry.BUCKET_VOLUME))) || (i == 2 && stack.getItem() == Items.bucket);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side){
        return (slot == 1 && stack.getItem() == Items.bucket) || (slot == 3 && FluidContainerRegistry.containsFluid(stack, new FluidStack(InitBlocks.fluidOil, FluidContainerRegistry.BUCKET_VOLUME)));
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
        if(from != ForgeDirection.DOWN && resource.getFluid() == InitBlocks.fluidCanolaOil){
            return this.canolaTank.fill(resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
        if(resource.getFluid() == InitBlocks.fluidOil){
            return this.oilTank.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
        return this.oilTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid){
        return from != ForgeDirection.DOWN && fluid == InitBlocks.fluidCanolaOil;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid){
        return from != ForgeDirection.UP && fluid == InitBlocks.fluidOil;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from){
        return new FluidTankInfo[]{this.canolaTank.getInfo(), this.oilTank.getInfo()};
    }

    @Override
    public FluidStack[] getFluids(){
        return new FluidStack[]{this.oilTank.getFluid(), this.canolaTank.getFluid()};
    }

    @Override
    public void setFluids(FluidStack[] fluids){
        this.oilTank.setFluid(fluids[0]);
        this.canolaTank.setFluid(fluids[1]);
    }
}

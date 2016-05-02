/*
 * This file ("TileEntityGreenhouseGlass.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;


import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public class TileEntityGreenhouseGlass extends TileEntityBase{

    private int timeUntilNextFert;

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean isForSync){
        super.writeSyncableNBT(compound, isForSync);
        this.timeUntilNextFert = compound.getInteger("Time");
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean isForSync){
        super.readSyncableNBT(compound, isForSync);
        compound.setInteger("Time", this.timeUntilNextFert);
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!this.worldObj.isRemote){
            if(this.worldObj.canBlockSeeSky(this.getPos()) && this.worldObj.isDaytime()){
                if(this.timeUntilNextFert > 0){
                    this.timeUntilNextFert--;
                    if(this.timeUntilNextFert <= 0){
                        BlockPos blockToFert = this.blockToFertilize();
                        if(blockToFert != null){
                            int metaBefore = PosUtil.getMetadata(blockToFert, this.worldObj);
                            PosUtil.getBlock(blockToFert, this.worldObj).updateTick(this.worldObj, blockToFert, this.worldObj.getBlockState(blockToFert), Util.RANDOM);

                            if(PosUtil.getMetadata(blockToFert, this.worldObj) != metaBefore){
                                this.worldObj.playAuxSFX(2005, blockToFert, 0);
                            }
                        }
                    }
                }
                else{
                    int time = 300;
                    this.timeUntilNextFert = time+Util.RANDOM.nextInt(time);
                }
            }
        }
    }

    public BlockPos blockToFertilize(){
        for(int i = -1; i > 0; i--){
            BlockPos offset = PosUtil.offset(this.pos, 0, i, 0);
            Block block = PosUtil.getBlock(this.pos, this.worldObj);
            if(block != null && !(this.worldObj.isAirBlock(offset))){
                if((block instanceof IGrowable || block instanceof IPlantable) && !(block instanceof BlockGrass)){
                    return offset;
                }
                else{
                    return null;
                }
            }
        }
        return null;
    }
}

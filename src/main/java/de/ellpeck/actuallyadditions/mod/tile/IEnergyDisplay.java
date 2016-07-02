/*
 * This file ("IEnergyDisplay.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IEnergyDisplay{

    @SideOnly(Side.CLIENT)
    int getEnergy();

    @SideOnly(Side.CLIENT)
    int getMaxEnergy();

    @SideOnly(Side.CLIENT)
    boolean needsHoldShift();
}

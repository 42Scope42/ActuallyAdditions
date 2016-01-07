/*
 * This file ("RenderReconstructorLens.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.blocks.render;

import de.ellpeck.actuallyadditions.api.Position;
import de.ellpeck.actuallyadditions.api.lens.ILensItem;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityAtomicReconstructor;
import de.ellpeck.actuallyadditions.mod.util.AssetUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderReconstructorLens extends TileEntitySpecialRenderer{

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float par5, int par6){
        if(!(tile instanceof TileEntityAtomicReconstructor)){
            return;
        }
        ItemStack stack = ((TileEntityAtomicReconstructor)tile).getStackInSlot(0);

        if(stack != null && stack.getItem() instanceof ILensItem){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x+0.5F, (float)y-0.5F, (float)z+0.5F);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

            int meta = Position.fromTileEntity(tile).getMetadata(getWorld());
            if(meta == 0){
                GL11.glTranslatef(0F, -0.5F, 0F);
                GL11.glTranslatef(-0.25F, 0F, -0.25F);
                GL11.glRotatef(90F, 1F, 0F, 0F);
            }
            if(meta == 1){
                GL11.glTranslatef(0F, -1.5F-0.5F/16F, 0F);
                GL11.glTranslatef(-0.25F, 0F, -0.25F);
                GL11.glRotatef(90F, 1F, 0F, 0F);
            }
            if(meta == 2){
                GL11.glTranslatef(0F, -1F, 0F);
                GL11.glTranslatef(0F, 0F, -0.5F);
                GL11.glTranslatef(-0.25F, -0.25F, 0F);
            }
            if(meta == 3){
                GL11.glTranslatef(0F, -1F, 0F);
                GL11.glTranslatef(0F, 0F, 0.5F+0.5F/16F);
                GL11.glTranslatef(-0.25F, -0.25F, 0F);
            }
            if(meta == 4){
                GL11.glTranslatef(0F, -1F, 0F);
                GL11.glTranslatef(0.5F+0.5F/16F, 0F, 0F);
                GL11.glTranslatef(0F, -0.25F, 0.25F);
                GL11.glRotatef(90F, 0F, 1F, 0F);
            }
            if(meta == 5){
                GL11.glTranslatef(0F, -1F, 0F);
                GL11.glTranslatef(-0.5F, 0F, 0F);
                GL11.glTranslatef(0F, -0.25F, 0.25F);
                GL11.glRotatef(90F, 0F, 1F, 0F);
            }

            GL11.glScalef(0.5F, 0.5F, 0.5F);
            AssetUtil.renderItemInWorld(stack, 0);

            GL11.glPopMatrix();
        }
    }
}

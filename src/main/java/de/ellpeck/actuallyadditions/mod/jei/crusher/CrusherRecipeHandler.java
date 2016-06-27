/*
 * This file ("CrusherRecipeHandler.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.jei.crusher;

import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CrusherRecipeHandler implements IRecipeHandler<CrusherRecipe>{


    @Override
    public Class getRecipeClass(){
        return CrusherRecipe.class;
    }


    @Override
    public String getRecipeCategoryUid(){
        return CrusherRecipeCategory.NAME;
    }

    @Override
    public String getRecipeCategoryUid(CrusherRecipe recipe){
        return this.getRecipeCategoryUid();
    }


    @Override
    public IRecipeWrapper getRecipeWrapper(CrusherRecipe recipe){
        return new CrusherRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(CrusherRecipe recipe){
        return true;
    }
}

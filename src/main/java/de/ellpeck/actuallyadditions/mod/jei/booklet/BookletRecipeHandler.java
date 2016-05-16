/*
 * This file ("BookletRecipeHandler.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.jei.booklet;

import de.ellpeck.actuallyadditions.api.booklet.BookletPage;
import de.ellpeck.actuallyadditions.mod.nei.NEIBookletRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class BookletRecipeHandler implements IRecipeHandler<BookletPage>{

    @Nonnull
    @Override
    public Class getRecipeClass(){
        return BookletPage.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(){
        return NEIBookletRecipe.NAME;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull BookletPage recipe){
        return new BookletRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@Nonnull BookletPage recipe){
        return true;
    }
}

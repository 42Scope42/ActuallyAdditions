/*
 * This file ("BookletChapter.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.booklet.chapter;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.booklet.BookletPage;
import de.ellpeck.actuallyadditions.api.booklet.IBookletChapter;
import de.ellpeck.actuallyadditions.api.booklet.IBookletEntry;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.StringUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class BookletChapter implements IBookletChapter{

    public final BookletPage[] pages;
    public final IBookletEntry entry;
    public final ItemStack displayStack;
    private final String unlocalizedName;
    public TextFormatting color;

    public boolean isIncomplete;

    public BookletChapter(String unlocalizedName, IBookletEntry entry, ItemStack displayStack, BookletPage... pages){
        this.pages = pages.clone();

        this.unlocalizedName = unlocalizedName;
        entry.addChapter(this);
        ActuallyAdditionsAPI.allAndSearch.addChapter(this);
        this.entry = entry;
        this.displayStack = displayStack;

        for(BookletPage page : this.pages){
            page.setChapter(this);
        }

        this.color = TextFormatting.RESET;
    }

    @Override
    public BookletPage[] getPages(){
        return this.pages;
    }

    @Override
    public String getUnlocalizedName(){
        return this.unlocalizedName;
    }

    @Override
    public String getLocalizedName(){
        return StringUtil.localize("booklet."+ModUtil.MOD_ID+".chapter."+this.unlocalizedName+".name");
    }

    @Override
    public String getLocalizedNameWithFormatting(){
        return this.color+this.getLocalizedName();
    }

    @Override
    public IBookletEntry getEntry(){
        return this.entry;
    }

    @Override
    public ItemStack getDisplayItemStack(){
        return this.displayStack;
    }

    public BookletChapter setIncomplete(){
        this.isIncomplete = true;
        return this;
    }

    public BookletChapter setImportant(){
        this.color = TextFormatting.DARK_GREEN;
        return this;
    }

    public BookletChapter setSpecial(){
        this.color = TextFormatting.DARK_PURPLE;
        return this;
    }
}

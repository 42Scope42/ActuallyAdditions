/*
 * This file ("GuiBooklet.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://github.com/Ellpeck/ActuallyAdditions/blob/master/README.md
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015 Ellpeck
 */

package ellpeck.actuallyadditions.booklet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.actuallyadditions.booklet.button.BookmarkButton;
import ellpeck.actuallyadditions.booklet.button.IndexButton;
import ellpeck.actuallyadditions.booklet.button.TexturedButton;
import ellpeck.actuallyadditions.booklet.chapter.BookletChapter;
import ellpeck.actuallyadditions.booklet.entry.BookletEntry;
import ellpeck.actuallyadditions.booklet.entry.BookletEntryAllSearch;
import ellpeck.actuallyadditions.booklet.page.BookletPage;
import ellpeck.actuallyadditions.config.GuiConfiguration;
import ellpeck.actuallyadditions.proxy.ClientProxy;
import ellpeck.actuallyadditions.update.UpdateChecker;
import ellpeck.actuallyadditions.util.AssetUtil;
import ellpeck.actuallyadditions.util.KeyUtil;
import ellpeck.actuallyadditions.util.ModUtil;
import ellpeck.actuallyadditions.util.StringUtil;
import ellpeck.actuallyadditions.util.playerdata.PersistentClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBooklet extends GuiScreen{

    public static final ResourceLocation resLoc = AssetUtil.getBookletGuiLocation("guiBooklet");
    public static final ResourceLocation resLocHalloween = AssetUtil.getBookletGuiLocation("guiBookletHalloween");
    public static final ResourceLocation resLocChristmas = AssetUtil.getBookletGuiLocation("guiBookletChristmas");
    public static final ResourceLocation resLocValentine = AssetUtil.getBookletGuiLocation("guiBookletValentinesDay");

    public static final int CHAPTER_BUTTONS_AMOUNT = 13;

    public int xSize;
    public int ySize;
    public int guiLeft;
    public int guiTop;

    public BookletPage currentPage;
    public BookletChapter currentChapter;
    public BookletEntry currentIndexEntry;

    public int pageOpenInIndex;
    public int indexPageAmount;

    public GuiButton buttonForward;
    public GuiButton buttonBackward;
    public GuiButton buttonPreviousScreen;
    public GuiButton buttonUpdate;
    public GuiButton buttonTwitter;
    public GuiButton buttonForum;
    public GuiButton buttonAchievements;
    public GuiButton buttonConfig;
    public GuiButton[] chapterButtons = new GuiButton[CHAPTER_BUTTONS_AMOUNT];

    public GuiButton[] bookmarkButtons = new GuiButton[8];

    public GuiTextField searchField;
    public GuiScreen parentScreen;
    private int ticksElapsed;
    private boolean mousePressed;
    private boolean tryOpenMainPage;
    private boolean saveOnClose;

    private static final int[] AND_HIS_NAME_IS = new int[]{Keyboard.KEY_C, Keyboard.KEY_E, Keyboard.KEY_N, Keyboard.KEY_A};
    private int hisNameIsAt;

    public GuiBooklet(GuiScreen parentScreen, boolean tryOpenMainPage, boolean saveOnClose){
        this.xSize = 146;
        this.ySize = 180;
        this.parentScreen = parentScreen;
        this.tryOpenMainPage = tryOpenMainPage;
        this.saveOnClose = saveOnClose;
    }

    public void drawHoveringText(List list, int x, int y){
        super.func_146283_a(list, x, y);
    }

    public FontRenderer getFontRenderer(){
        return this.fontRendererObj;
    }

    public List getButtonList(){
        return this.buttonList;
    }

    @Override
    public void drawScreen(int x, int y, float f){
        //Fixes Unicode flag
        boolean unicodeBefore = this.fontRendererObj.getUnicodeFlag();
        this.fontRendererObj.setUnicodeFlag(true);

        //Draws the Background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ClientProxy.jingleAllTheWay ? resLocChristmas : (ClientProxy.pumpkinBlurPumpkinBlur ? resLocHalloween : (ClientProxy.bulletForMyValentine ? resLocValentine : resLoc)));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        //Draws the search bar
        if(this.currentIndexEntry instanceof BookletEntryAllSearch && this.currentChapter == null){
            this.mc.getTextureManager().bindTexture(resLoc);
            this.drawTexturedModalRect(this.guiLeft+146, this.guiTop+160, 146, 80, 70, 14);
        }

        //Draws Achievement Info
        BookletUtils.drawAchievementInfo(this, true, x, y);

        //Draws the title
        this.fontRendererObj.setUnicodeFlag(false);
        BookletUtils.drawTitle(this);
        this.fontRendererObj.setUnicodeFlag(true);

        //Pre-Renders the current page's content etc.
        BookletUtils.renderPre(this, x, y, this.ticksElapsed, this.mousePressed);

        //Does vanilla drawing stuff
        super.drawScreen(x, y, f);
        this.searchField.drawTextBox();

        //Draws hovering texts for buttons
        this.fontRendererObj.setUnicodeFlag(false);
        BookletUtils.doHoverTexts(this, x, y);
        BookletUtils.drawAchievementInfo(this, false, x, y);
        this.fontRendererObj.setUnicodeFlag(true);

        //Renders the current page's content
        if(this.currentIndexEntry != null && this.currentChapter != null && this.currentPage != null){
            this.currentPage.render(this, x, y, this.ticksElapsed, this.mousePressed);
        }
        this.fontRendererObj.setUnicodeFlag(unicodeBefore);

        //Resets mouse
        if(this.mousePressed){
            this.mousePressed = false;
        }
    }

    @Override
    public void keyTyped(char theChar, int key){
        if(key == Keyboard.KEY_ESCAPE && this.parentScreen != null){
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if(this.searchField.isFocused()){
            this.searchField.textboxKeyTyped(theChar, key);
            BookletUtils.updateSearchBar(this);
        }
        else{
            if(AND_HIS_NAME_IS.length > this.hisNameIsAt && AND_HIS_NAME_IS[this.hisNameIsAt] == key){
                if(this.hisNameIsAt+1 >= AND_HIS_NAME_IS.length){
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation(ModUtil.MOD_ID_LOWER, "duhDuhDuhDuuuh")));
                    ModUtil.LOGGER.info("AND HIS NAME IS JOHN CENA DUH DUH DUH DUUUH");
                    this.hisNameIsAt = 0;
                }
                else{
                    this.hisNameIsAt++;
                }
            }
            else{
                this.hisNameIsAt = 0;
                super.keyTyped(theChar, key);
            }
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3){
        this.searchField.mouseClicked(par1, par2, par3);
        //Notifys the booklet of the mouse being pressed
        if(par3 == 0 && this.currentChapter != null){
            this.mousePressed = true;
        }
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    public void actionPerformed(GuiButton button){
        //Handles update
        if(button == this.buttonUpdate){
            if(UpdateChecker.needsUpdateNotify){
                BookletUtils.openBrowser(UpdateChecker.CHANGELOG_LINK, UpdateChecker.DOWNLOAD_LINK);
            }
        }
        //Handles Twitter
        else if(button == this.buttonTwitter){
            BookletUtils.openBrowser("http://twitter.com/ActAddMod");
        }
        //Handles forum
        else if(button == this.buttonForum){
            BookletUtils.openBrowser("http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2551118");
        }
        //Handles config
        else if(button == this.buttonConfig){
            mc.displayGuiScreen(new GuiConfiguration(this));
        }
        //Handles achievements
        else if(button == this.buttonAchievements){
            mc.displayGuiScreen(new GuiAAAchievements(this, mc.thePlayer.getStatFileWriter()));
        }
        else if(button == this.buttonForward){
            BookletUtils.handleNextPage(this);
        }
        else if(button == this.buttonBackward){
            BookletUtils.handlePreviousPage(this);
        }
        //Handles gonig from page to chapter or from chapter to index
        else if(button == this.buttonPreviousScreen){
            if(KeyUtil.isShiftPressed()){
                if(this.currentChapter != null){
                    BookletUtils.openIndexEntry(this, this.currentIndexEntry, this.pageOpenInIndex, true);
                }
                else{
                    BookletUtils.openIndexEntry(this, null, 1, true);
                }
            }
            else{
                //TODO History
            }
        }
        //Handles Bookmark button
        else if(button instanceof BookmarkButton){
            ((BookmarkButton)button).onPressed();
        }
        else{
            BookletUtils.handleChapterButtonClick(this, button);
        }
    }

    @SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
    @Override
    public void initGui(){
        this.guiLeft = (this.width-this.xSize)/2;
        this.guiTop = (this.height-this.ySize)/2;

        this.buttonForward = new TexturedButton(0, this.guiLeft+this.xSize-26, this.guiTop+this.ySize+1, 164, 0, 18, 10, Collections.singletonList(EnumChatFormatting.GOLD+"Next Page"));
        this.buttonList.add(this.buttonForward);

        this.buttonBackward = new TexturedButton(1, this.guiLeft+8, this.guiTop+this.ySize+1, 146, 0, 18, 10, Collections.singletonList(EnumChatFormatting.GOLD+"Previous Page"));
        this.buttonList.add(this.buttonBackward);

        List prevScreenHover = new ArrayList<>();
        prevScreenHover.add(EnumChatFormatting.GOLD+"Back");
        prevScreenHover.add("Click for last item in history");
        prevScreenHover.add(EnumChatFormatting.ITALIC+"Shift-Click for Chapter");
        this.buttonPreviousScreen = new TexturedButton(2, this.guiLeft+this.xSize/2-7, this.guiTop+this.ySize+1, 182, 0, 15, 10, prevScreenHover);
        this.buttonList.add(this.buttonPreviousScreen);

        ArrayList updateHover = new ArrayList();
        if(UpdateChecker.checkFailed){
            updateHover.add(IChatComponent.Serializer.func_150699_a(StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".update.failed")).getFormattedText());
        }
        else if(UpdateChecker.needsUpdateNotify){
            updateHover.add(IChatComponent.Serializer.func_150699_a(StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".update.generic")).getFormattedText());
            updateHover.add(IChatComponent.Serializer.func_150699_a(StringUtil.localizeFormatted("info."+ModUtil.MOD_ID_LOWER+".update.versionCompare", ModUtil.VERSION, UpdateChecker.updateVersion)).getFormattedText());
            updateHover.add(StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".update.buttonOptions"));
        }
        this.buttonUpdate = new TexturedButton(4, this.guiLeft-11, this.guiTop-11, 245, 0, 11, 11, updateHover);
        this.buttonUpdate.visible = UpdateChecker.needsUpdateNotify;
        this.buttonList.add(this.buttonUpdate);

        this.buttonTwitter = new TexturedButton(5, this.guiLeft, this.guiTop, 213, 0, 8, 8, Collections.singletonList(EnumChatFormatting.GOLD+"Open @ActAddMod on Twitter in Browser"));
        this.buttonList.add(this.buttonTwitter);

        this.buttonForum = new TexturedButton(6, this.guiLeft, this.guiTop+10, 221, 0, 8, 8, Collections.singletonList(EnumChatFormatting.GOLD+"Open Minecraft Forum Post in Browser"));
        this.buttonList.add(this.buttonForum);

        this.buttonAchievements = new TexturedButton(7, this.guiLeft+138, this.guiTop, 205, 0, 8, 8, Collections.singletonList(EnumChatFormatting.GOLD+"Show Achievements"));
        this.buttonList.add(this.buttonAchievements);

        ArrayList configHover = new ArrayList();
        configHover.add(EnumChatFormatting.GOLD+"Show Configuration GUI");
        configHover.addAll(this.fontRendererObj.listFormattedStringToWidth("It is highly recommended that you restart your game after changing anything as that prevents possible bugs occuring!", 200));
        this.buttonConfig = new TexturedButton(8, this.guiLeft+138, this.guiTop+10, 197, 0, 8, 8, configHover);
        this.buttonList.add(this.buttonConfig);

        for(int i = 0; i < this.chapterButtons.length; i++){
            this.chapterButtons[i] = new IndexButton(9+i, guiLeft+15, guiTop+10+(i*12), 115, 10, "", this);
            this.buttonList.add(this.chapterButtons[i]);
        }

        for(int i = 0; i < this.bookmarkButtons.length; i++){
            int x = this.guiLeft+xSize/2-(this.bookmarkButtons.length/2*16)+(i*16);
            this.bookmarkButtons[i] = new BookmarkButton(this.chapterButtons[this.chapterButtons.length-1].id+1+i, x, this.guiTop+this.ySize+13, this);
            this.buttonList.add(this.bookmarkButtons[i]);
        }

        this.searchField = new GuiTextField(this.fontRendererObj, guiLeft+148, guiTop+162, 66, 10);
        this.searchField.setMaxStringLength(30);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setCanLoseFocus(false);

        this.currentPage = null;
        this.currentChapter = null;
        this.currentIndexEntry = null;

        if(this.tryOpenMainPage && !PersistentClientData.getBoolean("BookAlreadyOpened")){
            BookletUtils.openIndexEntry(this, InitBooklet.chapterIntro.entry, 1, true);
            BookletUtils.openChapter(this, InitBooklet.chapterIntro, null);

            PersistentClientData.setBoolean("BookAlreadyOpened", true);
        }
        else{
            PersistentClientData.openLastBookPage(this);
        }
    }

    @Override
    public void updateScreen(){
        super.updateScreen();
        this.searchField.updateCursorCounter();

        if(this.currentIndexEntry != null && this.currentChapter != null && this.currentPage != null){
            this.currentPage.updateScreen(this.ticksElapsed);
        }

        boolean buttonThere = UpdateChecker.needsUpdateNotify;
        this.buttonUpdate.visible = buttonThere;
        if(buttonThere){
            if(this.ticksElapsed%8 == 0){
                TexturedButton button = (TexturedButton)this.buttonUpdate;
                button.setTexturePos(245, button.texturePosY == 0 ? 22 : 0);
            }
        }

        this.ticksElapsed++;
    }

    @Override
    public void onGuiClosed(){
        if(this.saveOnClose){
            PersistentClientData.saveBookPage(this);
        }
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
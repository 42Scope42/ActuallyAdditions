package ellpeck.actuallyadditions.inventory.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.actuallyadditions.inventory.ContainerFeeder;
import ellpeck.actuallyadditions.tile.TileEntityBase;
import ellpeck.actuallyadditions.tile.TileEntityFeeder;
import ellpeck.actuallyadditions.util.AssetUtil;
import ellpeck.actuallyadditions.util.ModUtil;
import ellpeck.actuallyadditions.util.StringUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiFeeder extends GuiContainer{

    private static final ResourceLocation resLoc = AssetUtil.getGuiLocation("guiFeeder");
    public TileEntityFeeder tileFeeder;

    public GuiFeeder(InventoryPlayer inventory, TileEntityBase tile){
        super(new ContainerFeeder(inventory, tile));
        this.tileFeeder = (TileEntityFeeder)tile;
        this.xSize = 176;
        this.ySize = 70+86;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
        AssetUtil.displayNameString(this.fontRendererObj, xSize, -10, this.tileFeeder.getInventoryName());
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetUtil.GUI_INVENTORY_LOCATION);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+70, 0, 0, 176, 86);
        this.mc.getTextureManager().bindTexture(resLoc);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 70);

        if(this.tileFeeder.currentTimer > 0){
            int i = this.tileFeeder.getCurrentTimerToScale(20);
            this.drawTexturedModalRect(guiLeft+85, guiTop+42-i, 181, 19+19-i, 6, 20);
        }

        if(this.tileFeeder.currentAnimalAmount >= 2 && this.tileFeeder.currentAnimalAmount < this.tileFeeder.animalThreshold)
            this.drawTexturedModalRect(guiLeft+70, guiTop+31, 192, 16, 8, 8);

        if(this.tileFeeder.currentAnimalAmount >= this.tileFeeder.animalThreshold)
            this.drawTexturedModalRect(guiLeft+70, guiTop+31, 192, 24, 8, 8);
    }

    @Override
    public void drawScreen(int x, int y, float f){
        super.drawScreen(x, y, f);
        if(x >= guiLeft+69 && y >= guiTop+30 && x <= guiLeft+69+10 && y <= guiTop+30+10){
            String[] array = new String[]{(this.tileFeeder.currentAnimalAmount+" "+StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".gui.animals")), ((this.tileFeeder.currentAnimalAmount >= 2 && this.tileFeeder.currentAnimalAmount < this.tileFeeder.animalThreshold) ? StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".gui.enoughToBreed") : (this.tileFeeder.currentAnimalAmount >= this.tileFeeder.animalThreshold ? StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".gui.tooMany") : StringUtil.localize("info."+ModUtil.MOD_ID_LOWER+".gui.notEnough")))};
            this.func_146283_a(Arrays.asList(array), x, y);
        }
    }
}
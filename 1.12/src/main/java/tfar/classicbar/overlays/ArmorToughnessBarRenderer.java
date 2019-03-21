package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.config.ModConfig.general;

/*
    Class handles the drawing of the toughness bar
 */

public class ArmorToughnessBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    ;
    private int updateCounter = 0;
    private double playerArmorToughness = 1;

    private boolean forceUpdateIcons = false;

    public ArmorToughnessBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderArmorToughnessBar(RenderGameOverlayEvent.Post event) {


        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (!(renderViewEnity instanceof EntityPlayer  ||event.isCanceled())) return;
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.capabilities.isCreativeMode)return;
        double armorToughness = player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        if (armorToughness<=0)return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        updateCounter = mc.ingameGUI.getUpdateCounter();

        if (armorToughness != playerArmorToughness || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        playerArmorToughness = armorToughness;
        int xStart = scaledWidth / 2 + 9;
        int yStart = scaledHeight - 49;
        if(Loader.isModLoaded("toughasnails"))yStart-=10;

        mc.profiler.startSection("armortoughness");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        int f;

        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
        if(general.displayIcons)
            //Draw armor toughness icon
            drawTexturedModalRect(xStart + 82, yStart, 83, 0, 9, 9);
        //draw bar portion

        if (armorToughness<=20) {
            //calculate bar color
            cU.color2Gl(cU.hex2Color(colors.advancedColors.armorColorValues[0]));
            //draw portion of bar based on armor toughness amount
            f = xStart+80-getWidth(armorToughness,20);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(armorToughness,20), 7);

        }
        else {

            //we have wrapped, draw 2 bars
            int index = (int)Math.ceil(armorToughness/20);
            int size = colors.advancedColors.armorColorValues.length;
            int i = index;
            //if we are out of colors wrap the bar
            if (index>=size) i=size-1;
            if (index < size && armorToughness % 20 != 0){

            //draw complete first bar
                cU.color2Gl(cU.hex2Color(colors.advancedColors.armorColorValues[i-1]));
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, 79, 7);

            //draw partial second bar
                f = xStart+80-getWidth(armorToughness%20,20);

                cU.color2Gl(cU.hex2Color(colors.advancedColors.armorColorValues[i]));
            drawTexturedModalRect(f, yStart+1, 1, 10, getWidth(armorToughness%20,20), 7);}
        //case 2, bar is a multiple of 20 or it is capped
            else{
            //draw complete second bar
                cU.color2Gl(cU.hex2Color(colors.advancedColors.armorColorValues[i]));
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, 79, 7);
        }
        }

        //draw armor toughness amount
        int i1 = (int) Math.ceil(armorToughness);
        int i3 = (general.displayIcons)? 1 : 0;

        int c = Integer.valueOf(colors.advancedColors.armorColorValues[0].substring(1, 7),16);
        if (numbers.showPercent)i1 = (int)armorToughness*5;
        drawStringOnHUD(i1 + "", xStart + 101 - 9 * i3, yStart - 1, c, 0);
        //Reset back to normal settings

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;


        // GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }

}
package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.ArmorBarColor;

import static tfar.classicbar.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class ArmorBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    ;
    private int updateCounter = 0;
    private double playerArmor = 1;
    private double lastArmor = 1;

    private boolean forceUpdateIcons = false;

    public ArmorBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderArmorBar(RenderGameOverlayEvent.Pre event) {

        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ARMOR
                || event.isCanceled()
                || !(renderViewEntity instanceof EntityPlayer)) {
            return;
        }
        event.setCanceled(true);
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        double armor = player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
        if (armor<=0)return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        updateCounter = mc.ingameGUI.getUpdateCounter();
        int absorb = MathHelper.ceil(player.getAbsorptionAmount());

        if (armor != playerArmor || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        playerArmor = armor;
        double j = lastArmor;
        int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - 49;
        if (absorb>0)yStart-=10;

        mc.profiler.startSection("armor");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //Pass 1, draw bar portion

        //if armor >20
        if (armor<=20) {
            //calculate bar color
            ArmorBarColor.setColorFromHex(armorColorValues[0]);
            //draw portion of bar based on armor amount
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor,20), 7);

        }
        else {

            //we have wrapped, draw 2 bars
            int index = (int)armor/20;
            int size = armorColorValues.length;

            //if we are out of colors wrap the bar
            if (index>size) index=size;

            String i1 = armorColorValues[index-1];
            String i2 = armorColorValues[index];

            //draw first bar
            ArmorBarColor.setColorFromHex(i1);
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, 79, 7);
            //draw partial second bar
            ArmorBarColor.setColorFromHex(i2);
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, getWidth(armor%20,20), 7);

        }

        //draw armor amount
        int i1 = (int) Math.ceil(armor);
        int i2 = (int) Math.floor(Math.log10(Math.max(1, i1)));
        int i3 = (displayIcons)? 1 : 0;

        int c = Integer.valueOf(armorColorValues[0].substring(1, 7),16);
        if (showPercent)i1 = (int)armor*5;
        drawStringOnHUD(i1 + "", xStart - 10 - 10 * i3 - 6 * i2, yStart - 1, c, 0);
        //Reset back to normal settings

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ICON_VANILLA);

        if(displayIcons)
        //Draw armor icon
        drawTexturedModalRect(xStart - 10, yStart, 43, 9, 9, 9);

        //armor icon
        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }
}

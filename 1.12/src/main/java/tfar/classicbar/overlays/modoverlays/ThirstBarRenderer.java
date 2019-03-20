package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import toughasnails.api.TANCapabilities;
import toughasnails.thirst.ThirstHandler;

import static tfar.classicbar.HexColor.setColorFromHex;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;
import static toughasnails.handler.thirst.ThirstOverlayHandler.OVERLAY;

/*
    Class handles the drawing of the thirst bar
 */

public class ThirstBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    ;
    private int updateCounter = 0;
    private double playerAir = 1;

    private boolean forceUpdateIcons = false;

    public ThirstBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderThirstBar(RenderGameOverlayEvent.Pre event) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (//event.getType() != RenderGameOverlayEvent.ElementType.AIR
                //|| event.isCanceled()
                 !(renderViewEntity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.capabilities.isCreativeMode)return;
        ThirstHandler thirstStats = (ThirstHandler)player.getCapability(TANCapabilities.THIRST, (EnumFacing)null);

        double thirst = thirstStats.getThirst();
        //System.out.println(thirst);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        updateCounter = mc.ingameGUI.getUpdateCounter();

        if (thirst != playerAir || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        playerAir = thirst;
        int xStart = scaledWidth / 2 + 9;
        int yStart = scaledHeight - 49;

        mc.profiler.startSection("thirst");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on thirst amount

        float f = xStart+80-getWidth(thirst,20);
        setColorFromHex(colors.thirstBarColor);
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(thirst,20), 7);

        //draw thirst amount
        int h1 = (int) Math.floor(thirst);

        int c = Integer.valueOf(colors.thirstBarColor.substring(1),16);
        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent)h1 = (int)thirst*5;
        drawStringOnHUD(h1 + "", xStart + 83 + 9 * i3, yStart - 1, c, 0);
        //Reset back to normal settings

        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(OVERLAY);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw thirst icon
            drawTexturedModalRect(xStart + 83, yStart, 1, 25, 7, 9);
        }
        mc.getTextureManager().bindTexture(ICON_VANILLA);

        //GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }

}
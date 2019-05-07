package tfar.classicbar.overlays.modoverlays;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.TANCapabilities;
import toughasnails.thirst.ThirstHandler;

import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;
import static toughasnails.handler.thirst.ThirstOverlayHandler.OVERLAY;

public class TemperatureBarRenderer {

/*
    Class handles the drawing of the temp bar
 */

//TODO Fix this crap

    public class ThirstBarRenderer {
        private final Minecraft mc = Minecraft.getMinecraft();


        public ThirstBarRenderer() {
        }

        @SubscribeEvent//(priority = EventPriority.HIGH)
        public void renderTempBar(RenderGameOverlayEvent.Pre event) {

            if (true)return;

            Entity renderViewEntity = mc.getRenderViewEntity();
            if (//event.getType() != RenderGameOverlayEvent.ElementType.AIR ||
                    event.isCanceled() ||
                            !(renderViewEntity instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
            if (player.capabilities.isCreativeMode) return;
            ThirstHandler thirstStats = (ThirstHandler) player.getCapability(TANCapabilities.THIRST, null);
            double thirst = thirstStats.getThirst();
            double hydration = thirstStats.getHydration();
            System.out.println(hydration);
            //System.out.println(thirst);
            int scaledWidth = event.getResolution().getScaledWidth();
            int scaledHeight = event.getResolution().getScaledHeight();
            //Push to avoid lasting changes

            int xStart = scaledWidth / 2 + 9;
            int yStart = scaledHeight - 49;

            mc.profiler.startSection("temp");
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            //Bind our Custom bar
            mc.getTextureManager().bindTexture(ICON_BAR);
            //Bar background
       //     drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style);

            //draw portion of bar based on thirst amount

            float f = xStart + 80 - getWidth(thirst, 20);
          //  cU.color2Gl(cU.hex2Color(colors.thirstBarColor));
       //     drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(thirst, 20), 7,general.style);

            //draw hydration if present
       /* if (thirstStats.getHydration()>0){
            healthFractions += getWidth(thirst,20)-getWidth(thirstStats.getHydration(),20);
            setColorFromHex(colors.hydrationBarColor);
            drawTexturedModalRect(healthFractions, yStart + 1, 1, 10, getWidth(thirstStats.getHydration(),20), 7);
        }

        //draw thirst exhaustion
    /*    healthFractions = xStart - getWidth(thirstStats.getExhaustion(), 4) + 80;
        GlStateManager.color(1, 1, 1, .25f);
        drawTexturedModalRect(healthFractions, yStart + 1, 1, 28, getWidth(thirstStats.getExhaustion(), 4), 9);*/

            //draw thirst amount
            int h1 = (int) Math.floor(thirst);
            //int c = Integer.decode(colors.thirstBarColor);
            int i3 = general.displayIcons ? 1 : 0;
            if (numbers.showPercent) h1 = (int) thirst * 5;
            //drawStringOnHUD(h1 + "", xStart + 83 + 9 * i3, yStart - 1, c, 0);
            //Reset back to normal settings
            GlStateManager.color(1, 1, 1, 1);

            mc.getTextureManager().bindTexture(OVERLAY);
            GuiIngameForge.left_height += 10;

            if (general.displayIcons) {
                //Draw thirst icon
             //   drawTexturedModalRect(xStart + 83, yStart, 1, 25, 7, 9,general.style);
            }
            mc.getTextureManager().bindTexture(ICON_VANILLA);

            //GlStateManager.disableBlend();
            //Revert our state back
            GlStateManager.popMatrix();
            mc.profiler.endSection();
        }
    }
}

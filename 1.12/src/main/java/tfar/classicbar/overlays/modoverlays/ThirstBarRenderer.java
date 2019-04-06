package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.TANCapabilities;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.thirst.ThirstHandler;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;
import static toughasnails.handler.thirst.ThirstOverlayHandler.OVERLAY;

/*
    Class handles the drawing of the thirst bar
 */

public class ThirstBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Config.Name("Tough as Nails Options")
    public static ConfigToughAsNails configToughAsNails = new ConfigToughAsNails();

    public static class ConfigToughAsNails {
        @Config.Name("Thirst Bar Color")
        public String thirstBarColor = "#1C5EE4";
    }

    public ThirstBarRenderer() {
    }

    @SubscribeEvent//(priority = EventPriority.HIGH)
    public void renderThirstBar(RenderGameOverlayEvent.Post event) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (//event.getType() != RenderGameOverlayEvent.ElementType.AIR ||
                event.isCanceled() ||
             !SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST) ||
                 !(renderViewEntity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.capabilities.isCreativeMode)return;
        ThirstHandler thirstStats = (ThirstHandler)player.getCapability(TANCapabilities.THIRST, null);
        double thirst = thirstStats.getThirst();
        //System.out.println(thirst);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        int xStart = scaledWidth / 2 + 10;
        int yStart = scaledHeight - 49;

        mc.profiler.startSection("thirst");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style,false,false);

        //draw portion of bar based on thirst amount

        float f = xStart+80-getWidth(thirst,20);
        cU.color2Gl(cU.hex2Color(configToughAsNails.thirstBarColor));
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(thirst,20), 7,general.style, true,false);

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
        int c = Integer.decode(configToughAsNails.thirstBarColor);
        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent)h1 = (int)thirst*5;
        drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
        //Reset back to normal settings
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(OVERLAY);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw thirst icon
            drawTexturedModalRect(xStart + 83, yStart, 1, 25, 7, 9,0,false,false);
        }
        mc.getTextureManager().bindTexture(ICON_VANILLA);

        //GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }
}
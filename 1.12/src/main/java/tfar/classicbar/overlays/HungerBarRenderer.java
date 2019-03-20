package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import static tfar.classicbar.HexColor.*;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the hunger bar
 */

public class HungerBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    public HungerBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHungerBar(RenderGameOverlayEvent.Pre event) {


        //Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD) return;
        event.setCanceled(true);
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        double food = player.getFoodStats().getFoodLevel();
        double saturation = player.getFoodStats().getSaturationLevel();
        float exhaustion = getExhaustion(player);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes
        int xStart = scaledWidth / 2 + 9;
        int yStart = scaledHeight - 39;

        mc.profiler.startSection("hunger");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on food amount
        float f = xStart + 80 - getWidth(food, 20);

        setColorFromHex(colors.hungerBarColor);
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(food, 20), 7);

        if (saturation > 0) {

            //draw saturation
            setColorFromHex(colors.saturationBarColor);
            f += getWidth(food, 20) - getWidth(saturation, 20);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(saturation, 20), 7);

        }
        f += getWidth(saturation, 20) - getWidth(exhaustion, 4f);
        //draw exhaustion
        GlStateManager.color(1, 1, 1, .25f);
        drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);

        //draw food amount
        int h1 = (int) Math.floor(food);

        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent) h1 = (int) food * 5;
        int c = Integer.valueOf(colors.hungerBarColor.substring(1), 16);
        drawStringOnHUD(h1 + "", xStart + 9 * i3 + 83, yStart - 1, c, 0);
        //Reset back to normal settings

        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw food icon
            //food background
            drawTexturedModalRect(xStart + 83, yStart, 16, 27, 9, 9);
            //food
            drawTexturedModalRect(xStart + 83, yStart, 52, 27, 9, 9);

        }

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }
}

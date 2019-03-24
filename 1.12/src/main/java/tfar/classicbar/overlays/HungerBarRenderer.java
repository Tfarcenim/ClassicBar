package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the hunger bar
 */

public class HungerBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private float alpha = 0;
    private boolean increase = true;

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

        cU.color2Gl(cU.hex2Color(colors.hungerBarColor));
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(food, 20), 7);

        if (saturation > 0 && general.overlays.hunger.showSaturationBar) {

            //draw saturation
            cU.color2Gl(cU.hex2Color(colors.saturationBarColor));
            f += getWidth(food, 20) - getWidth(saturation, 20);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(saturation, 20), 7);

        }
        //render held food overlay

        if (general.overlays.hunger.showHeldFoodOverlay && player.getHeldItemMainhand().getItem() instanceof ItemFood ){
            ItemStack stack = player.getHeldItemMainhand();
            if (increase)alpha+=general.overlays.hunger.transitionSpeed;
            else alpha-=general.overlays.hunger.transitionSpeed;
            if (alpha>=1)increase = false;
            else if (alpha<=0) increase = true;
            ItemFood foodItem = ((ItemFood) stack.getItem());
            double hungerOverlay = foodItem.getHealAmount(stack);
            double saturationMultiplier = foodItem.getSaturationModifier(stack);
            double actualSaturation = 2*hungerOverlay*saturationMultiplier;

            //Draw Potential hunger
            double hungerWidth = Math.min(20-food,hungerOverlay);
            f = xStart - getWidth(hungerWidth+food,20) + 80;
            cU.color2Gla(cU.hex2Color(colors.hungerBarColor),alpha);
            drawTexturedModalRect(f, yStart+1, 1, 10, getWidth(hungerWidth,20), 7);

            //Draw Potential saturation
            if (general.overlays.hunger.showSaturationBar){
                double saturationWidth = Math.min(actualSaturation,20-saturation);
                saturationWidth = Math.min(saturationWidth,food+hungerWidth);
                saturationWidth = Math.min(saturationWidth,hungerOverlay+food);
                f = xStart - getWidth(saturationWidth+saturation,20) + 80;
                cU.color2Gla(cU.hex2Color(colors.saturationBarColor),alpha);
                drawTexturedModalRect(f, yStart+1, 1, 10, getWidth(saturationWidth,20), 7);
            }
        }

        if (general.overlays.hunger.showExhaustionOverlay) {
            exhaustion = Math.min(exhaustion,4);
            f = xStart - getWidth(exhaustion, 4) + 80;
            //draw exhaustion
            GlStateManager.color(1, 1, 1, .25f);
            drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
        }

        //draw food amount
        int h1 = (int) Math.floor(food);

        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent) h1 = (int) food * 5;
        int c = Integer.decode(colors.hungerBarColor);
        drawStringOnHUD(h1 + "", xStart + 9 * i3 + 82, yStart - 1, c, 0);

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
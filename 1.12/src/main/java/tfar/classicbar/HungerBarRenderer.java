package tfar.classicbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ModConfig.displayIcons;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the hunger bar
 */

public class HungerBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    public HungerBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHungerBar(RenderGameOverlayEvent.Pre event) {


        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD
                || event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) return;
        event.setCanceled(true);
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        double food = player.getFoodStats().getFoodLevel();
        double saturation = player.getFoodStats().getSaturationLevel();
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
            float f = xStart+80-getWidth(food,20);
            GlStateManager.color(.75f,.3f,0);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(food,20), 7);

        if (saturation>0) {

            //draw saturation

            GlStateManager.color(1,.8f,0);
            f += getWidth(food,20)-getWidth(saturation,20);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(saturation,20), 7);

        }

        //draw food amount
        int h1 = (int) Math.floor(food);

        int c = 0x994F00;
        int i3 = displayIcons ? 1 : 0;

        drawStringOnHUD(h1 + "", xStart + 81 + 10 * i3, yStart - 1, c, 0);
        //Reset back to normal settings

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;

        if (displayIcons) {
            int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
            //Draw food icon
            //food background
            drawTexturedModalRect(xStart + 82, yStart, 16, 27, 9, 9);
            //food
            drawTexturedModalRect(xStart + 82, yStart, 52, 27, 9, 9);

        }

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }
}
package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.Color;
import toughasnails.api.TANCapabilities;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.thirst.ThirstHandler;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static toughasnails.handler.thirst.ThirstOverlayHandler.OVERLAY;

/*
    Class handles the drawing of the thirst bar
 */

public class ThirstBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    public ThirstBarRenderer() {
    }

    @SubscribeEvent//(priority = EventPriority.HIGH)
    public void renderThirstBar(RenderGameOverlayEvent.Post event) {
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL ||
             !SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST) ||
                 !(renderViewEntity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) renderViewEntity;
        if (player.capabilities.isCreativeMode)return;
        IThirst thirstStats = player.getCapability(TANCapabilities.THIRST, null);
        double thirst = thirstStats.getThirst();
        double hydration = thirstStats.getHydration();
        double thirstExhaustion = thirstStats.getExhaustion();
      //  System.out.println(thirstExhaustion);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        int xStart = scaledWidth / 2 + 10;

        int yStart = scaledHeight - GuiIngameForge.right_height;
        GuiIngameForge.right_height +=10;

        mc.profiler.startSection("thirst");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int backgroundOffset = 0;
        int iconIndex = 0;
        boolean dehydration = false;
        if(player.isPotionActive(TANPotions.thirst)) {
            iconIndex += 4;
            backgroundOffset += 117;
            dehydration= true;
        }

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on thirst amount

        float f = xStart+79-getWidth(thirst,20);
        hex2Color((dehydration) ? mods.deHydrationBarColor : mods.thirstBarColor).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(thirst,20), 7);

        //draw hydration if present
        if (hydration>0){
            f = xStart + 79 - getWidth(hydration, 20);
            hex2Color((dehydration) ? mods.deHydrationSecondaryBarColor : mods.hydrationBarColor).color2Gl();
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hydration,20), 7);
        }

        //draw thirst exhaustion
    /*    normalFractions = xStart - getWidth(thirstStats.getExhaustion(), 4) + 80;
        GlStateManager.color(1, 1, 1, .25f);
        drawTexturedModalRect(normalFractions, yStart + 1, 1, 28, getWidth(thirstStats.getExhaustion(), 4), 9);*/

        if (true/*general.overlays.hunger.showExhaustionOverlay*/) {
            thirstExhaustion = Math.min(thirstExhaustion,4);
            f = xStart - getWidth(thirstExhaustion, 4) + 80;
            //draw exhaustion
            GlStateManager.color(1, 1, 1, .25f);
            drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(thirstExhaustion, 4), 9);
        }

        //draw thirst amount
        int h1 = (int) Math.floor(thirst);
        int c = Integer.decode((dehydration) ? mods.deHydrationBarColor : mods.thirstBarColor);
        if (numbers.showPercent)h1 = (int)thirst*5;
        if (numbers.showThirstNumbers)drawStringOnHUD(h1 + "", xStart + 9 * ((general.displayIcons) ? 1 : 0) + rightTextOffset, yStart - 1, c);
        //Reset back to normal settings
        Color.reset();

        mc.getTextureManager().bindTexture(OVERLAY);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw thirst icon

            drawTexturedModalRect(xStart + 82, yStart, backgroundOffset, 16, 9,9);
                drawTexturedModalRect(xStart + 82, yStart, (iconIndex + 4) * 9, 16, 9, 9);
        }
        mc.getTextureManager().bindTexture(ICON_VANILLA);

        //GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }
}
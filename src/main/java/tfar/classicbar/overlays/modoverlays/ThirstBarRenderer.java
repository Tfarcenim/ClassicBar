package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;
import toughasnails.api.TANCapabilities;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.IThirst;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static toughasnails.handler.thirst.ThirstOverlayHandler.OVERLAY;

/*
    Class handles the drawing of the thirst bar
 */

public class ThirstBarRenderer implements IBarOverlay {

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
  }

  @Override
  public void render(EntityPlayer player,int width, int height) {

    IThirst thirstStats = player.getCapability(TANCapabilities.THIRST, null);
    double thirst = thirstStats.getThirst();
    double hydration = thirstStats.getHydration();
    double thirstExhaustion = thirstStats.getExhaustion();

    //Push to avoid lasting changes

    int xStart = width / 2 + 10;

    int yStart = width - GuiIngameForge.right_height;

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

    GlStateManager.pushMatrix();
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

    if (true/*general.overlayorder.hunger.showExhaustionOverlay*/) {
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


    if (general.displayIcons) {
      //Draw thirst icon
      mc.getTextureManager().bindTexture(OVERLAY);

      drawTexturedModalRect(xStart + 82, yStart, backgroundOffset, 16, 9,9);
      drawTexturedModalRect(xStart + 82, yStart, (iconIndex + 4) * 9, 16, 9, 9);
    }
    mc.getTextureManager().bindTexture(ICON_VANILLA);

    //GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "thirst";
  }
}
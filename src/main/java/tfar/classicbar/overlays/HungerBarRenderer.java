package tfar.classicbar.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.Effects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.classicbar.Color;


import static tfar.classicbar.ColorUtils.*;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the hunger bar
 */

public class HungerBarRenderer {

  private float alpha = 0;
  private boolean increase = true;
  private final Minecraft mc = Minecraft.getInstance();

  public HungerBarRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void renderHungerBar(RenderGameOverlayEvent.Pre event) {
    Entity renderViewEntity = mc.getRenderViewEntity();
    if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD || !(renderViewEntity instanceof PlayerEntity)
            ||event.isCanceled()) return;
    PlayerEntity player = (PlayerEntity) renderViewEntity;
    event.setCanceled(true);
    if (player.getRidingEntity() != null)return;
    double hunger = player.getFoodStats().getFoodLevel();
    //double maxHunger = ClassicBar.IBLIS ? IblisHelper.getMaxHunger(player) : 20;
    double maxHunger = 20;
    double currentSat = player.getFoodStats().getSaturationLevel();
    float exhaustion = getExhaustion(player);
    int scaledWidth = mc.mainWindow.getScaledWidth();
    int scaledHeight = mc.mainWindow.getScaledHeight();
    //Push to avoid lasting changes
    int xStart = scaledWidth / 2 + 10;
    int yStart = scaledHeight - 39;

    mc.getProfiler().startSection("hunger");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    float alpha2 = hunger / maxHunger <= general.overlays.lowHungerThreshold && general.overlays.lowHungerWarning ? (int) (System.currentTimeMillis() / 250) % 2 : 1;

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //draw portion of bar based on hunger amount
    int f = xStart + 79 - getWidth(hunger, maxHunger);

    boolean flag = player.isPotionActive(Effects.HUNGER);

    hex2Color(flag ?  colors.hungerBarDebuffColor :colors.hungerBarColor).color2Gla(alpha2);
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hunger, maxHunger), 7);


    if (currentSat > 0 && general.overlays.hunger.showSaturationBar) {

      //draw saturation
      hex2Color(flag ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(alpha2);
      f += getWidth(hunger, maxHunger) - getWidth(currentSat, maxHunger);
      drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(currentSat, maxHunger), 7);
    }
    //render held hunger overlay

    if (general.overlays.hunger.showHeldFoodOverlay &&
            player.getHeldItemMainhand().getItem().isFood() ){
      ItemStack stack = player.getHeldItemMainhand();
      if (increase)alpha+=general.overlays.hunger.transitionSpeed;
      else alpha-=general.overlays.hunger.transitionSpeed;
      if (alpha>=1)increase = false;
      else if (alpha<=0) increase = true;
      Food food = stack.getItem().getFood();
      double hungerOverlay = food.getHealing();
      double saturationMultiplier = food.getSaturation();
      double potentialSat = 2*hungerOverlay*saturationMultiplier;


      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger-hunger,hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger <maxHunger) {
        f = xStart - getWidth(hungerWidth+hunger,maxHunger) + 78;
        hex2Color(flag ?  colors.hungerBarDebuffColor :colors.hungerBarColor).color2Gla(alpha);
        drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(hunger+hungerOverlay, maxHunger), 7);
      }

      //Draw Potential saturation
      if (general.overlays.hunger.showSaturationBar){
        //maximum potential saturation cannot combine with current saturation to go over 20
        double saturationWidth = Math.min(potentialSat,maxHunger - currentSat);

        //Potential Saturation cannot go over potential hunger + current hunger combined
        saturationWidth = Math.min(saturationWidth,hunger + hungerWidth);
        saturationWidth = Math.min(saturationWidth,hungerOverlay + hunger);
        if ((potentialSat + currentSat)>(hunger+hungerWidth)){
          double diff = (potentialSat + currentSat) - (hunger+hungerWidth);
          saturationWidth = potentialSat - diff;
        }
        //offset used to decide where to place the bar
        f = xStart - getWidth(saturationWidth+currentSat,maxHunger) + 78;
        hex2Color(flag ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(alpha);
        if (true)//currentSat > 0)
          drawTexturedModalRect(f + 1, yStart+1, 1, 10, getWidth(saturationWidth+currentSat,maxHunger), 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (general.overlays.hunger.showExhaustionOverlay) {
      exhaustion = Math.min(exhaustion,4);
      f = xStart - getWidth(exhaustion, 4) + 80;
      //draw exhaustion
      GlStateManager.color4f(1, 1, 1, .25f);
      drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
    }

    //draw hunger amount
    int h1 = (int) Math.floor(hunger);

    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) hunger * 5;
    int c = Integer.decode(flag ? colors.hungerBarDebuffColor :colors.hungerBarColor);
    drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);

    //Reset back to normal settings
    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);
    ForgeIngameGui.left_height += 10;

    if (general.displayIcons) {

      int k5 = 52;
      int k6 = 16;
      if (flag) {k5 += 36;k6 = k5 + 45;}
      //Draw hunger icon
      //hunger background
      drawTexturedModalRect(xStart + 82, yStart, k6, 27, 9, 9);

      //hunger
      drawTexturedModalRect(xStart + 82, yStart, k5, 27, 9, 9);
    }
    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.getProfiler().endSection();
    event.setCanceled(true);
  }
}
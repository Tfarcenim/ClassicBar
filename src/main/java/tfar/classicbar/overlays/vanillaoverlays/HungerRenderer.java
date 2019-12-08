package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.Color;
import tfar.classicbar.ModUtils;
import tfar.classicbar.compat.HungerHelper;
import tfar.classicbar.compat.VampireHelper;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class HungerRenderer implements IBarOverlay {

  private float foodAlpha = 0;
  private boolean foodIncrease = true;

  public boolean side;

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return !ClassicBar.VAMPIRISM || !VampireHelper.isVampire(player);//player.getRidingEntity() == null;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {
    double hunger = player.getFoodStats().getFoodLevel();
    double maxHunger = HungerHelper.getMaxHunger(player);
    double currentSat = player.getFoodStats().getSaturationLevel();
    float exhaustion = getExhaustion(player);
    //Push to avoid lasting changes
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    mc.profiler.startSection("hunger");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    float alpha2 = hunger / maxHunger <= general.overlays.lowHungerThreshold && general.overlays.lowHungerWarning ? (int) (Minecraft.getSystemTime() / 250) % 2 : 1;


    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
    //draw portion of bar based on hunger amount
    float f = xStart + 79 - getWidth(hunger, maxHunger);
    boolean hungerActive = player.isPotionActive(MobEffects.HUNGER);
    hex2Color(hungerActive ? colors.hungerBarDebuffColor : colors.hungerBarColor).color2Gla(alpha2);
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hunger, maxHunger), 7);
    if (currentSat > 0 && general.overlays.hunger.showSaturationBar) {
      //draw saturation
      hex2Color(hungerActive ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(alpha2);
      f += getWidth(hunger, maxHunger) - getWidth(currentSat, maxHunger);
      ModUtils.drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(currentSat, maxHunger), 7);
    }
    //render held hunger overlay
    if (general.overlays.hunger.showHeldFoodOverlay &&
            player.getHeldItemMainhand().getItem() instanceof ItemFood) {
      ItemStack stack = player.getHeldItemMainhand();
      if (foodIncrease) foodAlpha += general.overlays.hunger.transitionSpeed;
      else foodAlpha -= general.overlays.hunger.transitionSpeed;
      if (foodAlpha >= 1) foodIncrease = false;
      else if (foodAlpha <= 0) foodIncrease = true;
      ItemFood foodItem = ((ItemFood) stack.getItem());
      double hungerOverlay = foodItem.getHealAmount(stack);
      double saturationMultiplier = foodItem.getSaturationModifier(stack);
      double potentialSat = 2 * hungerOverlay * saturationMultiplier;

      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger < maxHunger) {
        f = xStart - getWidth(hungerWidth + hunger, maxHunger) + 78;
        hex2Color(hungerActive ? colors.hungerBarDebuffColor : colors.hungerBarColor).color2Gla(foodAlpha);
        drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(hunger + hungerOverlay, maxHunger), 7);
      }

      //Draw Potential saturation
      if (general.overlays.hunger.showSaturationBar) {
        //maximum potential saturation cannot combine with current saturation to go over 20
        double saturationWidth = Math.min(potentialSat, maxHunger - currentSat);

        //Potential Saturation cannot go over potential hunger + current hunger combined
        saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
        saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
        if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
          double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
          saturationWidth = potentialSat - diff;
        }
        //offset used to decide where to place the bar
        f = xStart - getWidth(saturationWidth + currentSat, maxHunger) + 78;
        hex2Color(hungerActive ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(foodAlpha);
        if (true)//currentSat > 0)
          drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(saturationWidth + currentSat, maxHunger), 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (general.overlays.hunger.showExhaustionOverlay) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart - getWidth(exhaustion, 4) + 80;
      //draw exhaustion
      GlStateManager.color(1, 1, 1, .25f);
      drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
    }

    //Revert our state back
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showHungerNumbers;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //draw hunger amount
    double hunger = player.getFoodStats().getFoodLevel();
    boolean hungerActive = player.isPotionActive(MobEffects.HUNGER);

    int h1 = (int) Math.floor(hunger);

    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) hunger * 5;
    int c = Integer.decode(hungerActive ? colors.hungerBarDebuffColor : colors.hungerBarColor);
    if (numbers.showHungerNumbers) drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    mc.getTextureManager().bindTexture(Gui.ICONS);
    boolean hungerActive = player.isPotionActive(MobEffects.HUNGER);

    int k5 = 52;
    int k6 = 16;
    if (hungerActive) {
      k5 += 36;
      k6 = k5 + 45;
    }
    //Draw hunger icon
    //hunger background
    drawTexturedModalRect(xStart + 82, yStart, k6, 27, 9, 9);

    //hunger
    drawTexturedModalRect(xStart + 82, yStart, k5, 27, 9, 9);

  }

  @Override
  public String name() {
    return "food";
  }
}

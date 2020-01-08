package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import tfar.classicbar.Color;
import tfar.classicbar.ModUtils;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class Hunger implements IBarOverlay {

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
  public boolean shouldRender(PlayerEntity player) {
    return true;//!ClassicBar.VAMPIRISM || !VampireHelper.isVampire(player);//player.getRidingEntity() == null;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {
    double hunger = player.getFoodStats().getFoodLevel();
    double maxHunger = 20;//HungerHelper.getMaxHunger(player);
    double currentSat = player.getFoodStats().getSaturationLevel();
    float exhaustion = getExhaustion(player);
    //Push to avoid lasting changes
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    float alpha2 = hunger / maxHunger <= lowHungerThreshold.get() && lowHungerWarning.get() ? (int) (System.currentTimeMillis() / 250) % 2 : 1;


    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
    //draw portion of bar based on hunger amount
    int f = xStart + 79 - getWidth(hunger, maxHunger);
    boolean hungerActive = player.isPotionActive(Effects.HUNGER);
    hex2Color(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get()).color2Gla(alpha2);
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hunger, maxHunger), 7);
    if (currentSat > 0 && showSaturationBar.get()) {
      //draw saturation
      hex2Color(hungerActive ? saturationBarDebuffColor.get() : saturationBarColor.get()).color2Gla(alpha2);
      f += getWidth(hunger, maxHunger) - getWidth(currentSat, maxHunger);
      ModUtils.drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(currentSat, maxHunger), 7);
    }
    //render held hunger overlay
    if (showHeldFoodOverlay.get() &&
            player.getHeldItemMainhand().getItem().isFood()) {
      ItemStack stack = player.getHeldItemMainhand();
      double time = System.currentTimeMillis()/1000d * transitionSpeed.get();
      double foodAlpha = Math.sin(time)/2 + .5;

      Food food = stack.getItem().getFood();
      double hungerOverlay = food.getHealing();
      double saturationMultiplier = food.getSaturation();
      double potentialSat = 2 * hungerOverlay * saturationMultiplier;

      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger < maxHunger) {
        f = xStart - getWidth(hungerWidth + hunger, maxHunger) + 78;
        hex2Color(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get()).color2Gla((float)foodAlpha);
        drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(hunger + hungerOverlay, maxHunger), 7);
      }

      //Draw Potential saturation
      if (showSaturationBar.get()) {
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
        hex2Color(hungerActive ? saturationBarDebuffColor.get() : saturationBarColor.get()).color2Gla((float)foodAlpha);
        if (true)//currentSat > 0)
          drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(saturationWidth + currentSat, maxHunger), 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (showExhaustionOverlay.get()) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart - getWidth(exhaustion, 4) + 80;
      //draw exhaustion
      RenderSystem.color4f(1, 1, 1, .25f);
      drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
    }

    //Revert our state back
    RenderSystem.disableBlend();
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showHungerNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //draw hunger amount
    double hunger = player.getFoodStats().getFoodLevel();
    boolean hungerActive = player.isPotionActive(Effects.HUNGER);

    int h1 = (int) Math.floor(hunger);

    int i3 = displayIcons.get() ? 1 : 0;
    if (showPercent.get()) h1 = (int) hunger * 5;
    int c = Integer.decode(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get());
    drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    boolean hungerActive = player.isPotionActive(Effects.HUNGER);

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
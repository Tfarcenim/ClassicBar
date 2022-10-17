package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import tfar.classicbar.Color;
import tfar.classicbar.ModUtils;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.overlays.BarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class Hunger implements BarOverlay {

  public boolean side;

  @Override
  public BarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(Player player) {
    return !Helpers.vampirismloaded || !Helpers.isVampire(player);
  }

  @Override
  public void renderBar(PoseStack matrices, Player player, int screenWidth, int screenHeight) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;//HungerHelper.getMaxHunger(player);
    double currentSat = player.getFoodData().getSaturationLevel();
    float exhaustion = getExhaustion(player);
    //Push to avoid lasting changes
    int xStart = screenWidth / 2 + 10;
    int yStart = screenHeight - getSidedOffset();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();

    boolean warn = hunger / maxHunger <= lowHungerThreshold.get() && lowHungerWarning.get();

    long alpha2 = warn ? (System.currentTimeMillis() / 250) % 2 : 1;

    //Bar background
    Color.reset();
    drawTexturedModalRect(matrices,xStart, yStart, 0, 0, 81, 9);
    //draw portion of bar based on hunger amount
    int f = xStart + 79 - getWidth(hunger, maxHunger);
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);
    hex2Color(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get()).color2Gla(alpha2);
    drawTexturedModalRect(matrices,f, yStart + 1, 1, 10, getWidth(hunger, maxHunger), 7);
    if (currentSat > 0 && showSaturationBar.get()) {
      //draw saturation
      hex2Color(hungerActive ? saturationBarDebuffColor.get() : saturationBarColor.get()).color2Gla(alpha2);
      f += getWidth(hunger, maxHunger) - getWidth(currentSat, maxHunger);
      ModUtils.drawTexturedModalRect(matrices,f, yStart + 1, 1, 10, getWidth(currentSat, maxHunger), 7);
    }
    //render held hunger overlay
    if (showHeldFoodOverlay.get() &&
            player.getMainHandItem().getItem().isEdible()) {
      ItemStack stack = player.getMainHandItem();
      double time = System.currentTimeMillis()/1000d * transitionSpeed.get();
      double foodAlpha = Math.sin(time)/2 + .5;

      FoodProperties food = stack.getItem().getFoodProperties();
      double hungerOverlay = food.getNutrition();
      double saturationMultiplier = food.getSaturationModifier();
      double potentialSat = 2 * hungerOverlay * saturationMultiplier;

      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger < maxHunger) {
        f = xStart - getWidth(hungerWidth + hunger, maxHunger) + 78;
        hex2Color(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get()).color2Gla((float)foodAlpha);
        drawTexturedModalRect(matrices,f + 1, yStart + 1, 1, 10, getWidth(hunger + hungerOverlay, maxHunger), 7);
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
          drawTexturedModalRect(matrices,f + 1, yStart + 1, 1, 10, getWidth(saturationWidth + currentSat, maxHunger), 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (showExhaustionOverlay.get()) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart - getWidth(exhaustion, 4) + 80;
      //draw exhaustion
      RenderSystem.color4f(1, 1, 1, .25f);
      drawTexturedModalRect(matrices,f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
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
  public void renderText(PoseStack stack,Player player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //draw hunger amount
    double hunger = player.getFoodData().getFoodLevel();
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

    int h1 = (int) Math.floor(hunger);

    int i3 = displayIcons.get() ? 1 : 0;
    if (showPercent.get()) h1 = (int) hunger * 5;
    int c = Integer.decode(hungerActive ? hungerBarDebuffColor.get() : hungerBarColor.get());
    drawStringOnHUD(stack,h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(PoseStack stack,Player player, int width, int height) {

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    mc.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

    int k5 = 52;
    int k6 = 16;
    if (hungerActive) {
      k5 += 36;
      k6 = k5 + 45;
    }
    //Draw hunger icon
    //hunger background
    drawTexturedModalRect(stack,xStart + 82, yStart, k6, 27, 9, 9);

    //hunger
    drawTexturedModalRect(stack,xStart + 82, yStart, k5, 27, 9, 9);

  }

  @Override
  public String name() {
    return "food";
  }
}
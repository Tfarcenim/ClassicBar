package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Hunger extends BarOverlayImpl {

  public Hunger() {
    super("food");
  }

  @Override
  public boolean shouldRender(Player player) {
    return !Helpers.vampirismloaded || !Helpers.isVampire(player);
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack matrices, Player player, int screenWidth, int screenHeight, int vOffset) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;//HungerHelper.getMaxHunger(player);
    
    double barWidthH = getBarWidth(player);
    
    double currentSat = player.getFoodData().getSaturationLevel();
    double maxSat = maxHunger;
    double barWidthS = getSatBarWidth(player);
    float exhaustion = player.getFoodData().getExhaustionLevel();

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;

    //Bar background
    Color.reset();
    renderFullBarBackground(matrices,xStart,yStart);
    //draw portion of bar based on hunger amount
    double f = xStart + (rightHandSide() ? ModUtils.WIDTH - barWidthH : 0);

    Color hungerColor = getSecondaryBarColor(0,player);
    Color satColor = getPrimaryBarColor(0,player);

    hungerColor.color2Gl();
    renderSecondaryBar(matrices,f, yStart,  barWidthH);
    if (currentSat > 0 && ClassicBarsConfig.showSaturationBar.get()) {
      //draw saturation
      satColor.color2Gl();
      f = xStart + (rightHandSide() ? ModUtils.WIDTH - barWidthS : 0);
      renderMainBar(matrices,f, yStart, barWidthS);
    }
    //render held hunger overlay
    if (ClassicBarsConfig.showHeldFoodOverlay.get() &&
            player.getMainHandItem().getItem().isEdible()) {
      ItemStack stack = player.getMainHandItem();
      double time = System.currentTimeMillis()/1000d * ClassicBarsConfig.transitionSpeed.get();
      double foodAlpha = Math.sin(time)/2 + .5;

      FoodProperties food = stack.getItem().getFoodProperties(stack,player);
      double hungerOverlay = food.getNutrition();
      double saturationMultiplier = food.getSaturationModifier();
      double potentialSat = 2 * hungerOverlay * saturationMultiplier;

      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger < maxHunger) {
        f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(hungerWidth + hunger, maxHunger) : 0);
        hungerColor.color2Gla((float)foodAlpha);
        ModUtils.drawTexturedModalRect(matrices,f + 2, yStart + 1, 2, 10, ModUtils.getWidth(hunger + hungerOverlay, maxHunger), 7);
      }

      //Draw Potential saturation
      if (ClassicBarsConfig.showSaturationBar.get()) {
        //maximum potential saturation cannot combine with current saturation to go over 20
        double saturationWidth = Math.min(potentialSat, maxSat - currentSat);

        //Potential Saturation cannot go over potential hunger + current hunger combined
        saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
        saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
        if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
          double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
          saturationWidth = potentialSat - diff;
        }

        double w = ModUtils.getWidth(saturationWidth + currentSat, maxSat);
        //offset used to decide where to place the bar
        f = xStart + (rightHandSide() ? ModUtils.WIDTH - w : 0);
        satColor.color2Gla((float)foodAlpha);
        if (true)//currentSat > 0)
          ModUtils.drawTexturedModalRect(matrices,f + 2, yStart + 1, 2, 10, w, 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (ClassicBarsConfig.showExhaustionOverlay.get()) {
      exhaustion = Math.min(exhaustion, 4);

      double adjBarWidth = ModUtils.getWidth(exhaustion, 4) * 79 / ModUtils.WIDTH;

      f = xStart + (rightHandSide() ? ModUtils.WIDTH + 4 - adjBarWidth : 0);
      //draw exhaustion
      RenderSystem.setShaderColor(1, 1, 1, .25f);
      ModUtils.drawTexturedModalRect(matrices,f, yStart + 1, 1, 28, adjBarWidth, 7);
    }
  }

  @Override
  public double getBarWidth(Player player) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;
    return Math.ceil(ModUtils.WIDTH * hunger / maxHunger);
  }
  
  public int getSatBarWidth(Player player) {
    double saturation = player.getFoodData().getSaturationLevel();
    double maxSat = 20;
    return (int) Math.ceil(ModUtils.WIDTH * saturation/ maxSat);
  }
  //saturation
  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
  }

  //hunger
  @Override
  public Color getSecondaryBarColor(int index, Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
  }

  @Override
  public boolean shouldRenderText() {
    return ClassicBarsConfig.showHungerNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //draw hunger amount
    double hunger = player.getFoodData().getFoodLevel();
    int c = getSecondaryBarColor(0,player).colorToText();
    textHelper(stack,xStart,yStart,hunger,c);
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {

    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

    int k5 = 52;
    int k6 = 16;
    if (hungerActive) {
      k5 += 36;
      k6 = k5 + 45;
    }
    //Draw hunger icon
    //hunger background
    ModUtils.drawTexturedModalRect(stack,xStart, yStart, k6, 27, 9, 9);

    //hunger
    ModUtils.drawTexturedModalRect(stack,xStart, yStart, k5, 27, 9, 9);

  }
}
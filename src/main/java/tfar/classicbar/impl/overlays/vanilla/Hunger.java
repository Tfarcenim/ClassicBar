package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.network.Message;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

// Changed: removed shouldRenderText() override that returned ClassicBarsConfig.showHungerNumbers.get().
// Text visibility is now driven by barSettings.show_text via BarOverlayImpl.
public class Hunger extends BarOverlayImpl {

  public Hunger() {
    super("food");
  }

  @Override
  public boolean shouldRender(Player player) {
    return !ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player);
  }

  @Override
  public void renderBar(Gui gui, GuiGraphics matrices, Player player, int screenWidth, int screenHeight, int vOffset) {
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
    double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthH : 0);

    Color hungerColor = getSecondaryBarColor(0,player);
    Color satColor = getPrimaryBarColor(0,player);

    hungerColor.color2Gl();
    renderPartialBar(matrices,f + 2, yStart + 2,  barWidthH);
    if (currentSat > 0 && ClassicBarsConfig.showSaturationBar.get()) {
      //draw saturation
      satColor.color2Gl();
      f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthS : 0);
      renderPartialBar(matrices,f + 2, yStart + 2, barWidthS);
    }
    //render held hunger overlay
    FoodProperties heldFood = player.getMainHandItem().get(net.minecraft.core.component.DataComponents.FOOD);
    if (ClassicBarsConfig.showHeldFoodOverlay.get() && heldFood != null) {
      double time = System.currentTimeMillis()/1000d * ClassicBarsConfig.transitionSpeed.get();
      double foodAlpha = Math.sin(time)/2 + .5;

      double hungerOverlay = heldFood.nutrition();
      double saturationMultiplier = heldFood.saturation();
      double potentialSat = 2 * hungerOverlay * saturationMultiplier;

      //Draw Potential hunger
      double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
      //don't render the bar at all if hunger is full
      if (hunger < maxHunger) {
        double w = ModUtils.getWidth(hungerWidth + hunger, maxHunger);

        f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - w : 0);
        hungerColor.color2Gla((float)foodAlpha);
        renderPartialBar(matrices,f + 2, yStart + 2, w);
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
        f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - w : 0);
        satColor.color2Gla((float)foodAlpha);
        renderPartialBar(matrices,f + 2, yStart + 2,w);
      }
    }

    if (ClassicBarsConfig.showExhaustionOverlay.get() && Message.presentOnServer) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - ModUtils.getWidth(exhaustion, 4) : 0);
      //draw exhaustion
      RenderSystem.setShaderColor(1, 1, 1, .25f);
      ModUtils.drawTexturedModalRect(matrices,f + 2, yStart + 1, 1, 28, ModUtils.getWidth(exhaustion, 4f), 9);
      RenderSystem.setShaderColor(1, 1, 1, 1); // Changed: added reset after exhaustion overlay; the 0.25f alpha was leaking into subsequent render calls
    }
  }

  @Override
  public double getBarWidth(Player player) {
    double hunger = player.getFoodData().getFoodLevel();
    double maxHunger = 20;
    return Math.ceil(BarOverlayImpl.WIDTH * hunger / maxHunger);
  }
  
  public int getSatBarWidth(Player player) {
    double saturation = Math.min(player.getFoodData().getSaturationLevel(), 20);
    double maxSat = 20;
    return (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / maxSat);
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
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //draw hunger amount
    double hunger = player.getFoodData().getFoodLevel();
    int c = getSecondaryBarColor(0,player).colorToText();
    textHelper(graphics,xStart,yStart,hunger,c);
  }

  private static final ResourceLocation FOOD_EMPTY = ResourceLocation.withDefaultNamespace("hud/food_empty");
  private static final ResourceLocation FOOD_EMPTY_HUNGER = ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
  private static final ResourceLocation FOOD_FULL = ResourceLocation.withDefaultNamespace("hud/food_full");
  private static final ResourceLocation FOOD_FULL_HUNGER = ResourceLocation.withDefaultNamespace("hud/food_full_hunger");

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

    //Draw hunger icon
    //hunger background
    graphics.blitSprite(hungerActive ? FOOD_EMPTY_HUNGER : FOOD_EMPTY, xStart, yStart, 9, 9);
    //hunger
    graphics.blitSprite(hungerActive ? FOOD_FULL_HUNGER : FOOD_FULL, xStart, yStart, 9, 9);

  }
}
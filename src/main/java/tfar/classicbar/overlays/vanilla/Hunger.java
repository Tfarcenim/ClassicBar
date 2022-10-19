package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.config.ClassicBarsConfig.*;
import static tfar.classicbar.util.ModUtils.getStringLength;
import static tfar.classicbar.util.ModUtils.leftTextOffset;

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
    double currentSat = player.getFoodData().getSaturationLevel();
    double maxSat = maxHunger;
    float exhaustion = player.getFoodData().getExhaustionLevel();

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;

    //Bar background
    Color.reset();
    renderFullBarBackground(matrices,xStart,yStart);
    //draw portion of bar based on hunger amount
    double f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(hunger, maxHunger) : 0);
    boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

    Color hungerColor = getSecondaryBarColor(0,player);
    Color satColor = getPrimaryBarColor(0,player);

    hungerColor.color2Gl();
    renderSecondaryBar(matrices,f, yStart,  ModUtils.getWidth(hunger, maxHunger));
    if (currentSat > 0 && showSaturationBar.get()) {
      //draw saturation
      satColor.color2Gl();
      f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(currentSat, maxSat) : 0);
      renderMainBar(matrices,f, yStart, ModUtils.getWidth(currentSat, maxSat));
    }
    //render held hunger overlay
    if (showHeldFoodOverlay.get() &&
            player.getMainHandItem().getItem().isEdible()) {
      ItemStack stack = player.getMainHandItem();
      double time = System.currentTimeMillis()/1000d * transitionSpeed.get();
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
        ModUtils.drawTexturedModalRect(matrices,f + 1, yStart + 1, 1, 10, ModUtils.getWidth(hunger + hungerOverlay, maxHunger), 7);
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
        f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(saturationWidth + currentSat, maxHunger) : 0);
        satColor.color2Gla((float)foodAlpha);
        if (true)//currentSat > 0)
          ModUtils.drawTexturedModalRect(matrices,f + 1, yStart + 1, 1, 10, ModUtils.getWidth(saturationWidth + currentSat, maxHunger), 7);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (showExhaustionOverlay.get()) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(exhaustion, 4) : 0);
      //draw exhaustion
      RenderSystem.setShaderColor(1, 1, 1, .25f);
      ModUtils.drawTexturedModalRect(matrices,f, yStart + 1, 1, 28, ModUtils.getWidth(exhaustion, 4f), 9);
    }
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
    return showHungerNumbers.get();
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
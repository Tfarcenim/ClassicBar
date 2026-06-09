package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.network.PacketHandler;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Food extends BarOverlayImpl {

  protected final boolean showSaturation;
  protected final boolean showExhaustion;
    private final boolean showHeldFood;

    public Food(BarSettings barSettings, boolean showSaturation, boolean showExhaustion,boolean showHeldFood) {
    super("food",barSettings,player -> player.getFoodData().getFoodLevel()/20f);
      this.showSaturation = showSaturation;
      this.showExhaustion = showExhaustion;
      this.showHeldFood = showHeldFood;
    }

  public static final Codec<Food> CODEC = RecordCodecBuilder.create(
          objectInstance -> objectInstance.group(BarSettings.CODEC.fieldOf("bar_settings")
                  .forGetter(Food::getBarSettings),
                  Codec.BOOL.fieldOf("show_saturation").forGetter(f -> f.showSaturation),
                  Codec.BOOL.fieldOf("show_exhaustion").forGetter(f -> f.showExhaustion),
                  Codec.BOOL.fieldOf("show_held_food").forGetter(f -> f.showHeldFood)
          ).apply(objectInstance, Food::new)
  );

  @Override
  public boolean shouldRender(Player player) {
    return super.shouldRender(player) && (!ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player));
  }

  @Override
  public void renderBar(ForgeGui gui, GuiGraphics matrices, Player player, int screenWidth, int screenHeight, int vOffset) {
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
    double f = xStart + (getSide() == BarSide.RIGHT? BarOverlayImpl.WIDTH - barWidthH : 0);

    Color hungerColor = getSecondaryBarColor(player);
    Color satColor = getPrimaryBarColor(player);

    renderPartialBar(hungerColor,matrices,f + 2, yStart + 2,  barWidthH);
    if (currentSat > 0 && showSaturation) {
      //draw saturation
      f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - barWidthS : 0);
      renderPartialBar(satColor,matrices,f + 2, yStart + 2, barWidthS);
    }
    //render held hunger overlay
    if (showHeldFood && player.getMainHandItem().getItem().isEdible()) {
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
        double w = ModUtils.getWidth(hungerWidth + hunger, maxHunger);

        f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - w : 0);
        renderPartialBar(hungerColor.withAlpha((float) foodAlpha),matrices,f + 2, yStart + 2, w);
      }

      //Draw Potential saturation
      if (showSaturation) {
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
        f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - w : 0);
        ;
        if (true)//currentSat > 0)
          renderPartialBar(satColor.withAlpha((float)foodAlpha),matrices,f + 2, yStart + 2,w);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (showExhaustion && PacketHandler.presentOnServer) {
      exhaustion = Math.min(exhaustion, 4);
      f = xStart + (getSide()  == BarSide.RIGHT ? BarOverlayImpl.WIDTH - ModUtils.getWidth(exhaustion, 4) : 0);
      //draw exhaustion
      RenderSystem.setShaderColor(1, 1, 1, .25f);
      ModUtils.drawTexturedModalRect(BAR,matrices,f + 2, yStart + 1, 1, 28, ModUtils.getWidth(exhaustion, 4f), 9);
      RenderSystem.setShaderColor(1, 1, 1, 1);
    }
  }

  @Override
  public Codec<? extends BarOverlayImpl> getCodec() {
    return CODEC;
  }
  
  public int getSatBarWidth(Player player) {
    double saturation = player.getFoodData().getSaturationLevel();
    double maxSat = 20;
    return (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / maxSat);
  }
  //saturation
  public Color getPrimaryBarColor(Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
  }

  //hunger
  public Color getSecondaryBarColor(Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //draw hunger amount
    double hunger = player.getFoodData().getFoodLevel();
    int c = getSecondaryBarColor(player).colorToText();
    textHelper(graphics,xStart,yStart,hunger,c);
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

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
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, k6, 27, 9, 9);
    //hunger
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, k5, 27, 9, 9);

  }
}
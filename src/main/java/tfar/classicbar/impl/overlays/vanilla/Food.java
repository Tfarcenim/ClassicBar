package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.network.PacketHandler;
import tfar.classicbar.api.Color;
import tfar.classicbar.util.ModUtils;

public class Food extends BarOverlayImpl {

  protected final boolean showSaturation;
  protected final boolean showExhaustion;
  private final boolean showHeldFood;

  public static final BarInfo INFO = BarInfo.createSimpleVanilla("food",
          player -> (!ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player))
          ,player -> player.getFoodData().getFoodLevel(),fixed(FoodConstants.MAX_FOOD));

  public Food(BarSettings barSettings, boolean showSaturation, boolean showExhaustion,boolean showHeldFood) {
    super(INFO,barSettings);
    this.showSaturation = showSaturation;
    this.showExhaustion = showExhaustion;
    this.showHeldFood = showHeldFood;
  }

  public static final Codec<Food> CODEC = RecordCodecBuilder.create(
          objectInstance -> codecStart(objectInstance)
                  .and(Codec.BOOL.fieldOf("show_saturation").forGetter(f -> f.showSaturation))
                  .and(Codec.BOOL.fieldOf("show_exhaustion").forGetter(f -> f.showExhaustion))
                  .and(Codec.BOOL.fieldOf("show_held_food").forGetter(f -> f.showHeldFood)
          ).apply(objectInstance, Food::new)
  );

  @Override
  public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    double hunger = barInfo.numerator().getValue(player);
    double maxHunger = barInfo.denominator().getValue(player);//HungerHelper.getMaxHunger(player);
    
    int barWidthH = getBarWidth(player);

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;

    //Bar background
    renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);
    //draw portion of bar based on hunger amount
    float f = xStart + (getSide() == BarSide.RIGHT? BarOverlayImpl.WIDTH - barWidthH : 0);

    Color hungerColor = getHungerBarColor(player);
    Color satColor = getSaturationBarColor(player);

    renderPartialBar(hungerColor,graphics,f + 2, yStart + 2,  barWidthH);
    float currentSat = player.getFoodData().getSaturationLevel();
    if (currentSat > 0 && showSaturation) {
      int barWidthS = getSatBarWidth(player);
      //draw saturation
      f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - barWidthS : 0);
      renderPartialBar(satColor,graphics,f + 2, yStart + 2, barWidthS);
    }

  }

  @Override
  public void renderBarDecorations(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    double hunger = barInfo.numerator().getValue(player);
    double maxHunger = barInfo.denominator().getValue(player);//HungerHelper.getMaxHunger(player);
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
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
        int w = BarOverlayImpl.getWidth(hungerWidth + hunger, maxHunger);

        float f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - w : 0);
        Color hungerColor = getHungerBarColor(player);

        renderPartialBar(hungerColor.withAlpha((float) foodAlpha),graphics,f + 2, yStart + 2, w);
      }

      //Draw Potential saturation
      if (showSaturation) {
        float currentSat = player.getFoodData().getSaturationLevel();
        //maximum potential saturation cannot combine with current saturation to go over 20
        double saturationWidth = Math.min(potentialSat, maxHunger - currentSat);
        Color satColor = getSaturationBarColor(player);

        //Potential Saturation cannot go over potential hunger + current hunger combined
        saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
        saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
        if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
          double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
          saturationWidth = potentialSat - diff;
        }

        int w = BarOverlayImpl.getWidth(saturationWidth + currentSat, maxHunger);

        //offset used to decide where to place the bar
        float f = xStart + (getSide()  == BarSide.RIGHT? BarOverlayImpl.WIDTH - w : 0);
        ;
        if (true)//currentSat > 0)
          renderPartialBar(satColor.withAlpha((float)foodAlpha),graphics,f + 2, yStart + 2,w);
        else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

      }
    }

    if (showExhaustion && PacketHandler.presentOnServer) {
      float exhaustion = player.getFoodData().getExhaustionLevel();
      exhaustion = Math.min(exhaustion, FoodConstants.EXHAUSTION_DROP);
      int barWidthE = BarOverlayImpl.getWidth(exhaustion, FoodConstants.EXHAUSTION_DROP);
      xStart = getXStartBar(screenWidth,barWidthE);
      //draw exhaustion
      RenderSystem.setShaderColor(1, 1, 1, .25f);
      ModUtils.drawTexturedModalRect(BAR,graphics,xStart + 2, yStart + 1, 1, 28, exhaustion, 9);
    }
  }

  @Override
  public Codec<? extends BarOverlayImpl> codec() {
    return CODEC;
  }
  
  public int getSatBarWidth(Player player) {
    double saturation = player.getFoodData().getSaturationLevel();
    return (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / FoodConstants.MAX_SATURATION);
  }
  //saturation
  public Color getSaturationBarColor(Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
  }

  //hunger
  public Color getHungerBarColor(Player player) {
    boolean hunger = player.hasEffect(MobEffects.HUNGER);
    return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //draw hunger amount
    double hunger = barInfo.numerator().getValue(player);
    int c = getHungerBarColor(player).colorToText();
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
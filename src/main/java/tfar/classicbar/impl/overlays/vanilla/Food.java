package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.overlays.templates.BarOverlayImpl;
import tfar.classicbar.impl.overlays.templates.FoodLikeBarOverlay;
import tfar.classicbar.util.ModUtils;

public class Food extends FoodLikeBarOverlay {

  public static final BarInfo INFO = BarInfo.createSimpleVanilla("food",
          player -> (!ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player))
          ,player -> player.getFoodData().getFoodLevel(),fixed(FoodConstants.MAX_FOOD));

  public Food(BarSettings barSettings, boolean showSaturation, boolean showExhaustion,boolean showHeldFood) {
    super(INFO, CODEC,() -> FoodConstants.EXHAUSTION_DROP,p -> p.getFoodData().getExhaustionLevel(), barSettings, showSaturation, showExhaustion, showHeldFood);
  }

  public static final Codec<Food> CODEC = RecordCodecBuilder.create(
          objectInstance -> codecStartFoodLike(objectInstance).apply(objectInstance, Food::new));


  @Override
  public boolean isHealingItem(ItemStack stack, Player player) {
    return stack.getFoodProperties(player) != null;
  }

  @Override
  public int getPotentialHealing(ItemStack stack, Player player) {
    return stack.getItem().getFoodProperties(stack,player).getNutrition();
  }

  @Override
  public float getSaturationValue(Player player) {
    return player.getFoodData().getSaturationLevel();
  }

  @Override
  public float getPotentialSaturationMultiplier(ItemStack stack,Player player) {
    FoodProperties food = stack.getItem().getFoodProperties(stack,player);
    return food.getSaturationModifier();
  }

  public int getSatBarWidth(Player player) {
    double saturation = player.getFoodData().getSaturationLevel();
    return (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / FoodConstants.MAX_SATURATION);
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int vOffset) {

    int xStart = graphics.guiWidth() / 2 + getIconOffset();
    int yStart = graphics.guiHeight() - vOffset;
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
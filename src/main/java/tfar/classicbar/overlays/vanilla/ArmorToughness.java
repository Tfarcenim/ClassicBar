package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;

public class ArmorToughness  extends BarOverlayImpl {

  public ArmorToughness() {
    super("armor_toughness");
  }

  @Override
  public boolean shouldRender(Player player) {
    return ClassicBarsConfig.displayToughnessBar.get() && player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue() >= 1;
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
    //armor toughness stuff
    double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
    //Push to avoid lasting changes
    int xStart = screenWidth / 2 + 10;
    int yStart = screenHeight - vOffset;
    double f;
    //draw bar background portion
    Color.reset();
    int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ClassicBarsConfig.armorToughnessColorValues.get().size() - 1);

    if (index == 0) {
      f = xStart + 79 - getWidth(armorToughness, 20);
      if (isFitted()) drawScaledBar(stack,armorToughness, 20,(int) f - 1, yStart, false);
      else drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);

      //calculate bar color
      hex2Color(ClassicBarsConfig.armorToughnessColorValues.get().get(0)).color2Gl();
      //draw portion of bar based on armor toughness amount
      drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(armorToughness, 20), 7);

    } else {
      drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      int size = ClassicBarsConfig.armorToughnessColorValues.get().size();
      //if we are out of colors wrap the bar
      if (index < size && armorToughness % 20 != 0) {

        //draw complete first bar
        hex2Color(ClassicBarsConfig.armorToughnessColorValues.get().get(index - 1)).color2Gl();
        drawTexturedModalRect(stack,xStart, yStart + 1, 0, 10, 79, 7);

        //draw partial second bar
        f = xStart + 79 - getWidth(armorToughness % 20, 20);

        hex2Color(ClassicBarsConfig.armorToughnessColorValues.get().get(index)).color2Gl();
        drawTexturedModalRect(stack,f, yStart + 1, 0, 10, getWidth(armorToughness % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20, or it is capped
      else {
        //draw complete second bar
        hex2Color(ClassicBarsConfig.armorToughnessColorValues.get().get(index)).color2Gl();
        drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
    }
  }

  @Override
  public boolean isFitted() {
    return !ClassicBarsConfig.fullToughnessBar.get();
  }

  @Override
  public boolean shouldRenderText() {
    return ClassicBarsConfig.showArmorToughnessNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
    int xStart = width / 2 + getHOffset();
    int yStart = height - vOffset;
    double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
    int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ClassicBarsConfig.armorToughnessColorValues.get().size() - 1);
    //draw armor toughness amount
    int iq1 = (int) Math.floor(armorToughness);
    int iq2 = ConfigCache.icons ? 1 : 0;

    int toughnesscolor = Integer.decode(ClassicBarsConfig.armorToughnessColorValues.get().get(index));
    drawStringOnHUD(stack,iq1 + "", xStart + 9 * iq2 + rightTextOffset, yStart - 1, toughnesscolor);
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + 10;
    int yStart = height - vOffset;
    //Draw armor toughness icon
    drawTexturedModalRect(stack,xStart + 82, yStart, 83, 0, 9, 9);
  }

  @Override
  public ResourceLocation getIconRL() {
    return ICON_BAR;
  }

}

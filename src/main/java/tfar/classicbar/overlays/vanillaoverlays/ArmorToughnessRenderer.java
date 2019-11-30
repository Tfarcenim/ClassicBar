package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.config.ModConfig.numbers;

public class ArmorToughnessRenderer implements IBarOverlay {

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return general.overlays.displayToughnessBar && player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue() >= 1;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {
    //armor toughness stuff
    double armorToughness = player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
    //Push to avoid lasting changes
    int xStart = width / 2 + 10;
    int yStart = height - GuiIngameForge.right_height;
    mc.profiler.startSection("armortoughness");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    int f;

    //Bar background
    if (general.displayIcons)
      //Draw armor toughness icon
      drawTexturedModalRect(xStart + 82, yStart, 83, 0, 9, 9);
    //draw bar portion
    int toughnessindex = (int) Math.min(Math.ceil(armorToughness / 20) - 1, colors.advancedColors.armorToughnessColorValues.length - 1);

    if (armorToughness <= 20) {
      f = xStart + 79 - getWidth(armorToughness, 20);
      if (!general.overlays.fullToughnessBar) drawScaledBar(armorToughness, 20, f - 1, yStart, false);
      else drawTexturedModalRect(f, yStart, 0, 0, 81, 9);

      //calculate bar color
      hex2Color(colors.advancedColors.armorToughnessColorValues[0]).color2Gl();
      //draw portion of bar based on armor toughness amount
      drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(armorToughness, 20), 7);

    } else {
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      int size = colors.advancedColors.armorToughnessColorValues.length;
      //if we are out of colors wrap the bar
      if (toughnessindex < size && armorToughness % 20 != 0) {

        //draw complete first bar
        hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex - 1]).color2Gl();
        drawTexturedModalRect(xStart, yStart + 1, 0, 10, 79, 7);

        //draw partial second bar
        f = xStart + 79 - getWidth(armorToughness % 20, 20);

        hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex]).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 0, 10, getWidth(armorToughness % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
    }

    //draw armor toughness amount
    int iq1 = (int) Math.floor(armorToughness);
    int iq2 = (general.displayIcons) ? 1 : 0;

    int toughnesscolor = Integer.decode(colors.advancedColors.armorToughnessColorValues[toughnessindex]);
    if (numbers.showPercent) iq1 = (int) armorToughness * 5;
    if (numbers.showArmorToughnessNumbers)
      drawStringOnHUD(iq1 + "", xStart + 9 * iq2 + rightTextOffset, yStart - 1, toughnesscolor);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);


    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "armortoughness";
  }
}

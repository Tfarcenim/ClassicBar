package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.config.ModConfig.general;

/*
    Class handles the drawing of the toughness bar
 */

public class ArmorToughnessBarRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();

  public ArmorToughnessBarRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void renderArmorToughnessBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = mc.getRenderViewEntity();
    if (!(renderViewEnity instanceof EntityPlayer) ||
            event.getType() != RenderGameOverlayEvent.ElementType.FOOD) return;
    EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
    double armorToughness = player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
    if (armorToughness < 1) return;
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes

    int xStart = scaledWidth / 2 + 10;
    int yStart = scaledHeight - 49;
    if (Loader.isModLoaded("toughasnails")) yStart -= 10;

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
    int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1,colors.advancedColors.armorToughnessColorValues.length - 1);

    if (armorToughness <= 20) {
      f = xStart + 79 - getWidth(armorToughness, 20);
      if (!general.overlays.fullToughnessBar) drawScaledBar(armorToughness, 20, f - 1, yStart,false);
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
      if (index < size && armorToughness % 20 != 0) {

        //draw complete first bar
        hex2Color(colors.advancedColors.armorToughnessColorValues[index - 1]).color2Gl();
        drawTexturedModalRect(xStart, yStart + 1, 0, 10, 79, 7);

        //draw partial second bar
        f = xStart + 79 - getWidth(armorToughness % 20, 20);

        hex2Color(colors.advancedColors.armorToughnessColorValues[index]).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 0, 10, getWidth(armorToughness % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(colors.advancedColors.armorToughnessColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
    }

    //draw armor toughness amount
    int i1 = (int) Math.floor(armorToughness);
    int i3 = (general.displayIcons) ? 1 : 0;

    int c = Integer.decode(colors.advancedColors.armorToughnessColorValues[index]);
    if (numbers.showPercent) i1 = (int) armorToughness * 5;
    drawStringOnHUD(i1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);
    GuiIngameForge.left_height += 10;


    // GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

}
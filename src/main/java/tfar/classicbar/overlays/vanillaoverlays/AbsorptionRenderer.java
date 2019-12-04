package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class AbsorptionRenderer implements IBarOverlay {

  public boolean side;

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return player.getAbsorptionAmount() > 0;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {

    double absorb = player.getAbsorptionAmount();

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    double maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();

    mc.profiler.startSection("health");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    int k5 = 16;

    if (player.isPotionActive(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;//evaluates to 88

    //draw absorption bar
    int index = (int) Math.ceil(absorb / maxHealth) - 1;
    // if (general.overlayorder.swap) yStart -= 10;
    Color.reset();
    //no wrapping
    if (absorb <= maxHealth) {
      if (!general.overlays.fullAbsorptionBar) drawScaledBar(absorb, maxHealth, xStart, yStart, true);
      else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

      switch (k5) {
        case 16: {
          hex2Color(colors.advancedColors.absorptionColorValues[0]).color2Gl();
          break;
        }
        case 52: {
          hex2Color(colors.advancedColors.absorptionPoisonColorValues[0]).color2Gl();
          break;
        }
        case 88: {
          hex2Color(colors.advancedColors.absorptionWitherColorValues[0]).color2Gl();
          break;
        }
      }

      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(absorb, maxHealth), 7);
    } else {
      //draw background bar
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      //don't crash from arrayindexoutofbounds
      if (index >= colors.advancedColors.absorptionColorValues.length - 1)
        index = colors.advancedColors.absorptionColorValues.length - 1;
      //draw first full bar
      switch (k5) {
        case 16: {
          hex2Color(colors.advancedColors.absorptionColorValues[index]).color2Gl();
          break;
        }
        case 52: {
          hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).color2Gl();
          break;
        }
        case 88: {
          hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).color2Gl();
          break;
        }
      }
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      //is it on the edge or capped already?
      if (absorb % maxHealth != 0 && index < colors.advancedColors.absorptionColorValues.length - 1) {
        //draw second partial bar
        switch (k5) {
          case 16: {
            hex2Color(colors.advancedColors.absorptionColorValues[index]).color2Gl();
            break;
          }
          case 52: {
            hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).color2Gl();
            break;
          }
          case 88: {
            hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).color2Gl();
            break;
          }
        }
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
      }
    }
    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showHealthNumbers;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {

    double absorb = player.getAbsorptionAmount();
    double maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    // handle the text
    int a1 = getStringLength((int) absorb + "");
    int a2 = general.displayIcons ? 1 : 0;
    int a3 = (int) absorb;
    int c = 0;

    int k5 = 16;

    if (player.isPotionActive(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;//evaluates to 88

    int index = Math.min((int) Math.ceil(absorb / maxHealth),colors.advancedColors.absorptionColorValues.length) - 1;

    switch (k5) {
      case 16: {
        c = hex2Color(colors.advancedColors.absorptionColorValues[index]).colorToText();
        break;
      }
      case 52: {
        c = hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).colorToText();
        break;
      }
      case 88: {
        c = hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).colorToText();
        break;
      }
    }

    drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 2, c);

  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {
    mc.getTextureManager().bindTexture(Gui.ICONS);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
    //draw absorption icon
    drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
    drawTexturedModalRect(xStart - 10, yStart, 160, 0, 9, 9);
  }

  @Override
  public String name() {
    return "absorption";
  }
}
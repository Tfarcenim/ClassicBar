package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class AbsorptionRenderer implements IBarOverlay {

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return  player.getAbsorptionAmount() > 0;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {

    double absorb = player.getAbsorptionAmount();

    IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    int xStart = width / 2 - 91;
    int yStart = height - GuiIngameForge.left_height;
    double maxHealth = maxHealthAttribute.getAttributeValue();

    mc.profiler.startSection("health");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    int k5 = 16;

    if (player.isPotionActive(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;//evaluates to 88

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
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
        drawTexturedModalRect(xStart + 1, yStart, 1, 10, 79, 7);
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
          drawTexturedModalRect(xStart + 1, yStart, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
        }
      }
      // handle the text
      int a1 = getStringLength((int) absorb + "");
      int a2 = general.displayIcons ? 1 : 0;
      int a3 = (int) absorb;
      int c = 0;

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

    if (numbers.showHealthNumbers)
      drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 2, c);

    int h1 = (int) Math.round(absorb);
    int i2 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) (100 * absorb / maxHealth);
    int i1 = getStringLength(h1 + "");


    //Reset back to normal settings
    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);


    if (general.displayIcons) {
      int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
        //draw absorption icon
        drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
        drawTexturedModalRect(xStart - 10, yStart, 160, 0, 9, 9);

    }
    //Reset back to normal settings

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "absorption";
  }
}
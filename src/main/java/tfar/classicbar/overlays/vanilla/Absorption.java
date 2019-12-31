package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.config.ModConfig.fullAbsorptionBar;

public class Absorption implements IBarOverlay {

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
  public boolean shouldRender(PlayerEntity player) {
    return player.getAbsorptionAmount() > 0;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {

    double absorb = player.getAbsorptionAmount();

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    double maxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int k5 = 16;

    if (player.isPotionActive(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(Effects.WITHER)) k5 += 72;//evaluates to 88

    //draw absorption bar
    int index = (int) Math.ceil(absorb / maxHealth) - 1;
    // if (general.overlayorder.swap) yStart -= 10;
    Color.reset();
    //no wrapping
    if (absorb <= maxHealth) {
      if (!fullAbsorptionBar.get()) drawScaledBar(absorb, maxHealth, xStart, yStart, true);
      else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

      switch (k5) {
        case 16: {
          hex2Color(absorptionColorValues.get().get(0)).color2Gl();
          break;
        }
        case 52: {
          hex2Color(absorptionPoisonColorValues.get().get(0)).color2Gl();
          break;
        }
        case 88: {
          hex2Color(absorptionWitherColorValues.get().get(0)).color2Gl();
          break;
        }
      }

      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(absorb, maxHealth), 7);
    } else {
      //draw background bar
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      //don't crash from arrayindexoutofbounds
      if (index >= absorptionColorValues.get().size() - 1)
        index = absorptionColorValues.get().size() - 1;
      //draw first full bar
      switch (k5) {
        case 16: {
          hex2Color(absorptionColorValues.get().get(index)).color2Gl();
          break;
        }
        case 52: {
          hex2Color(absorptionPoisonColorValues.get().get(index)).color2Gl();
          break;
        }
        case 88: {
          hex2Color(absorptionWitherColorValues.get().get(index)).color2Gl();
          break;
        }
      }
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      //is it on the edge or capped already?
      if (absorb % maxHealth != 0 && index < absorptionColorValues.get().size() - 1) {
        //draw second partial bar
        switch (k5) {
          case 16: {
            hex2Color(absorptionColorValues.get().get(index)).color2Gl();
            break;
          }
          case 52: {
            hex2Color(absorptionPoisonColorValues.get().get(index)).color2Gl();
            break;
          }
          case 88: {
            hex2Color(absorptionWitherColorValues.get().get(index)).color2Gl();
            break;
          }
        }
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
      }
    }
    RenderSystem.disableBlend();
    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showHealthNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {

    double absorb = player.getAbsorptionAmount();
    double maxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    // handle the text
    int a1 = getStringLength((int) absorb + "");
    int a2 = displayIcons.get() ? 1 : 0;
    int a3 = (int) absorb;
    int c = 0;

    int k5 = 16;

    if (player.isPotionActive(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(Effects.WITHER)) k5 += 72;//evaluates to 88

    int index = Math.min((int) Math.ceil(absorb / maxHealth),absorptionColorValues.get().size()) - 1;

    switch (k5) {
      case 16: {
        c = hex2Color(absorptionColorValues.get().get(index)).colorToText();
        break;
      }
      case 52: {
        c = hex2Color(absorptionPoisonColorValues.get().get(index)).colorToText();
        break;
      }
      case 88: {
        c = hex2Color(absorptionWitherColorValues.get().get(index)).colorToText();
        break;
      }
    }

    drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 2, c);

  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    int i5 = (player.world.getWorldInfo().isHardcore()) ? 5 : 0;
    //draw absorption icon
    drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
    drawTexturedModalRect(xStart - 10, yStart, 160, 0, 9, 9);
  }

  @Override
  public String name() {
    return "absorption";
  }
}
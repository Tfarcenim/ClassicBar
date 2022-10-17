package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.BarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.config.ModConfig.fullAbsorptionBar;

public class Absorption implements BarOverlay {

  public boolean side;

  @Override
  public BarOverlay setSide(boolean side) {
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
  public void renderBar(MatrixStack stack, PlayerEntity player, int screenWidth, int screenHeight) {

    double absorb = player.getAbsorptionAmount();

    int xStart = screenWidth / 2 - 91;
    int yStart = screenHeight - getSidedOffset();
    double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int k5 = 16;

    if (player.hasEffect(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.hasEffect(Effects.WITHER)) k5 += 72;//evaluates to 88

    //draw absorption bar
    int index = (int) Math.ceil(absorb / maxHealth) - 1;
    Color.reset();
    //no wrapping
    if (absorb <= maxHealth) {
      //background
      if (!fullAbsorptionBar.get()) drawScaledBar(stack,absorb, maxHealth, xStart, yStart + 1, true);
      else drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);

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

      drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, getWidth(absorb, maxHealth), 7);
    } else {
      //draw background bar
      drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
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
      drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, 79, 7);
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
        drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
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
  public void renderText(MatrixStack stack,PlayerEntity player, int width, int height) {

    double absorb = player.getAbsorptionAmount();
    double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    // handle the text
    int a1 = getStringLength((int) absorb + "");
    int a2 = displayIcons.get() ? 1 : 0;
    int a3 = (int) absorb;
    int c = 0;

    int k5 = 16;

    if (player.hasEffect(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.hasEffect(Effects.WITHER)) k5 += 72;//evaluates to 88

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

    drawStringOnHUD(stack,a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 2, c);

  }

  @Override
  public void renderIcon(MatrixStack stack,PlayerEntity player, int width, int height) {
    mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    int i5 = (player.level.getLevelData().isHardcore()) ? 5 : 0;
    //draw absorption icon
    drawTexturedModalRect(stack,xStart - 10, yStart, 16, 9 * i5, 9, 9);
    drawTexturedModalRect(stack,xStart - 10, yStart, 160, 0, 9, 9);
  }

  @Override
  public String name() {
    return "absorption";
  }
}
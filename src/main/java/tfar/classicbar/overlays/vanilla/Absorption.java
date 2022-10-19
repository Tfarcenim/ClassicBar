package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.util.Color;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;
import static tfar.classicbar.config.ClassicBarsConfig.*;

public class Absorption extends BarOverlayImpl {

  public Absorption() {
    super("absorption");
  }

  @Override
  public boolean shouldRender(Player player) {
    return player.getAbsorptionAmount() > 0;
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {

    double absorb = player.getAbsorptionAmount();

    int xStart = screenWidth / 2 - 91;
    int yStart = screenHeight - vOffset;
    double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();

    int k5 = 16;

    if (player.hasEffect(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.hasEffect(MobEffects.WITHER)) k5 += 72;//evaluates to 88

    //draw absorption bar
    int index = (int) Math.ceil(absorb / maxHealth) - 1;
    Color.reset();
    //no wrapping
    if (absorb <= maxHealth) {
      //background
      if (!ClassicBarsConfig.fullAbsorptionBar.get()) drawScaledBar(stack,absorb, maxHealth, xStart, yStart + 1, rightHandSide());
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
  }

  @Override
  public boolean shouldRenderText() {
    return showHealthNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {

    double absorb = player.getAbsorptionAmount();
    double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();
    int xStart = width / 2 + getHOffset();
    int yStart = height - vOffset;

    // handle the text
    int a1 = getStringLength((int) absorb + "");
    int a2 = ConfigCache.icons ? 1 : 0;
    int a3 = (int) absorb;
    int c = 0;

    int k5 = 16;

    if (player.hasEffect(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.hasEffect(MobEffects.WITHER)) k5 += 72;//evaluates to 88

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
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getHOffset();
    int yStart = height - vOffset;

    int i5 = (player.level.getLevelData().isHardcore()) ? 5 : 0;
    //draw absorption icon
    drawTexturedModalRect(stack,xStart - 10, yStart, 16, 9 * i5, 9, 9);
    drawTexturedModalRect(stack,xStart - 10, yStart, 160, 0, 9, 9);
  }
}
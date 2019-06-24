package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.Color;

import static tfar.classicbar.ColorUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class HealthBarRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();

  private int updateCounter = 0;
  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;
  private double displayHealth = 0;
  private int alpha;

  private boolean forceUpdateIcons = false;

  public HealthBarRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void renderHealthBar(RenderGameOverlayEvent.Pre event) {
    Entity renderViewEntity = mc.getRenderViewEntity();
    if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH
            || event.isCanceled()
            || !(renderViewEntity instanceof EntityPlayer)) return;
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes
    ;
    event.setCanceled(true);

    updateCounter = mc.ingameGUI.getUpdateCounter();

    EntityPlayer player = (EntityPlayer) renderViewEntity;
    double health = player.getHealth();
    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

    //player is damaged and resistant
    if (health < playerHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 20);
      lastPlayerHealth = playerHealth;
    } else if (health > playerHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 10);
      /* lastPlayerHealth = playerHealth;*/
    }
    double absorb = player.getAbsorptionAmount();
    if (health != playerHealth || forceUpdateIcons) {
      forceUpdateIcons = false;
    }
    playerHealth = health;
    displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);

    IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    int xStart = scaledWidth / 2 - 91;
    int yStart = scaledHeight - 39;
    double maxHealth = maxHealthAttribute.getAttributeValue();

    mc.profiler.startSection("health");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    int k5 = 16;

    if (player.isPotionActive(MobEffects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;//evaluates to 88

    int i4 = (highlight) ? 18 : 0;

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion
    alpha = health <= 0 ? 1 :health / maxHealth <= general.overlays.lowHealthThreshold && general.overlays.lowHealthWarning ?
            (int) (Minecraft.getSystemTime() / 250) % 2 : 1;
    //calculate bar color

    calculateScaledColor(health, maxHealth, k5).color2Gla(alpha);
    //draw portion of bar based on health remaining
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7);

    //interpolate the bar
    if (displayHealth != health) {
      //reset to white
      GlStateManager.color(1, 1, 1, alpha);
      if (displayHealth > health) {
        //draw interpolation
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(displayHealth, maxHealth), 7);
        //Health is increasing, idk what to do here
      } else {/*
                f = xStart + getWidth(health, maxHealth);
                drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
      }
    }

    if (k5 == 52) {
      //draw poison overlay
      GlStateManager.color(0, .5f, 0, .5f);
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 36, getWidth(health, maxHealth), 7);
    }
    //draw absorption bar if it exists
    if (absorb > 0) {
      int index = (int) Math.ceil(absorb / maxHealth) - 1;
      if (general.overlays.swap) yStart -= 10;
      Color.reset();
      //no wrapping
      if (absorb <= maxHealth) {
        if (!general.overlays.fullAbsorptionBar) drawScaledBar(absorb, maxHealth, xStart, yStart - 9, true);
        else drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);

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

        drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb, maxHealth), 7);
      } else {
        //draw background bar
        drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);
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
        drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, 79, 7);
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
          drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
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

      drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 11, c);
      if (general.overlays.swap) yStart += 10;
    }
    int h1 = (int) Math.round(health);
    int i2 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) (100 * health / maxHealth);
    int i1 = getStringLength(h1 + "");

    drawStringOnHUD(h1 + "", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, calculateScaledColor(health, maxHealth, k5).colorToText());

    //Reset back to normal settings
    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);
    GuiIngameForge.left_height += 10;
    if (absorb > 0) {
      GuiIngameForge.left_height += 10;
    }

    if (general.displayIcons) {
      int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
      //Draw health icon
      //heart background
      drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
      //heart
      drawTexturedModalRect(xStart - 10, yStart, 36 + k5, 9 * i5, 9, 9);
      if (absorb > 0) {
        if (general.overlays.swap) yStart -= 10;
        //draw absorption icon
        drawTexturedModalRect(xStart - 10, yStart - 10, 16, 9 * i5, 9, 9);
        drawTexturedModalRect(xStart - 10, yStart - 10, 160, 0, 9, 9);
      }
    }
    //Reset back to normal settings

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
    event.setCanceled(true);
  }
}

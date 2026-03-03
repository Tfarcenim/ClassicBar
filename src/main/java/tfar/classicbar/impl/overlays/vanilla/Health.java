package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Health extends BarOverlayImpl {

  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;

  public Health() {
    super("health");
  }

  @Override
  public boolean shouldRender(Player player) {
    return true;
  }

  @Override
  public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    int updateCounter = gui.getGuiTicks();

    double health = player.getHealth();
    double barWidth = getBarWidth(player);
    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

    //player is damaged and resistant
    if (health < playerHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 20;
      lastPlayerHealth = playerHealth;
    } else if (health > playerHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 10;
      /* lastPlayerHealth = playerHealth;*/
    }
    playerHealth = health;
    double displayHealth = health + (lastPlayerHealth - health) * ((double) player.invulnerableTime / player.invulnerableDuration);

    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double maxHealth = player.getMaxHealth();

    HealthEffect effect = getHealthEffect(player);

    int i4 = (highlight) ? 18 : 0;

    Color.reset();
    //Bar background
    ModUtils.drawTexturedModalRect(graphics,xStart, yStart, 0, i4, WIDTH + 4, 9);

    double f = xStart + (rightHandSide() ? WIDTH - barWidth : 0);

    //is the bar changing
    //Pass 1, draw bar portion
    //interpolate the bar
    if (displayHealth != health) {
      //reset to white
      if (displayHealth > health) {
        Color.reset();
        //draw interpolation
        double w = ModUtils.getWidth(displayHealth, maxHealth);
        double off = rightHandSide() ? w - barWidth : 0;
        //draw interpolation
        renderPartialBar(graphics,f + 2 - off, yStart + 2,w);
        //Health is increasing, IDK what to do here
      } else {/*
                  f = xStart + getWidth(health, maxHealth);
                  drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
      }
    }
    //calculate bar color
    Color primary = getPrimaryBarColor(0,player);
    primary.color2Gl();
    //draw portion of bar based on health remaining
    renderPartialBar(graphics,f + 2, yStart + 2, barWidth);
    if (effect == HealthEffect.POISON) {
      //draw poison overlay
      RenderSystem.setShaderColor(0, .5f, 0, .5f);
      ModUtils.drawTexturedModalRect(graphics,f + 1, yStart + 1, 1, 36, barWidth, 7);
    }
  }

  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    double health = player.getHealth();
    double maxHealth = player.getMaxHealth();
    HealthEffect effect = getHealthEffect(player);
    return ColorUtils.calculateScaledColor(health,maxHealth,effect);
  }

  @Override
  public double getBarWidth(Player player) {
    double health = player.getHealth();
    double maxHealth = player.getMaxHealth();
    return WIDTH * health / maxHealth;
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    double health = player.getHealth();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    textHelper(graphics,xStart,yStart,health,getPrimaryBarColor(0,player).colorToText());
  }

  private static final ResourceLocation HEART_CONTAINER = ResourceLocation.withDefaultNamespace("hud/heart/container");
  private static final ResourceLocation HEART_CONTAINER_HARDCORE = ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore");
  private static final ResourceLocation HEART_FULL = ResourceLocation.withDefaultNamespace("hud/heart/full");
  private static final ResourceLocation HEART_HARDCORE_FULL = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full");
  private static final ResourceLocation HEART_POISONED_FULL = ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full");
  private static final ResourceLocation HEART_WITHERED_FULL = ResourceLocation.withDefaultNamespace("hud/heart/withered_full");
  private static final ResourceLocation HEART_FROZEN_FULL = ResourceLocation.withDefaultNamespace("hud/heart/frozen_full");

  // Changed: renderIcon rewritten to use blitSprite with named sprite ResourceLocations
  // instead of drawTexturedModalRect with raw texture atlas offsets (previously effect.getX()).
  // The named sprite system (MC 1.20+) is the correct API and adds FROZEN heart support.
  // Also removed: shouldRenderText() override that returned ClassicBarsConfig.showHealthNumbers.get();
  // text visibility is now driven by barSettings.show_text (see BarOverlayImpl).
  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    HealthEffect effect = getHealthEffect(player);

    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    boolean hardcore = player.level().getLevelData().isHardcore();
    //Draw health icon
    //heart background
    graphics.blitSprite(hardcore ? HEART_CONTAINER_HARDCORE : HEART_CONTAINER, xStart, yStart, 9, 9);
    //heart
    ResourceLocation heartSprite = switch (effect) {
      case NONE -> hardcore ? HEART_HARDCORE_FULL : HEART_FULL;
      case POISON -> HEART_POISONED_FULL;
      case WITHER -> HEART_WITHERED_FULL;
      case FROZEN -> HEART_FROZEN_FULL;
    };
    graphics.blitSprite(heartSprite, xStart, yStart, 9, 9);
  }
}
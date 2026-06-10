package tfar.classicbar.impl.overlays.vanilla;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.colorprovider.BarLayer;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.api.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Health extends BarOverlayImpl {

  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;

  public static final BarInfo INFO = new BarInfo("health",
          player -> true,LivingEntity::getHealth, LivingEntity::getMaxHealth);

  public Health(BarSettings settings) {
    super(INFO,settings);
  }

  public static final Codec<Health> CODEC = RecordCodecBuilder.create(
          objectInstance -> codecStart(objectInstance).apply(objectInstance,Health::new)
  );

  @Override
  public Codec<? extends BarOverlayImpl> codec() {
    return CODEC;
  }

  @Override
  public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
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


      //Bar background
   // renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset,highlight);

    double f = xStart + (getSide() == BarSide.RIGHT ? WIDTH - barWidth : 0);

    //is the bar changing
    //Pass 1, draw bar portion
    //interpolate the bar
    if (displayHealth != health) {
      //reset to white
      if (displayHealth > health) {
        //draw interpolation
        double w = ModUtils.getWidth(displayHealth, maxHealth);
        double off = getSide() == BarSide.RIGHT ? w - barWidth : 0;
        //draw interpolation
        renderPartialBar(Color.WHITE,graphics,f + 2 - off, yStart + 2,w);
        //Health is increasing, IDK what to do here
      } else {/*
                  f = xStart + getWidth(health, maxHealth);
                  drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
      }
    }
    //draw portion of bar based on health remaining
   // Color primary = getBarSettings().colorProvider().getColor(player, ,0);

    renderSimpleBar(getBarSettings().colorProvider().getColor(player, BarLayer.PRIMARY), graphics, player, screenWidth, screenHeight, vOffset);

    HealthEffect effect = getHealthEffect(player);

    //renderPartialBar(primary,graphics,f + 2, yStart + 2, barWidth);
    if (effect == HealthEffect.POISON) {
      //draw poison overlay
      RenderSystem.setShaderColor(0, .5f, 0, .5f);
      ModUtils.drawTexturedModalRect(getIconRL(),graphics,f + 1, yStart + 1, 1, 36, barWidth, 7);
    }
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    HealthEffect effect = getHealthEffect(player);

    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    int i5 = (player.level().getLevelData().isHardcore()) ? 5 : 0;
    //Draw health icon
    //heart background
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 16, 9 * i5, 9, 9);
    //heart
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 36 + effect.i, 9 * i5, 9, 9);
  }
}
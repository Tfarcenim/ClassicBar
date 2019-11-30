package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.client.GuiIngameForge;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

public class HealthRenderer implements IBarOverlay {

  private final Minecraft mc = Minecraft.getMinecraft();

  private float foodAlpha = 0;
  private boolean foodIncrease = true;

  private int updateCounter = 0;
  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;
  private double displayHealth = 0;
  private int alpha;

  long lastSystemTime;

  private double mountHealth = 0;

  private float armorAlpha = 1;
  private static EntityEquipmentSlot[] armorList = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD,
          EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return true;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {
    updateCounter = mc.ingameGUI.getUpdateCounter();

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
    playerHealth = health;
    displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);

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

    int i4 = (highlight) ? 18 : 0;

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion
    alpha = health <= 0 ? 1 : health / maxHealth <= general.overlays.lowHealthThreshold && general.overlays.lowHealthWarning ?
            (int) (Minecraft.getSystemTime() / 250) % 2 : 1;

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

    //calculate bar color

    calculateScaledColor(health, maxHealth, k5).color2Gla(alpha);
    //draw portion of bar based on health remaining
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7);


    if (k5 == 52) {
      //draw poison overlay
      GlStateManager.color(0, .5f, 0, .5f);
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 36, getWidth(health, maxHealth), 7);
    }
    int h1 = (int) Math.round(health);
    int i2 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) (100 * health / maxHealth);
    int i1 = getStringLength(h1 + "");

    if (numbers.showHealthNumbers)
      drawStringOnHUD(h1 + "", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, calculateScaledColor(health, maxHealth, k5).colorToText());

    //Reset back to normal settings
    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    if (general.displayIcons) {
      int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
      //Draw health icon
      //heart background
      drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
      //heart
      drawTexturedModalRect(xStart - 10, yStart, 36 + k5, 9 * i5, 9, 9);
    }
    //Reset back to normal settings

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "health";
  }
}
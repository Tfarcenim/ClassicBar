package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.drawTexturedModalRect;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

public class MountHealthRenderer implements IBarOverlay {

  private int updateCounter = 0;
  private long healthUpdateCounter = 0;

  long lastSystemTime;

  private double mountHealth = 0;

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return player.getRidingEntity() instanceof EntityLivingBase;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {
    //Push to avoid lasting changes
    updateCounter = mc.ingameGUI.getUpdateCounter();

    EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
    if (mount.isDead) return;
    double mountHealth = mount.getHealth();

    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (mountHealth < this.mountHealth && player.hurtResistantTime > 0) {
      lastSystemTime = Minecraft.getSystemTime();
      healthUpdateCounter = (long) (updateCounter + 20);
    } else if (mountHealth > this.mountHealth && player.hurtResistantTime > 0) {
      lastSystemTime = Minecraft.getSystemTime();
      healthUpdateCounter = (long) (updateCounter + 10);
    }

    this.mountHealth = mountHealth;
    IAttributeInstance maxHealthAttribute = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    int xStart = width / 2 + 10;
    int yStart = height - 39;
    double maxHealth = maxHealthAttribute.getAttributeValue();

    mc.profiler.startSection("mountHealth");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    int i4 = (highlight) ? 18 : 0;


    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion

    //calculate bar color
    calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
    float f = xStart + 79 - getWidth(mountHealth, maxHealth);
    //draw portion of bar based on mountHealth remaining
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);
    //draw mountHealth amount
    int h1 = (int) Math.ceil(mountHealth);

    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) (100 * mountHealth / maxHealth);
    if (numbers.showMountHealthNumbers)
      drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());

    //Reset back to normal settings
    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    if (general.displayIcons) {
      //Draw mountHealth icon
      //heart background
      drawTexturedModalRect(xStart + 82, yStart, 16, 0, 9, 9);
      //heart
      drawTexturedModalRect(xStart + 82, yStart, 88, 9, 9, 9);

    }

    //Reset back to normal settings

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "healthmount";
  }
}

package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

public class MountHealthRenderer implements IBarOverlay {

  private long healthUpdateCounter = 0;

  private double mountHealth = 0;

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
    return player.getRidingEntity() instanceof EntityLivingBase;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {
    //Push to avoid lasting changes
    int updateCounter = mc.ingameGUI.getUpdateCounter();

    EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
    if (mount.isDead) return;
    double mountHealth = mount.getHealth();

    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (mountHealth < this.mountHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 20);
    } else if (mountHealth > this.mountHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 10);
    }

    this.mountHealth = mountHealth;
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    double maxHealth = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();

    mc.profiler.startSection("mountHealth");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    int i4 = (highlight) ? 18 : 0;

    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion

    //calculate bar color
    calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
    float f = xStart + 79 - getWidth(mountHealth, maxHealth);
    //draw portion of bar based on mountHealth remaining
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showMountHealthNumbers;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {
    int h1 = (int) Math.ceil(mountHealth);

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
    double maxHealth = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = (int) (100 * mountHealth / maxHealth);
    drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());
  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //heart background
    drawTexturedModalRect(xStart + 82, yStart, 16, 0, 9, 9);
    //heart
    drawTexturedModalRect(xStart + 82, yStart, 88, 9, 9, 9);
  }

  @Override
  public String name() {
    return "healthmount";
  }
}

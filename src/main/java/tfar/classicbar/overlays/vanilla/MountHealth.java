package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.showMountHealthNumbers;

public class MountHealth implements IBarOverlay {

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
  public boolean shouldRender(PlayerEntity player) {
    return player.getRidingEntity() instanceof LivingEntity;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {
    //Push to avoid lasting changes
    int updateCounter = mc.ingameGUI.getTicks();

    LivingEntity mount = (LivingEntity) player.getRidingEntity();
    if (!mount.isAlive()) return;
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
    double maxHealth = mount.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int i4 = (highlight) ? 18 : 0;

    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion

    //calculate bar color
    calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
    int f = xStart + 79 - getWidth(mountHealth, maxHealth);
    //draw portion of bar based on mountHealth remaining
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);

    RenderSystem.disableBlend();
    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showMountHealthNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {
    int h1 = (int) Math.ceil(mountHealth);

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    LivingEntity mount = (LivingEntity) player.getRidingEntity();
    double maxHealth = mount.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
    int i3 = ModConfig.displayIcons.get() ? 1 : 0;
    if (ModConfig.showPercent.get()) h1 = (int) (100 * mountHealth / maxHealth);
    drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());
  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {
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
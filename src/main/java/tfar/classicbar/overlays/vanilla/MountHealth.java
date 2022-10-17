package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.showMountHealthNumbers;

public class MountHealth implements BarOverlay {

  private long healthUpdateCounter = 0;

  private double mountHealth = 0;

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
  public boolean shouldRender(Player player) {
    return player.getVehicle() instanceof LivingEntity;
  }

  @Override
  public void renderBar(PoseStack stack, Player player, int screenWidth, int screenHeight) {
    //Push to avoid lasting changes
    int updateCounter = mc.gui.getGuiTicks();

    LivingEntity mount = (LivingEntity) player.getVehicle();
    if (!mount.isAlive()) return;
    double mountHealth = mount.getHealth();

    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (mountHealth < this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 20);
    } else if (mountHealth > this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 10);
    }

    this.mountHealth = mountHealth;
    int xStart = screenWidth / 2 + 10;
    int yStart = screenHeight - getSidedOffset();
    double maxHealth = mount.getAttribute(Attributes.MAX_HEALTH).getValue();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int i4 = (highlight) ? 18 : 0;

    //Bar background
    drawTexturedModalRect(stack,xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion

    //calculate bar color
    calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
    int f = xStart + 79 - getWidth(mountHealth, maxHealth);
    //draw portion of bar based on mountHealth remaining
    drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);

    RenderSystem.disableBlend();
    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showMountHealthNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height) {
    int h1 = (int) Math.ceil(mountHealth);

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    LivingEntity mount = (LivingEntity) player.getVehicle();
    double maxHealth = mount.getAttribute(Attributes.MAX_HEALTH).getValue();
    int i3 = ModConfig.displayIcons.get() ? 1 : 0;
    if (ModConfig.showPercent.get()) h1 = (int) (100 * mountHealth / maxHealth);
    drawStringOnHUD(stack,h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());
  }

  @Override
  public void renderIcon(PoseStack stack,Player player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //heart background
    drawTexturedModalRect(stack,xStart + 82, yStart, 16, 0, 9, 9);
    //heart
    drawTexturedModalRect(stack,xStart + 82, yStart, 88, 9, 9, 9);
  }

  @Override
  public String name() {
    return "healthmount";
  }
}
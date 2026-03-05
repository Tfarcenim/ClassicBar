package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

// Changed: removed shouldRenderText() override that returned ClassicBarsConfig.showMountHealthNumbers.get().
// Also removed the ClassicBarsConfig import. Text visibility is now driven by barSettings.show_text.
public class MountHealth extends BarOverlayImpl {

  private long healthUpdateCounter = 0;

  private double mountHealth = 0;

  public MountHealth() {
    super("health_mount");
  }

  @Override
  public boolean shouldRender(Player player) {
    return player.getVehicle() instanceof LivingEntity;
  }

  @Override
  public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    //Push to avoid lasting changes
    long updateCounter = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;

    LivingEntity mount = (LivingEntity) player.getVehicle();
    if (!mount.isAlive()) return;
    double mountHealth = mount.getHealth();
    double barWidth = getBarWidth(player);

    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (mountHealth < this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 20;
    } else if (mountHealth > this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 10;
    }

    this.mountHealth = mountHealth;
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double maxHealth = mount.getMaxHealth();
    int i4 = (highlight) ? 18 : 0;
    //Bar background
    ModUtils.drawTexturedModalRect(graphics,xStart, yStart, 0, i4, WIDTH + 4, 9); // Aligned: was 81; uses WIDTH + 4 like Health and BarOverlayImpl.renderFullBarBackground()
    //is the bar changing
    //Pass 1, draw bar portion
    //calculate bar color
    ColorUtils.calculateScaledColor(mountHealth, maxHealth, HealthEffect.NONE).color2Gl();
    double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
    //draw portion of bar based on mountHealth remaining
    renderPartialBar(graphics,f + 2, yStart + 2, barWidth);
  }
  @Override
  public double getBarWidth(Player player) {
    LivingEntity mount = (LivingEntity) player.getVehicle();
    double mountHealth = mount.getHealth();
    double maxHealth = mount.getMaxHealth();
    return Math.ceil(BarOverlayImpl.WIDTH * mountHealth / maxHealth); // Aligned: removed unnecessary (int) cast; return type is double, matches all other overlays
  }

  @Override
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    LivingEntity mount = (LivingEntity) player.getVehicle();
    double maxHealth = mount.getMaxHealth();
    textHelper(graphics,xStart,yStart,mountHealth, ColorUtils.calculateScaledColor(mountHealth, maxHealth, HealthEffect.NONE).colorToText());
  }

  private static final ResourceLocation HEART_VEHICLE_CONTAINER = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container");
  private static final ResourceLocation HEART_VEHICLE_FULL = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full");

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //heart background
    graphics.blitSprite(HEART_VEHICLE_CONTAINER, xStart, yStart, 9, 9);
    //heart
    graphics.blitSprite(HEART_VEHICLE_FULL, xStart, yStart, 9, 9);
  }
}
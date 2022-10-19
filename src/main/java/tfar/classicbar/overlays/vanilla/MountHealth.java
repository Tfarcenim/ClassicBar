package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.calculateScaledColor;
import static tfar.classicbar.util.ModUtils.*;
import static tfar.classicbar.config.ClassicBarsConfig.showMountHealthNumbers;

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
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
    //Push to avoid lasting changes
    int updateCounter = gui.getGuiTicks();

    LivingEntity mount = (LivingEntity) player.getVehicle();
    if (!mount.isAlive()) return;
    double mountHealth = mount.getHealth();

    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (mountHealth < this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 20;
    } else if (mountHealth > this.mountHealth && player.invulnerableTime > 0) {
      healthUpdateCounter = updateCounter + 10;
    }

    this.mountHealth = mountHealth;
    int xStart = screenWidth / 2 + 10;
    int yStart = screenHeight - vOffset;
    double maxHealth = mount.getAttribute(Attributes.MAX_HEALTH).getValue();

    int i4 = (highlight) ? 18 : 0;

    //Bar background
    drawTexturedModalRect(stack,xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion

    //calculate bar color
    calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
    double f = xStart + 79 - getWidth(mountHealth, maxHealth);
    //draw portion of bar based on mountHealth remaining
    drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);
  }

  @Override
  public boolean shouldRenderText() {
    return showMountHealthNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
    int h1 = (int) Math.ceil(mountHealth);

    int xStart = width / 2 + getHOffset();
    int yStart = height - vOffset;
    LivingEntity mount = (LivingEntity) player.getVehicle();
    double maxHealth = mount.getAttribute(Attributes.MAX_HEALTH).getValue();
    int i3 = ConfigCache.icons ? 1 : 0;
    drawStringOnHUD(stack,h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + 10;
    int yStart = height - vOffset;
    //heart background
    drawTexturedModalRect(stack,xStart + 82, yStart, 16, 0, 9, 9);
    //heart
    drawTexturedModalRect(stack,xStart + 82, yStart, 88, 9, 9, 9);
  }
}
package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class Air implements IBarOverlay {

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
    return player.getAir() < 300;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {
    //Push to avoid lasting changes

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    Color.reset();

    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //draw portion of bar based on air amount
    int air = player.getAir();

    int f = xStart + 79 - getWidth(air, 300);
    hex2Color(oxygenBarColor.get()).color2Gl();
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air, 300), 7);

    RenderSystem.disableBlend();
    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showAirNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {
    //draw air amount
    int air = player.getAir();
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    int h1 = (int) Math.floor(air / 20);

    int c = Integer.decode(oxygenBarColor.get());
    int i3 = displayIcons.get() ? 1 : 0;
    if (showPercent.get()) h1 = air / 3;
    drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    //Draw air icon
    drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9);
  }

  @Override
  public String name() {
    return "air";
  }
}
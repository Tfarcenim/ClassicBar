package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class AirRenderer implements IBarOverlay {

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
    return player.getAir() < 300;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {
    //Push to avoid lasting changes

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    mc.profiler.startSection("air");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //draw portion of bar based on air amount
    int air = player.getAir();

    float f = xStart + 79 - getWidth(air, 300);
    hex2Color(colors.oxygenBarColor).color2Gl();
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air, 300), 7);


    Color.reset();


    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showOxygenNumbers;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {
    //draw air amount
    int air = player.getAir();
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();

    int h1 = (int) Math.floor(air / 20);

    int c = Integer.decode(colors.oxygenBarColor);
    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = air / 3;
      drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {

    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    mc.getTextureManager().bindTexture(Gui.ICONS);
    //Draw air icon
    drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9);
  }

  @Override
  public String name() {
    return "air";
  }
}

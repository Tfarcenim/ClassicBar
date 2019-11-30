package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class AirRenderer implements IBarOverlay {
  @Override
  public boolean shouldRender(EntityPlayer player) {
    return player.getAir() < 300;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {
    int air = player.getAir();
    if (air >= 300) return;
    //Push to avoid lasting changes

    int xStart = width / 2 + 10;
    int yStart = height - GuiIngameForge.right_height;

    mc.profiler.startSection("air");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //draw portion of bar based on air amount

    float f = xStart + 79 - getWidth(air, 300);
    hex2Color(colors.oxygenBarColor).color2Gl();
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air, 300), 7);

    //draw air amount
    int h1 = (int) Math.floor(air / 20);

    int c = Integer.decode(colors.oxygenBarColor);
    int i3 = general.displayIcons ? 1 : 0;
    if (numbers.showPercent) h1 = air / 3;
    if (numbers.showOxygenNumbers)
      drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
    //Reset back to normal settings
    Color.reset();
    mc.getTextureManager().bindTexture(ICON_VANILLA);
    if (general.displayIcons) {
      //Draw air icon
      drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9);
    }
    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "air";
  }
}

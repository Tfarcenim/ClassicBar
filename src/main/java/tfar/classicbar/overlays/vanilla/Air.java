package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;
import static tfar.classicbar.config.ClassicBarsConfig.*;

public class Air extends BarOverlayImpl {

  public Air() {
    super("air");
  }

  @Override
  public boolean shouldRender(Player player) {
    return player.getAirSupply() < 300;
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {

    int xStart = screenWidth / 2 + 10;
    int yStart = screenHeight - vOffset;

    Color.reset();

    //Bar background
    drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);

    //draw portion of bar based on air amount
    int air = player.getAirSupply();

    double f = xStart + 79 - getWidth(air, 300);
    hex2Color(oxygenBarColor.get()).color2Gl();
    drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(air, 300), 7);
  }

  @Override
  public boolean shouldRenderText() {
    return showAirNumbers.get();
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
    //draw air amount
    int air = player.getAirSupply();
    int xStart = width / 2 + getHOffset();
    int yStart = height - vOffset;

    int h1 = (int) Math.floor(air / 20);

    int c = Integer.decode(oxygenBarColor.get());
    int i3 = ConfigCache.icons ? 1 : 0;
    drawStringOnHUD(stack,h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + 10;
    int yStart = height - vOffset;
    //Draw air icon
    drawTexturedModalRect(stack,xStart + 82, yStart, 16, 18, 9, 9);
  }
}
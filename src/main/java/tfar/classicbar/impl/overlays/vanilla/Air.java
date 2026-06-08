package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

// Changed: removed shouldRenderText() override that returned ClassicBarsConfig.showAirNumbers.get().
// Also removed the ClassicBarsConfig import. Text visibility is now driven by barSettings.show_text.
public class Air extends BarOverlayImpl {

  public Air() {
    super("air");
  }

  @Override
  public boolean shouldRender(Player player) {
    return player.getAirSupply() < player.getMaxAirSupply();
  }
  @Override
  public void renderBar(Gui gui, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) { //NOSONAR Gui kept for BarOverlayImpl contract
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double barWidth = getBarWidth(player);
    Color.reset();
    //Bar background
    renderFullBarBackground(graphics,xStart, yStart);
    //draw portion of bar based on air amount
    double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
    Color color = getPrimaryBarColor(0,player);
    color.color2Gl();
    renderPartialBar(graphics,f + 2, yStart + 2,barWidth);
  }

  @Override
  public double getBarWidth(Player player) {
    int air = player.getAirSupply();
    int maxAir = player.getMaxAirSupply();
    return Math.ceil((double) BarOverlayImpl.WIDTH * air/ maxAir);
  }
  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    return ConfigCache.air;
  }
  @Override
  public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
    //draw air amount
    int air = player.getAirSupply();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    Color color = getPrimaryBarColor(0,player);
    textHelper(graphics,xStart,yStart,air/20,color.colorToText());
  }
  private static final Identifier AIR_SPRITE = Identifier.withDefaultNamespace("hud/air");

  @Override
  public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //Draw air icon
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AIR_SPRITE, xStart, yStart, 9, 9);
  }
}
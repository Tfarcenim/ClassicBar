package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Air extends BarOverlayImpl {

  public Air(BarSettings settings) {
    super("air",settings);
  }

  public static final Codec<Air> CODEC = RecordCodecBuilder.create(
          objectInstance -> objectInstance.group(BarSettings.CODEC.fieldOf("bar_settings")
                  .forGetter(Air::getBarSettings)
          ).apply(objectInstance,Air::new)
  );

  @Override
  public boolean shouldRender(Player player) {
      return super.shouldRender(player) && player.getAirSupply() < player.getMaxAirSupply();
  }
  @Override
  public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double barWidth = getBarWidth(player);
    Color.reset();
    //Bar background
    renderFullBarBackground(graphics,xStart, yStart);
    //draw portion of bar based on air amount
    double f = xStart + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
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
  public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    //draw air amount
    int air = player.getAirSupply();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    Color color = getPrimaryBarColor(0,player);
    textHelper(graphics,xStart,yStart,air/20,color.colorToText());
  }

  @Override
  public Codec<? extends BarOverlayImpl> getCodec() {
    return CODEC;
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //Draw air icon
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 16, 18, 9, 9);
  }
}
package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Air extends BarOverlayImpl {

  public Air() {
    super("air");
  }

  @Override
  public boolean shouldRender(Player player) {
    return player.getAirSupply() < player.getMaxAirSupply();
  }
  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
    int xStart = screenWidth / 2 + getHOffset();
    int yStart = screenHeight - vOffset;
    double barWidth = getBarWidth(player);
    Color.reset();
    //Bar background
    renderFullBarBackground(stack,xStart, yStart);
    //draw portion of bar based on air amount
    double f = xStart + (rightHandSide() ? ModUtils.WIDTH - barWidth : 0);
    Color color = getPrimaryBarColor(0,player);
    color.color2Gl();
    ModUtils.drawTexturedModalRect(stack,f + 2, yStart + 1, 2, 10,barWidth, 7);
  }
  @Override
  public boolean shouldRenderText() {
    return ClassicBarsConfig.showAirNumbers.get();
  }

  @Override
  public double getBarWidth(Player player) {
    int air = player.getAirSupply();
    int maxAir = player.getMaxAirSupply();
    return (int) Math.ceil(ModUtils.WIDTH * Math.min((double) air,maxAir) / maxAir);
  }
  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    return ConfigCache.air;
  }
  @Override
  public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
    //draw air amount
    int air = player.getAirSupply();
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    Color color = getPrimaryBarColor(0,player);
    textHelper(stack,xStart,yStart,air/20,color.colorToText());
  }
  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //Draw air icon
    ModUtils.drawTexturedModalRect(stack,xStart, yStart, 16, 18, 9, 9);
  }
}
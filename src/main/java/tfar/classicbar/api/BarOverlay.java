package tfar.classicbar.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.util.Color;

import java.util.Set;

public interface BarOverlay {

  BarSide getSide();

  void render(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

  double getBarWidth(Player player);

  Color getPrimaryBarColor(int index,Player player);

  Color getSecondaryBarColor(int index,Player player);

  boolean isFitted();

  boolean errored();

  void setErrored();

  String name();
  Set<String> dependencies();
}
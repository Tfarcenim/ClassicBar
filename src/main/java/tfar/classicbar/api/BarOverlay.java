package tfar.classicbar.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.util.Color;

import java.util.Set;

public interface BarOverlay {

  BarSide getSide();

  boolean render(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

  void setErrored();

  String name();
}
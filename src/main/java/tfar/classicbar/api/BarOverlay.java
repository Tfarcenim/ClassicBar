package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public interface BarOverlay {

  BarSide getSide();

  boolean render(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

  boolean dependenciesMet();
  void setErrored();

  String name();
  Codec<? extends BarOverlay> codec();
}
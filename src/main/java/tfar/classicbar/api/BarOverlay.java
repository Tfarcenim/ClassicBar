package tfar.classicbar.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public interface BarOverlay {

  // Changed: added to support the per-bar JSON settings system. Called by ClassicBarsConfig
  // after reading each overlay's JSON file to inject its BarSettings (show_text, icon).
  // Implemented in BarOverlayImpl; previously each overlay hardcoded these via overrides.
  void setBarSettings(BarSettings barSettings);
  boolean rightHandSide();
  BarOverlay setSide(boolean right);

  void render(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

  ResourceLocation getIconRL();
  default void bindIconTexture() {
    ModUtils.CURRENT_TEXTURE = getIconRL();
  }

  default void bindBarTexture() {
    ModUtils.CURRENT_TEXTURE = BarOverlayImpl.ICON_BAR;
  }

  double getBarWidth(Player player);

  Color getPrimaryBarColor(int index,Player player);

  Color getSecondaryBarColor(int index,Player player);

  boolean isFitted();

  String name();
}
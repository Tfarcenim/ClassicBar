package tfar.classicbar.overlays;

import net.minecraft.entity.player.EntityPlayer;

public interface IBarOverlay {

  boolean shouldRender(EntityPlayer player);
  void render(EntityPlayer player, int width, int height);
  String name();
}

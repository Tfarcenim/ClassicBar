package tfar.classicbar.overlays;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.gui.ForgeIngameGui;

public interface IBarOverlay {

  boolean shouldRender(PlayerEntity player);
  boolean rightHandSide();
  IBarOverlay setSide(boolean right);
  void renderBar(PlayerEntity player, int width, int height);
  boolean shouldRenderText();
  void renderText(PlayerEntity player, int width, int height);
  void renderIcon(PlayerEntity player, int width, int height);
  default int getSidedOffset(){
    return rightHandSide() ? ForgeIngameGui.right_height : ForgeIngameGui.left_height;
  }
  String name();
}
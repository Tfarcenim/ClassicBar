package tfar.classicbar.overlays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;

public interface IBarOverlay {

  boolean shouldRender(EntityPlayer player);
  boolean rightHandSide();
  IBarOverlay setSide(boolean right);
  void renderBar(EntityPlayer player, int width, int height);
  boolean shouldRenderText();
  void renderText(EntityPlayer player, int width, int height);
  void renderIcon(EntityPlayer player, int width, int height);
  default int getSidedOffset(){
    return rightHandSide() ? GuiIngameForge.right_height : GuiIngameForge.left_height;
  }
  String name();
}

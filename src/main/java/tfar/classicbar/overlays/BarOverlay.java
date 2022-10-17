package tfar.classicbar.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;

public interface BarOverlay {

  boolean shouldRender(Player player);
  boolean rightHandSide();
  BarOverlay setSide(boolean right);
  void renderBar(PoseStack stack,Player player, int screenWidth, int screenHeight);
  boolean shouldRenderText();
  void renderText(PoseStack stack,Player player, int width, int height);
  void renderIcon(PoseStack stack,Player player, int width, int height);
  default int getSidedOffset(){
    return rightHandSide() ? ForgeIngameGui.right_height : ForgeIngameGui.left_height;
  }
  String name();
}
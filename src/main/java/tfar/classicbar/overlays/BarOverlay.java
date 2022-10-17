package tfar.classicbar.overlays;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.gui.ForgeIngameGui;

public interface BarOverlay {

  boolean shouldRender(PlayerEntity player);
  boolean rightHandSide();
  BarOverlay setSide(boolean right);
  void renderBar(MatrixStack stack,PlayerEntity player, int screenWidth, int screenHeight);
  boolean shouldRenderText();
  void renderText(MatrixStack stack,PlayerEntity player, int width, int height);
  void renderIcon(MatrixStack stack,PlayerEntity player, int width, int height);
  default int getSidedOffset(){
    return rightHandSide() ? ForgeIngameGui.right_height : ForgeIngameGui.left_height;
  }
  String name();
}
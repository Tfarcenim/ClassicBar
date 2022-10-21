package tfar.classicbar.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.util.Color;

public interface BarOverlay {

  //maximum width the bar can be
  int WIDTH = 77;
  int HEIGHT = 5;
  int BAR_U = 2;
  int BAR_V = 11;
  ResourceLocation ICON_BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");

  boolean rightHandSide();
  BarOverlay setSide(boolean right);

  void render(ForgeGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset);

  ResourceLocation getIconRL();
  default void bindIconTexture() {
    RenderSystem.setShaderTexture(0,getIconRL());
  }

  double getBarWidth(Player player);

  Color getPrimaryBarColor(int index,Player player);

  Color getSecondaryBarColor(int index,Player player);

  boolean isFitted();

  String name();
}
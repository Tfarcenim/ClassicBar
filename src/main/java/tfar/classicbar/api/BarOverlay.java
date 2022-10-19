package tfar.classicbar.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;

public interface BarOverlay {

  boolean rightHandSide();
  BarOverlay setSide(boolean right);

  void render(ForgeIngameGui gui,PoseStack stack,Player player, int screenWidth, int screenHeight,int vOffset);

  ResourceLocation getIconRL();
  default void bindIconTexture() {
    RenderSystem.setShaderTexture(0,getIconRL());
  }

  int getBarWidth(Player player);

  int getPrimaryBarColor(int index);

  int getSecondaryBarColor(int index);

  boolean isFitted();

  String name();
}
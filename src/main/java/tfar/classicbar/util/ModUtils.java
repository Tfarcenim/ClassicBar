package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ModUtils {

  public static void drawTexturedModalRect(ResourceLocation texture,GuiGraphics stack, double x, int y, int textureX, int textureY, double width, int height) {
    stack.blit(texture, (int) x, y, textureX, textureY, (int) width, height);
  }

  public static void drawStringOnHUD(GuiGraphics stack, String string, int xOffset, int yOffset, int color) {

    xOffset += 2;
    yOffset += 2;

    stack.drawString(Minecraft.getInstance().font,string, xOffset, yOffset, color,true);
  }
}

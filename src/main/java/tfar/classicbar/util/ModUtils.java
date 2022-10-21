package tfar.classicbar.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import tfar.classicbar.api.BarOverlay;

public class ModUtils {
  public static final Minecraft mc = Minecraft.getInstance();
  private static final Font fontRenderer = mc.font;

  public static void drawTexturedModalRect(PoseStack stack, double x, int y, int textureX, int textureY, double width, int height) {
    mc.gui.blit(stack, (int) x, y, textureX, textureY, (int) width, height);
  }

  public static double getWidth(double d1, double d2) {
    double ratio = BarOverlay.WIDTH * d1 / d2;
    return Math.ceil(ratio);
  }

  public static int getStringLength(String s) {
    return fontRenderer.width(s);
  }

  public static void drawStringOnHUD(PoseStack stack,String string, int xOffset, int yOffset, int color) {
   /* double scale = numbers.numberScale;
    GlStateManager.pushMatrix();
    GlStateManager.scale(scale, scale, 1);
    xOffset /= scale;
    yOffset /= scale;
    int l = fontRenderer.getStringWidth(string);
    xOffset += (left) ? .4*l * (1 - scale) / scale : 0;
    GlStateManager.translate(16 * (1 - scale) / scale, 14 * (1 - scale) / scale, 0);*/

    xOffset += 2;
    yOffset += 2;

    fontRenderer.drawShadow(stack,string, xOffset, yOffset, color);
  }
}

package tfar.classicbar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class ModUtils {
  public static final int rightTextOffset = 82;

  public static final int leftTextOffset = -5;

  public static ResourceLocation ICON_BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");
  public static final Minecraft mc = Minecraft.getInstance();
  private static final Font fontRenderer = mc.font;

  public static void drawTexturedModalRect(PoseStack stack,int x, int y, int textureX, int textureY, int width, int height) {
    mc.gui.blit(stack,x, y, textureX, textureY, width, height);
  }

  public static int getWidth(double d1, double d2) {
    int w = 78;
    double d3 = Math.max(w * d1 / d2, 0);
    return (int) Math.ceil(d3);
  }

  public static int getStringLength(String s) {
    return fontRenderer.width(s);
  }

  public static void drawScaledBar(PoseStack stack,double absorb, double maxHealth, int x, int y, boolean left) {
    int i = getWidth(absorb, maxHealth);

    if (left) {
      drawTexturedModalRect(stack,x, y - 1, 0, 0, i + 1, 9);
      drawTexturedModalRect(stack,x + i + 1, y - 1, 79, 0, 2, 9);
    } else {
      drawTexturedModalRect(stack,x + 2, y -1, 80 - i, 0, i + 1, 9);
      drawTexturedModalRect(stack,x, y-1, 0, 0, 2, 9);
    }
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

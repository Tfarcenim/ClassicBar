package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.impl.BarOverlayImpl;

public class ModUtils {
  public static final Minecraft mc = Minecraft.getInstance();
    public static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");
  public static final ResourceLocation THIRST_ICON = new ResourceLocation("toughasnails", "textures/gui/icons.png");
  private static final Font fontRenderer = mc.font;
  public static final ResourceLocation ICONS = new ResourceLocation("parcool:textures/gui/stamina_bar.png");
  public static ResourceLocation CURRENT_TEXTURE = BarOverlayImpl.GUI_ICONS_LOCATION;

  public static void drawTexturedModalRect(GuiGraphics stack, double x, int y, int textureX, int textureY, double width, int height) {
    stack.blit(CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height);
  }

  public static double getWidth(double d1, double d2) {
    double ratio = BarOverlayImpl.WIDTH * d1 / d2;
    return Math.ceil(ratio);
  }

  public static int getStringLength(String s) {
    return fontRenderer.width(s);
  }

  public static void drawStringOnHUD(GuiGraphics stack, String string, int xOffset, int yOffset, int color) {
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

    stack.drawString(Minecraft.getInstance().font,string, xOffset, yOffset, color,true);
  }
}

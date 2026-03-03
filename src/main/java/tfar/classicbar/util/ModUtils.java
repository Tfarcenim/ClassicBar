package tfar.classicbar.util;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.impl.BarOverlayImpl;

public class ModUtils {
  public static final Minecraft mc = Minecraft.getInstance();
  // Changed: these three ResourceLocation constants were previously declared as private static
  // fields inside their respective overlay classes (Blood, Thirst, StaminaB) and returned by
  // their individual getIconRL() overrides. They are now centralized here so that
  // ClassicBarsConfig.makeDefaultBarSettings() can reference them when writing default
  // JSON settings files, enabling per-bar icon overrides via config.
  public static final ResourceLocation VAMPIRISM_ICONS = ResourceLocation.parse("vampirism:textures/gui/icons.png");
  public static final ResourceLocation THIRST_ICON = ResourceLocation.fromNamespaceAndPath("toughasnails", "textures/gui/icons.png");
  public static final ResourceLocation ICONS = ResourceLocation.parse("parcool:textures/gui/stamina_bar.png");
  public static ResourceLocation CURRENT_TEXTURE = BarOverlayImpl.GUI_ICONS_LOCATION;

  public static void drawTexturedModalRect(GuiGraphics stack, double x, int y, int textureX, int textureY, double width, int height) {
    stack.blit(CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256);
  }

  public static double getWidth(double d1, double d2) {
    double ratio = BarOverlayImpl.WIDTH * d1 / d2;
    return Math.ceil(ratio);
  }

  public static int getStringLength(String s) {
    return mc.font.width(s);
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

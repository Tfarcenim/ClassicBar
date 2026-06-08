package tfar.classicbar.util;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import tfar.classicbar.impl.BarOverlayImpl;

public final class ModUtils {
  private ModUtils() {} // §16: utility class — private no-arg constructor
  public static final Minecraft mc = Minecraft.getInstance();
  // Changed: these three Identifier constants were previously declared as private static
  // fields inside their respective overlay classes (Blood, Thirst, StaminaB) and returned by
  // their individual getIconRL() overrides. They are now centralized here so that
  // ClassicBarsConfig.makeDefaultBarSettings() can reference them when writing default
  // JSON settings files, enabling per-bar icon overrides via config.
  public static final Identifier VAMPIRISM_ICONS = Identifier.fromNamespaceAndPath("vampirism", "textures/gui/icons.png");
  public static final Identifier THIRST_ICON = Identifier.fromNamespaceAndPath("toughasnails", "textures/gui/icons.png");
  public static final Identifier ICONS = Identifier.fromNamespaceAndPath("parcool", "textures/gui/stamina_bar.png");
  public static final Identifier THIRST_WAS_TAKEN_ICONS = Identifier.fromNamespaceAndPath("thirst", "textures/gui/thirst_icons.png"); // Thirst Was Taken mod icon texture
  public static final Identifier HOMEOSTATIC_ICONS = Identifier.fromNamespaceAndPath("homeostatic", "textures/gui/icons.png"); // Homeostatic mod icon texture (if exists)
  public static final Identifier IRONS_SPELLBOOKS_ICONS = Identifier.fromNamespaceAndPath("irons_spellbooks", "textures/gui/icons.png"); // Iron's Spells n Spellbooks icon texture
  public static Identifier CURRENT_TEXTURE = BarOverlayImpl.GUI_ICONS_LOCATION;

  // Changed: MC 26.1 removed RenderSystem.setShaderColor; GUI blits no longer read a global
  // shader color. Tint is now passed per-blit as a packed ARGB int. Color.color2Gla() writes
  // here and drawTexturedModalRect() reads it, preserving the old "set color then draw" flow.
  public static int CURRENT_COLOR = 0xFFFFFFFF;

  public static int argb(float a, float r, float g, float b) {
    return ((int) (a * 255f) & 0xFF) << 24
            | ((int) (r * 255f) & 0xFF) << 16
            | ((int) (g * 255f) & 0xFF) << 8
            | ((int) (b * 255f) & 0xFF);
  }

  public static void drawTexturedModalRect(GuiGraphicsExtractor stack, double x, int y, int textureX, int textureY, double width, int height) {
    stack.blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, (float) textureX, (float) textureY, (int) width, height, 256, 256, CURRENT_COLOR);
  }

  public static double getWidth(double d1, double d2) {
    double ratio = BarOverlayImpl.WIDTH * d1 / d2;
    return Math.ceil(ratio);
  }

  public static int getStringLength(String s) {
    return mc.font.width(s);
  }

  public static void drawStringOnHUD(GuiGraphicsExtractor stack, String string, int xOffset, int yOffset, int color) {
    xOffset += 2;
    yOffset += 2;

    // Changed: GuiGraphics.drawString -> GuiGraphicsExtractor.text (rename in MC 26.1).
    // OR 0xFF000000 so RGB-only colors (from Color.colorToText()) render fully opaque.
    stack.text(Minecraft.getInstance().font, string, xOffset, yOffset, color | 0xFF000000, true);
  }
}

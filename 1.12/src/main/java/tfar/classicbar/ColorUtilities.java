package tfar.classicbar;

import net.minecraft.client.renderer.GlStateManager;


import java.util.regex.Pattern;

import static tfar.classicbar.config.ModConfig.colors;

public class ColorUtilities {
    public static ColorUtilities cU = new ColorUtilities();
    public static final Pattern p1 = Pattern.compile("^#[0-9A-Fa-f]{6}$");


    public void color2Gl(Color c) {
        float r = c.gR() / 255f;
        float g = c.gG() / 255f;
        float b = c.gB() / 255f;
        GlStateManager.color(r, g, b);
    }

    public Color hex2Color(String s) {
        int i1 = Integer.parseInt(s, 16);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return new Color(r, g, b);
    }

    public Color colorBlend(Color c1, Color c2, double d) {
        int r = (int) Math.floor(c1.gR() * (1 - d) + c2.gR() * d);
        int g = (int) Math.floor(c1.gG() * (1 - d) + c2.gG() * d);
        int b = (int) Math.floor(c1.gB() * (1 - d) + c2.gB() * d);
        return new Color(r, g, b);
    }

    public Color calculateBarHexColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.advancedColors.healthFractions.length != colors.advancedColors.hexColors.length) return Color.BLACK;
        int i1 = colors.advancedColors.healthFractions.length - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colors.advancedColors.healthFractions[i2]) break;
            i3++;
        }
        //return first color in the list if health is too low
        if (d3 <= colors.advancedColors.healthFractions[0])
            return cU.hex2Color(colors.advancedColors.hexColors[0]);
        //return last color in the list if health is too high
        if (d3 >= colors.advancedColors.healthFractions[colors.advancedColors.healthFractions.length - 1])
            return cU.hex2Color(colors.advancedColors.hexColors[colors.advancedColors.healthFractions.length - 1]);

        Color c1 = cU.hex2Color(colors.advancedColors.hexColors[i3 - 1]);
        Color c2 = cU.hex2Color(colors.advancedColors.hexColors[i3]);

        double d4 = (d3 - colors.advancedColors.healthFractions[i3 - 1]) / (colors.advancedColors.healthFractions[i3] - colors.advancedColors.healthFractions[i3 - 1]);
        return cU.colorBlend(c1, c2, d4);
    }
}

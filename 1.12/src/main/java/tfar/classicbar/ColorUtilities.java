package tfar.classicbar;

import static tfar.classicbar.config.ModConfig.colors;
import java.util.regex.Pattern;

public class ColorUtilities {
    public static final Pattern p1 = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public static Color hex2Color(String s) {
        int i1 = Integer.decode(s);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return new Color(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.advancedColors.healthFractions.length != colors.advancedColors.hexColors.length) return new Color(0,0,0);
        int i1 = colors.advancedColors.healthFractions.length - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colors.advancedColors.healthFractions[i2]) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colors.advancedColors.healthFractions[0])
            return hex2Color(colors.advancedColors.hexColors[0]);
        //return last color in the list if health is too high
        if (d3 >= colors.advancedColors.healthFractions[colors.advancedColors.healthFractions.length - 1])
            return hex2Color(colors.advancedColors.hexColors[colors.advancedColors.healthFractions.length - 1]);

        Color c1 = hex2Color(colors.advancedColors.hexColors[i3 - 1]);
        Color c2 = hex2Color(colors.advancedColors.hexColors[i3]);

        double d4 = (d3 - colors.advancedColors.healthFractions[i3 - 1]) / (colors.advancedColors.healthFractions[i3] - colors.advancedColors.healthFractions[i3 - 1]);
        return c1.colorBlend(c2, d4);
    }

    public static Color calculatePoisonedScaledColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.advancedColors.healthFractions.length != colors.advancedColors.hexColors.length) return new Color(0,0,0);
        int i1 = colors.advancedColors.healthFractions.length - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colors.advancedColors.healthFractions[i2]) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colors.advancedColors.poisonedFractions[0])
            return hex2Color(colors.advancedColors.poisonedColors[0]);
        //return last color in the list if health is too high
        if (d3 >= colors.advancedColors.healthFractions[colors.advancedColors.poisonedFractions.length - 1])
            return hex2Color(colors.advancedColors.hexColors[colors.advancedColors.poisonedFractions.length - 1]);

        Color c1 = hex2Color(colors.advancedColors.poisonedColors[i3 - 1]);
        Color c2 = hex2Color(colors.advancedColors.poisonedColors[i3]);

        double d4 = (d3 - colors.advancedColors.poisonedFractions[i3 - 1]) / (colors.advancedColors.poisonedFractions[i3] - colors.advancedColors.poisonedFractions[i3 - 1]);
        return c1.colorBlend(c2, d4);
    }

    public static Color calculateWitheredScaledColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.advancedColors.healthFractions.length != colors.advancedColors.hexColors.length) return new Color(0,0,0);
        int i1 = colors.advancedColors.healthFractions.length - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colors.advancedColors.healthFractions[i2]) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colors.advancedColors.witheredFractions[0])
            return hex2Color(colors.advancedColors.witheredColors[0]);
        //return last color in the list if health is too high
        if (d3 >= colors.advancedColors.healthFractions[colors.advancedColors.witheredFractions.length - 1])
            return hex2Color(colors.advancedColors.hexColors[colors.advancedColors.witheredFractions.length - 1]);

        Color c1 = hex2Color(colors.advancedColors.witheredColors[i3 - 1]);
        Color c2 = hex2Color(colors.advancedColors.witheredColors[i3]);

        double d4 = (d3 - colors.advancedColors.witheredFractions[i3 - 1]) / (colors.advancedColors.witheredFractions[i3] - colors.advancedColors.witheredFractions[i3 - 1]);
        return c1.colorBlend(c2, d4);
    }
}

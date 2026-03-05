package tfar.classicbar.util;

import net.minecraft.util.Mth;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;

import java.util.List;


public final class ColorUtils {
    private ColorUtils() {} // §16: utility class — private no-arg constructor

    public static Color hex2Color(String s) {
        int i1 = Integer.decode(s);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return Color.from(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2, HealthEffect effect) {
        double d3 = (d1 / d2);

        // FROZEN: single flat color from config — no gradient needed
        if (effect == HealthEffect.FROZEN) return ConfigCache.frozenHealth;

        // §1: arrow-form switch expression to select color codes and fractions
        List<Color> colorCodes = switch (effect) {
            case NONE -> ConfigCache.normal;
            case POISON -> ConfigCache.poison;
            case WITHER -> ConfigCache.wither;
            default -> null;
        };
        List<? extends Double> colorFractions = switch (effect) {
            case NONE -> ClassicBarsConfig.normalFractions.get();
            case POISON -> ClassicBarsConfig.poisonedFractions.get();
            case WITHER -> ClassicBarsConfig.witheredFractions.get();
            default -> null;
        };
        if (colorCodes == null || colorFractions == null) return Color.BLACK;

        if (colorCodes.size() != colorFractions.size()) return Color.BLACK;
        int i1 = colorFractions.size() - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colorFractions.get(i2)) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colorFractions.get(0))
            return colorCodes.get(0);
        //return last color in the list if health is too high
        if (d3 >= colorFractions.get(colorFractions.size() - 1))
            return colorCodes.get(colorCodes.size() - 1);

        Color c1 = colorCodes.get(i3 - 1);
        Color c2 = colorCodes.get(i3);

        // Changed: old formula was: d3 - colorFractions.get(i3-1) / (colorFractions.get(i3) - colorFractions.get(i3-1))
        // That was a bug - missing parentheses around the numerator subtraction, so division
        // bound to only the second term. Mth.inverseLerp is the correct equivalent:
        // (d3 - start) / (end - start), properly computing the blend factor in [0,1].
        double d4 = Mth.inverseLerp(d3,colorFractions.get(i3-1),colorFractions.get(i3));
        return c1.colorBlend(c2, (float) d4);
    }
}

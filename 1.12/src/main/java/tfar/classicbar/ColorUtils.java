package tfar.classicbar;

import java.util.regex.Pattern;

public class ColorUtils {
    public static final Pattern p1 = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public static Color hex2Color(String s) {
        int i1 = Integer.decode(s);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return new Color(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2, String[] colorCodes, double[] colorFractions) {
        double d3 = (d1 / d2);
        if (colorCodes.length != colorFractions.length) return Color.BLACK;
        int i1 = colorFractions.length - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colorFractions[i2]) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colorFractions[0])
            return hex2Color(colorCodes[0]);
        //return last color in the list if health is too high
        if (d3 >= colorFractions[colorFractions.length - 1])
            return hex2Color(colorCodes[colorFractions.length - 1]);

        Color c1 = hex2Color(colorCodes[i3 - 1]);
        Color c2 = hex2Color(colorCodes[i3]);

        double d4 = (d3 - colorFractions[i3 - 1]) / (colorFractions[i3] - colorFractions[i3 - 1]);
        return c1.colorBlend(c2, d4);
    }
}

package tfar.classicbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tfar.classicbar.config.ModConfig.colors;

public class ScalingBarHandler {

    public static Pattern p = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    static String s4 = "000000";
    public static int r1;
    public static int b1;
    public static int g1;
    public static int r2;
    public static int b2;
    public static int g2;
    public static int r3;
    public static int b3;
    public static int g3;
    public static int a;
    public ScalingBarHandler()
    {
        r1 = b1 = g1 = r2 = b2 = g2 = a = 1;
    }

    public static String calculateBarHexColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.f.length != colors.s.length)return "#000000";
        int i1 = colors.f.length;
        int i3 = 0;
        for (int i2 =0; i2<i1;i2++) {
            if (d3 <= colors.f[i2]) break;
            i3++;
        }

        if (d3 <= colors.f[0])return colors.s[0];
        if (d3 >= colors.f[i3])return colors.s[i3];
        String s1 = colors.s[i3-1];
        String s2 = colors.s[i3];
        Matcher m1 = p.matcher(s1);
        Matcher m2 = p.matcher(s2);
        if (m1.matches() && m2.matches()){
            r1 = Integer.valueOf(s1.substring(1, 3), 16);
            g1 = Integer.valueOf(s1.substring(3, 5), 16);
            b1 = Integer.valueOf(s1.substring(5), 16);

            r2 = Integer.valueOf(s2.substring(1, 3), 16);
            g2 = Integer.valueOf(s2.substring(3, 5), 16);
            b2 = Integer.valueOf(s2.substring(5), 16);

            double d4 = (d3-colors.f[i3-1])/(colors.f[i3]-colors.f[i3-1]);

            r3 = (int)Math.abs(Math.floor(r1 * (1-d4) + r2 * d4));
            g3 = (int)Math.abs(Math.floor(g1 * (1-d4) + g2 * d4));
            b3 = (int)Math.abs(Math.floor(b1 * (1-d4) + b2 * d4));

            r3 = r3 << 16;
            g3 = g3 << 8;

            String s3 = Integer.toHexString(r3 + g3 + b3);
            if (s3.length()<6)s3 =s4.substring(s3.length()-1,5)+s3;
            return "#"+s3;

        } else return "#000000";
    }

    }

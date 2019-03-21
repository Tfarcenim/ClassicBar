package tfar.classicbar;

import net.minecraft.client.renderer.GlStateManager;


import static tfar.classicbar.ClassicBar.logger;
import static tfar.classicbar.Color.BLACK;
import static tfar.classicbar.config.ModConfig.colors;

public class ColorUtilities {
    public static ColorUtilities cU = new ColorUtilities();

    public void color2gl(Color c){
        float r= c.gR()/255f;
        float g= c.gG()/255f;
        float b= c.gB()/255f;
        GlStateManager.color(r,g,b);
    }

    public Color hex2Color(String s){
        if(s.startsWith("#")) s = s.substring(1);
        try {
            int i1 = Integer.parseInt(s, 16);
            int r = i1 >> 16 & 0xFF;
            int g = i1 >> 8 & 0xFF;
            int b = i1 & 0xFF;
            return new Color(r,g,b);
        } catch (NumberFormatException usersAmirite) {
            logger.error("USER IS AN IDIOT AND PUT IN A BAD HEX CODE");
            usersAmirite.printStackTrace();
            return BLACK;
        }
    }
    public Color colorBlend(Color c1, Color c2, double d){
        int r = (int)Math.floor(c1.gR()*(1-d) + c2.gR()*d);
        int g = (int)Math.floor(c1.gG()*(1-d) + c2.gG()*d);
        int b = (int)Math.floor(c1.gB()*(1-d )+ c2.gB()*d);
        return new Color(r,g,b);
    }

    public Color calculateBarHexColor(double d1, double d2) {
        double d3 = (d1 / d2);
        if (colors.f.length != colors.s.length) return Color.BLACK;
        int i1 = colors.f.length;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 <= colors.f[i2]) break;
            i3++;
        }
        if (d3 <= colors.f[0]) return cU.hex2Color(colors.s[0]);
        if (d3 >= colors.f[i3]) return cU.hex2Color(colors.s[i3]);

        Color c1 = cU.hex2Color(colors.s[i3 - 1]);
        Color c2 = cU.hex2Color(colors.s[i3]);

        double d4 = (d3 - colors.f[i3 - 1]) / (colors.f[i3] - colors.f[i3 - 1]);
        return cU.colorBlend(c1, c2, d4);
    }
}

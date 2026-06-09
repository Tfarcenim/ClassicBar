package tfar.classicbar.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;

import java.util.List;

public record Color(int a,int r,int g,int b) {
    public static final Color WHITE = Color.fromRGB(0xff,0xff,0xff);
    public static final Color BLACK = Color.fromRGB(0,0,0);
    public static final Color RED = Color.fromRGB(0xff,0,0);
    public static final Color YELLOW = Color.fromRGB(0xff,0xff,0);
    public static final Color FEATHERS = Color.hex2Color("#22a5f0");


    public static Color fromRGB(int red, int green, int blue) {
        return fromRGBA(red, green, blue,0xff);
    }

    public static Color fromRGBA(int red, int green, int blue, int alpha) {
        return new Color(alpha,red, green, blue);
    }

    public static final Codec<Color> CODEC = RecordCodecBuilder.create(
            colorInstance -> colorInstance.group(
                    Codec.INT.fieldOf("alpha").forGetter(Color::a),
                    Codec.INT.fieldOf("red").forGetter(Color::r),
                    Codec.INT.fieldOf("green").forGetter(Color::g),
                    Codec.INT.fieldOf("blue").forGetter(Color::b)
            ).apply(colorInstance, Color::new)
    );

    public static final Codec<Color> HEX_CODEC = Codec.STRING.xmap(Color::hex2Color, Color::toHexString);


    public static Color hex2Color(String s) {
        s = s.startsWith("#") ? s.substring(1) : s;
        int i1 = Integer.parseUnsignedInt(s,16);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return fromRGB(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2, HealthEffect effect) {
        double d3 = (d1 / d2);

        List<Color> colorCodes;
        List<? extends Double> colorFractions;

        switch (effect) {
            case NONE: colorCodes = ConfigCache.normal;
            colorFractions = ClassicBarsConfig.normalFractions.get(); break;
            case POISON: colorCodes = ConfigCache.poison;
                colorFractions = ClassicBarsConfig.poisonedFractions.get(); break;
            case WITHER: colorCodes = ConfigCache.wither;
                colorFractions = ClassicBarsConfig.witheredFractions.get(); break;
            case FROZEN:return ConfigCache.frozenHealth;
            default: return BLACK;
        }

        if (colorCodes.size() != colorFractions.size()) return BLACK;
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

        double d4 = Mth.inverseLerp(d3,colorFractions.get(i3-1),colorFractions.get(i3));
        return c1.colorBlend(c2, (float) d4);
    }

    public void color2Gl() {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;
        float a = this.a / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public Color withAlpha(float alpha) {
        return fromRGBA(this.r, this.g, this.b, (int) (alpha * 0xff));
    }

    public Color colorBlend(Color c2, float d) {
        int r = Mth.lerpInt(d,this.r,c2.r);
        int g = Mth.lerpInt(d,this.g,c2.g);
        int b = Mth.lerpInt(d,this.b,c2.b);
        return Color.fromRGB(r, g, b);
    }

    public String toHexString() {
        return "#"+Integer.toHexString(colorToText());
    }

    public int colorToText(){
        return this.a << 24 | this.r << 16 | this.g << 8 | this.b;
    }

    public static void reset() {
        RenderSystem.setShaderColor(1,1,1,1);
    }
}
package tfar.classicbar.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Mth;

public record Color(int r,int g,int b,int a) {
    public static final Color WHITE = Color.fromRGB(0xff,0xff,0xff);
    public static final Color BLACK = Color.fromRGB(0,0,0);
    public static final Color RED = Color.fromRGB(0xff,0,0);
    public static final Color YELLOW = Color.fromRGB(0xff,0xff,0);

    public static Color fromRGB(int red, int green, int blue) {
        return fromRGBA(red, green, blue,0xff);
    }

    public static Color fromRGBA(int red, int green, int blue, int alpha) {
        return new Color(red, green, blue,alpha);
    }


    public static Color from(String s) {
        return BLACK;
    }

    public void color2Gl() {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;
        float a = this.a / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public Color withAlpha(float alpha) {
        return new Color(this.r, this.g, this.b, (int) (alpha * 0xff));
    }

    public Color colorBlend(Color c2, float d) {
        int r = Mth.lerpInt(d,this.r,c2.r);
        int g = Mth.lerpInt(d,this.g,c2.g);
        int b = Mth.lerpInt(d,this.b,c2.b);
        return Color.fromRGB(r, g, b);
    }
    public int colorToText(){
        return this.r << 16 | this.g << 8 | this.b;
    }

    public static void reset() {
        RenderSystem.setShaderColor(1,1,1,1);
    }
}
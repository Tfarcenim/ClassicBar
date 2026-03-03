package tfar.classicbar.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Mth;

// Changed: converted from a plain class to a record; removes boilerplate constructor
// and explicit field declarations since records auto-generate them.
public record Color(int r,int g,int b) {
    public static final Color BLACK = Color.from(0,0,0);
    public static final Color RED = Color.from(0xff,0,0);
    public static final Color YELLOW = Color.from(0xff,0xff,0);

    public static Color from(int red, int green, int blue) {
        return new Color(red, green, blue);
    }

    public static Color from(String s) {
        return BLACK;
    }

    public void color2Gl() {
        color2Gla(1);
    }
    // Changed: parameter widened from double to float to match Mth.lerpInt's signature.
    // Replaced manual (int) Math.floor(a*(1-d) + b*d) with Mth.lerpInt, which is the
    // equivalent MC math utility and avoids a floor/cast pattern.
    public Color colorBlend(Color c2, float d) {
        int r = Mth.lerpInt(d,this.r,c2.r);
        int g = Mth.lerpInt(d,this.g,c2.g);
        int b = Mth.lerpInt(d,this.b,c2.b);
        return Color.from(r, g, b);
    }
    // Changed: replaced addition (+) with bitwise OR (|) for combining color channels.
    // Both produce identical results for valid 8-bit channel values; OR is semantically
    // correct and consistent with Minecraft's own packed-color conventions.
    public int colorToText(){
        return this.r << 16 | this.g << 8 | this.b;
    }

    public void color2Gla(float a) {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
    }
    public static void reset() {
        RenderSystem.setShaderColor(1,1,1,1);
    }
}
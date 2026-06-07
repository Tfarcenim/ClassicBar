package tfar.classicbar.util;

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

    // Changed: MC 26.1 removed RenderSystem.setShaderColor. The tint is now stored as a packed
    // ARGB int in ModUtils.CURRENT_COLOR and applied per-blit by ModUtils.drawTexturedModalRect.
    public void color2Gla(float a) {
        ModUtils.CURRENT_COLOR = ((int) (a * 255f) & 0xFF) << 24 | (this.r & 0xFF) << 16 | (this.g & 0xFF) << 8 | (this.b & 0xFF);
    }
    public static void reset() {
        ModUtils.CURRENT_COLOR = 0xFFFFFFFF;
    }
}
package tfar.classicbar;

import com.mojang.blaze3d.systems.RenderSystem;

public class Color {
    public final int r, g, b;
    public static final Color BLACK = new Color(0,0,0);

    public Color(int red, int green, int blue) {
        this.r = red;
        this.g = green;
        this.b = blue;
    }

    public void color2Gl() {
        color2Gla(1);
    }
    public Color colorBlend(Color c2, double d) {
        int r = (int) Math.floor(this.r * (1 - d) + c2.r * d);
        int g = (int) Math.floor(this.g * (1 - d) + c2.g * d);
        int b = (int) Math.floor(this.b * (1 - d) + c2.b * d);
        return new Color(r, g, b);
    }
    public int colorToText(){
        return (this.r << 16)+(this.g << 8) + this.b;
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
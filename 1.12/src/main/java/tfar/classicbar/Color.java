package tfar.classicbar;

public final class Color {
    private int red, green, blue;

    public Color(int r, int g, int b) {
        set(r, g, b);
    }

    public static final Color BLACK = new Color(0, 0, 0);

    public void set(int r, int g, int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    public int gR() {
        return this.red;
    }

    public int gG() {
        return this.green;
    }

    public int gB() {
        return this.blue;
    }
}
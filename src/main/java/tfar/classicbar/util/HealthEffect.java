package tfar.classicbar.util;

public enum HealthEffect {
    NONE(2),POISON(4),WITHER(6),FROZEN(9);

    public final int i;

    HealthEffect(int i){
        this.i = i;
    }

    public int getX() {
        return 16 + this.i * 18;
    }

}

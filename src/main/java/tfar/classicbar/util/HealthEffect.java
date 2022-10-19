package tfar.classicbar.util;

public enum HealthEffect {
    NONE(16),POISON(52),WITHER(88);

    public final int i;

    HealthEffect(int i){
        this.i = i;
    }
}

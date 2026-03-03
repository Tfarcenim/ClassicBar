package tfar.classicbar.util;

public enum HealthEffect {
    // Changed: values now store the direct texture atlas X offsets instead of index multipliers.
    // Old approach: values were 2,4,6,9 and getX() computed (16 + i*18) at call sites.
    // New approach: values ARE the offsets; call sites use (36 + effect.i) to get the same results.
    // getX() was removed as the offset math is now split: base 36 at call site + i here.
    // Note: these are only used for documentation now; Health.java icon rendering was
    // later replaced with blitSprite (named sprites), making the numeric values vestigial.
    NONE(16),POISON(52),WITHER(88),FROZEN(88 + 54);

    public final int i;

    HealthEffect(int i){
        this.i = i;
    }
}

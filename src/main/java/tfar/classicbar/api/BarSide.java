package tfar.classicbar.api;

import net.minecraft.util.StringRepresentable;

public enum BarSide implements StringRepresentable {
    LEFT("left"), RIGHT("right");

    public static final EnumCodec<BarSide> CODEC = StringRepresentable.fromEnum(BarSide::values);

    private final String name;

    BarSide(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

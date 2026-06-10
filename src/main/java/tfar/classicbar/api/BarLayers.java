package tfar.classicbar.api;

import net.minecraft.util.StringRepresentable;

public enum BarLayers implements StringRepresentable {
    SINGLE("single"),DUAL("dual");

    private final String name;

    BarLayers(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

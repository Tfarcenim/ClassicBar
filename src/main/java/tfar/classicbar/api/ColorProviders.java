package tfar.classicbar.api;

import com.mojang.serialization.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class ColorProviders {
    public static final Map<String,ColorProviderSerializer> MAP =  new HashMap<>();

    public static void init() {

    }

    public static final ColorProviderSerializer<SingleColorProvider> SINGLE_COLOR = register(new ColorProviderSerializer<>
            ("single_color",SingleColorProvider.CODEC));

    public static <C extends ColorProvider> ColorProviderSerializer<C> register(ColorProviderSerializer<C> serializer) {
        MAP.put(serializer.name(), serializer);
        return serializer;
    }
}

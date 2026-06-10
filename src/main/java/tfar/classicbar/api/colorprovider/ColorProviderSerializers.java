package tfar.classicbar.api.colorprovider;

import java.util.HashMap;
import java.util.Map;

public class ColorProviderSerializers {
    public static final Map<String,ColorProviderSerializer> MAP =  new HashMap<>();

    public static void init() {

    }

    public static final ColorProviderSerializer<SingleColorProvider> SINGLE_COLOR = register(new ColorProviderSerializer<>
            ("single",SingleColorProvider.CODEC));

    public static final ColorProviderSerializer<DualColorProvider> DUAL_COLOR = register(new ColorProviderSerializer<>
            ("dual",DualColorProvider.CODEC));

    public static final ColorProviderSerializer<HealthColorProvider> HEALTH_COLOR = register(new ColorProviderSerializer<>
            ("health",HealthColorProvider.CODEC));

    public static <C extends ColorProvider> ColorProviderSerializer<C> register(ColorProviderSerializer<C> serializer) {
        MAP.put(serializer.name(), serializer);
        return serializer;
    }
}

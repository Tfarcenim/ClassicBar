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

    public static final ColorProviderSerializer<TransitioningColorProvider> TRANSITIONING = register(new ColorProviderSerializer<>
            ("transitioning", TransitioningColorProvider.CODEC));

    public static final ColorProviderSerializer<TransitioningEffectColorProvider> TRANSITIONING_EFFECT = register(new ColorProviderSerializer<>
            ("transitioning_effect", TransitioningEffectColorProvider.CODEC));

    public static final ColorProviderSerializer<StackingColorProvider> STACKING = register(new ColorProviderSerializer<>
            ("stacking", StackingColorProvider.CODEC));


    public static final ColorProviderSerializer<StackingEffectColorProvider> STACKING_EFFECT = register(new ColorProviderSerializer<>
            ("stacking_effect", StackingEffectColorProvider.CODEC));

    public static final ColorProviderSerializer<DualEffectColorProvider> DUAL_EFFECT = register(new ColorProviderSerializer<>
            ("dual_effect", DualEffectColorProvider.CODEC));

    public static <C extends ColorProvider> ColorProviderSerializer<C> register(ColorProviderSerializer<C> serializer) {
        MAP.put(serializer.name(), serializer);
        return serializer;
    }
}

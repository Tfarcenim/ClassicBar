package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.impl.overlays.templates.BarOverlayImpl;
import tfar.classicbar.util.HealthEffect;

import java.util.LinkedHashMap;
import java.util.Map;

public record TransitioningEffectColorProvider(Map<Float, Color> normalColors,
                                               Map<Float, Color> poisonColors,
                                               Map<Float, Color> witherColors,
                                               Map<Float, Color> frozenColors) implements ColorProvider {

    public static final MapCodec<TransitioningEffectColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TransitioningColorProvider.codec().fieldOf("normal").forGetter(TransitioningEffectColorProvider::normalColors),
            TransitioningColorProvider.codec().fieldOf("poison").forGetter(TransitioningEffectColorProvider::poisonColors),
            TransitioningColorProvider.codec().fieldOf("wither").forGetter(TransitioningEffectColorProvider::witherColors),
            TransitioningColorProvider.codec().fieldOf("frozen").forGetter(TransitioningEffectColorProvider::frozenColors)
    ).apply(instance, TransitioningEffectColorProvider::new));

    public static final TransitioningEffectColorProvider DEFAULT = createDefault();


    static TransitioningEffectColorProvider createDefault() {
        Map<Float, Color> normal = new LinkedHashMap<>();
        normal.put(.25f, Color.hex2Color("#FF0000"));
        normal.put(.5f, Color.hex2Color("#FFFF00"));
        normal.put(.75f, Color.hex2Color("#00FF00"));

        Map<Float, Color> poison = new LinkedHashMap<>();
        poison.put(.25f, Color.hex2Color("#00FF00"));
        poison.put(.5f, Color.hex2Color("#55FF55"));
        poison.put(.75f, Color.hex2Color("#00FF00"));

        Map<Float, Color> wither = new LinkedHashMap<>();
        wither.put(.25f, Color.hex2Color("#555555"));
        wither.put(.5f, Color.hex2Color("#AAAAAA"));
        wither.put(.75f, Color.hex2Color("#555555"));
        Map<Float, Color> frozen = new LinkedHashMap<>();
        frozen.put(.25f, Color.hex2Color("#7fafff"));
        frozen.put(.5f, Color.hex2Color("#7fafff"));
        frozen.put(.75f, Color.hex2Color("#7fafff"));
        return new TransitioningEffectColorProvider(normal, poison, wither, frozen);
    }

    public Color calculateScaledColor(double d3, HealthEffect effect) {

        Map<Float, Color> colorCodes = switch (effect) {
            case NONE -> normalColors;
            case POISON -> poisonColors;
            case WITHER -> witherColors;
            case FROZEN -> frozenColors;
        };
        return TransitioningColorProvider.calculateScaledColor(d3, colorCodes);
    }

    @Override
    public Color getColor(Player player, float ratio, int layer) {
        HealthEffect effect = BarOverlayImpl.getHealthEffect(player);
        return calculateScaledColor(ratio, effect);
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.TRANSITIONING_EFFECT;
    }
}

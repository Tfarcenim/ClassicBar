package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tfar.classicbar.api.Color;

public record TwoColors(Color primary, Color secondary) {
    public static final Codec<TwoColors> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(Color.HEX_CODEC.fieldOf("primary").forGetter(TwoColors::primary),
                Color.HEX_CODEC.fieldOf("secondary").forGetter(TwoColors::secondary)
        ).apply(instance, TwoColors::new));
}

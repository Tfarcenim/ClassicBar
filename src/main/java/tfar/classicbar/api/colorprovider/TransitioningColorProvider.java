package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public record TransitioningColorProvider(Map<Float, Color> colors) implements ColorProvider {

    public static final MapCodec<TransitioningColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec().fieldOf("colors").forGetter(TransitioningColorProvider::colors)
    ).apply(instance, TransitioningColorProvider::new));

    public static UnboundedMapCodec<Float, Color> codec() {
        return Codec.unboundedMap(Codec.STRING.xmap(Float::parseFloat, Object::toString), Color.HEX_CODEC);
    }

    public static final TransitioningColorProvider DEFAULT = createDefault();

    static TransitioningColorProvider createDefault() {
        Map<Float, Color> colors = new LinkedHashMap<>();
        colors.put(.25f, Color.hex2Color("#FF0000"));
        colors.put(.5f, Color.hex2Color("#FFFF00"));
        colors.put(.75f, Color.hex2Color("#00FF00"));

        return new TransitioningColorProvider(colors);
    }

    public Color calculateScaledColor(double d3) {
        return calculateScaledColor((float) d3,colors);
    }


    public static Color calculateScaledColor(double d3,Map<Float, Color> colors) {

        int index = -1;

        Color below = null;
        Float belowFraction = null;
        Color above = null;
        Float aboveFraction = null;
        boolean aboveEverything = true;
        for (Map.Entry<Float, Color> entry : colors.entrySet()) {
            float fraction = entry.getKey();
            above = entry.getValue();
            aboveFraction = fraction;
            if (fraction > d3) {
                aboveEverything = false;
                break;
            }
            below = entry.getValue();
            belowFraction = fraction;
            index++;
        }

        //return first color in the list if health is too low
        if (index == -1) {
            return above;
        }

        //return last color in the list if health is too high
        if (aboveEverything) {
            return below;
        }

        double d4 = Mth.inverseLerp(d3, belowFraction, aboveFraction);
        return below.colorBlend(above, (float) d4);
    }

    @Override
    public Color getColor(Player player, float ratio, BarLayer layer) {
        return calculateScaledColor(ratio);
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.TRANSITIONING;
    }
}

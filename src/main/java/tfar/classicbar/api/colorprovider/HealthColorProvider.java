package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.HealthEffect;

import java.util.Map;

public record HealthColorProvider(Map<Float,Color> normalColors,
                                  Map<Float,Color> poisonColors,
                                  Map<Float,Color> witherColors,
                                  Map<Float,Color> frozenColors) implements ColorProvider {

    public static final MapCodec<HealthColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec().fieldOf("normal").forGetter(HealthColorProvider::normalColors),
            codec().fieldOf("poison").forGetter(HealthColorProvider::poisonColors),
            codec().fieldOf("wither").forGetter(HealthColorProvider::witherColors),
            codec().fieldOf("frozen").forGetter(HealthColorProvider::frozenColors)
    ).apply(instance,HealthColorProvider::new));

    public static UnboundedMapCodec<Float, Color> codec() {
        return Codec.unboundedMap(Codec.STRING.xmap(Float::parseFloat, Object::toString),Color.HEX_CODEC);
    }

    public static final HealthColorProvider DEFAULT = createDefault();

    static HealthColorProvider createDefault() {
        Map<Float,Color> normal = Map.of(.25f,Color.hex2Color("#FF0000"),.5f,Color.hex2Color("#FFFF00"),.75f,Color.hex2Color("#00FF00"));
        Map<Float,Color> poison = Map.of(.25f,Color.hex2Color("#00FF00"),.5f,Color.hex2Color("#55FF55"),.75f,Color.hex2Color("#00FF00"));
        Map<Float,Color> wither = Map.of(.25f,Color.hex2Color("#555555"),.5f,Color.hex2Color( "#AAAAAA"),.75f,Color.hex2Color( "#555555"));
        Map<Float,Color> frozen = Map.of(.25f,Color.hex2Color( "#7fafff"),.5f,Color.hex2Color( "#7fafff"),.75f,Color.hex2Color( "#7fafff"));
        return new HealthColorProvider(normal,poison,wither,frozen);
    }

    public Color calculateScaledColor(double d1, double d2, HealthEffect effect) {
        double d3 = (d1 / d2);

        Map<Float, Color> colorCodes = switch (effect) {
            case NONE -> normalColors;
            case POISON -> poisonColors;
            case WITHER -> witherColors;
            case FROZEN -> frozenColors;
        };

        int index = -1;

        Color below = null;
        Float belowFraction = null;
        Color above = null;
        Float aboveFraction = null;
        boolean aboveEverything = true;
        for (Map.Entry<Float, Color> entry : colorCodes.entrySet()) {
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
        if (aboveEverything) {return below;}



        double d4 = Mth.inverseLerp(d3,belowFraction,aboveFraction);
        return below.colorBlend(above, (float) d4);
    }

    @Override
    public Color getColor(Player player, int layer) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        HealthEffect effect = BarOverlayImpl.getHealthEffect(player);
        return calculateScaledColor(health,maxHealth,effect);
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.HEALTH_COLOR;
    }
}

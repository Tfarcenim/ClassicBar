package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.HealthEffect;

import java.util.List;
import java.util.stream.Stream;

public record StackingEffectColorProvider(List<Color> normalColors,
                                          List<Color> poisonColors,
                                          List<Color> witherColors,
                                          List<Color> frozenColors) implements ColorProvider{

    public static final MapCodec<StackingEffectColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.listOf().fieldOf("normal").forGetter(StackingEffectColorProvider::normalColors),
            Color.HEX_CODEC.listOf().fieldOf("poison").forGetter(StackingEffectColorProvider::normalColors),
            Color.HEX_CODEC.listOf().fieldOf("wither").forGetter(StackingEffectColorProvider::normalColors),
            Color.HEX_CODEC.listOf().fieldOf("frozen").forGetter(StackingEffectColorProvider::normalColors)

    ).apply(instance, StackingEffectColorProvider::new));

    public static final StackingEffectColorProvider DEFAULT_ABSORPTION = new StackingEffectColorProvider(
            Stream.of("#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF").map(Color::hex2Color).toList(),
            Stream.of("#94ef00","#82ff00","#4dff00","#00fa37","#0a9b84","#98daa2","#9fdd87","#9fe95d","#94ff5d","#00c486","#78ffa8","#9fff9f").map(Color::hex2Color).toList(),
            Stream.of("#939393","#969696","#828282","#777777","#787878","#c6c6c6","#c1c1c1","#b7b7b7","#c5c5c5","#828282","#cbcbcb","#dfdfdf").map(Color::hex2Color).toList(),
            Stream.of("#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8","#B8C1E8").map(Color::hex2Color).toList());


    @Override
    public Color getColor(Player player, float ratio, int layer) {
        HealthEffect effect = BarOverlayImpl.getHealthEffect(player);
        return switch (effect) {
            case NONE -> normalColors.get(Math.min(layer,normalColors.size()-1));
            case POISON -> poisonColors.get(Math.min(layer,poisonColors.size()-1));
            case WITHER -> witherColors.get(Math.min(layer,witherColors.size()-1));
            case FROZEN -> frozenColors.get(Math.min(layer,frozenColors.size()-1));

        };
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.STACKING_EFFECT;
    }
}

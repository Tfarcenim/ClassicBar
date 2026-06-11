package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

import java.util.List;
import java.util.stream.Stream;

public record StackingColorProvider(List<Color> colors) implements ColorProvider{

    public static final MapCodec<StackingColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.listOf().fieldOf("colors").forGetter(StackingColorProvider::colors)
    ).apply(instance, StackingColorProvider::new));

    public static final StackingColorProvider DEFAULT_ARMOR = new StackingColorProvider(
            Stream.of("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF").map(Color::hex2Color).toList());

    @Override
    public Color getColor(Player player, float ratio, int layer) {
        return colors.get(Math.min(layer, colors.size() - 1));
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.STACKING;
    }
}

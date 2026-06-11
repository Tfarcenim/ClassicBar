package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

import java.util.List;

public record WrappingColorProvider(List<Color> colors) implements ColorProvider{

    public static final MapCodec<WrappingColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.listOf().fieldOf("colors").forGetter(WrappingColorProvider::colors)
    ).apply(instance, WrappingColorProvider::new));

    @Override
    public Color getColor(Player player, float ratio, int layer) {
        return colors.get(Math.min(layer, colors.size() - 1));
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.WRAPPING;
    }
}

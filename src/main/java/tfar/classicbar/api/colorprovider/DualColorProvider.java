package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

public record DualColorProvider(Color primary, Color secondary) implements ColorProvider{

    public static final MapCodec<DualColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("primary").forGetter(DualColorProvider::primary),
            Color.HEX_CODEC.fieldOf("secondary").forGetter(DualColorProvider::secondary)
    ).apply(instance,DualColorProvider::new));

    @Override
    public Color getColor(Player player, float ratio, int layer) {
        if (layer == 0) return primary;
        return  secondary;
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.DUAL_COLOR;
    }
}

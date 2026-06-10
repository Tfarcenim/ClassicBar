package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

public record DualColorProvider(Color layer0, Color layer1) implements ColorProvider{

    public static final MapCodec<DualColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("layer0").forGetter(DualColorProvider::layer0),
            Color.HEX_CODEC.fieldOf("layer1").forGetter(DualColorProvider::layer1)
    ).apply(instance,DualColorProvider::new));

    @Override
    public Color getColor(Player player, int layer) {
        return layer == 0 ? layer0 : layer1;
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.DUAL_COLOR;
    }
}

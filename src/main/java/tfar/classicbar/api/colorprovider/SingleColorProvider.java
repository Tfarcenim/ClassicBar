package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

public record SingleColorProvider(Color color) implements ColorProvider {

    public static final MapCodec<SingleColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("color").forGetter(SingleColorProvider::color)).apply(instance,SingleColorProvider::new));

    @Override
    public Color getColor(Player player, int layer) {
        return color;
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.SINGLE_COLOR;
    }
}

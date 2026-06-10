package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

public record SingleColorProvider(Color color) implements ColorProvider {

    public static final MapCodec<SingleColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("color").forGetter(SingleColorProvider::color)).apply(instance,SingleColorProvider::new));

    @Override
    public Color getColor(Player player, int layer) {
        return color;
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviders.SINGLE_COLOR;
    }

    @Override
    public String getSerializedName() {
        return "single_color_provider";
    }
}

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
        int primaryIndex = (int) Mth.clamp(Math.ceil(ratio) - 1,0,colors.size()-1);//0 - 20 = 0, 21 - 40 = 1 etc
        int secondaryIndex = primaryIndex - 1;

        return switch (layer) {
            case 1 ->  colors.get(primaryIndex);
            case 0 -> colors.get(secondaryIndex);
            default -> throw new IllegalArgumentException("Invalid layer " + layer);
        };
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.WRAPPING;
    }
}

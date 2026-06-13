package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.compat.ModCompat;

public record DualColorProvider(Color primary, Color secondary) implements ColorProvider{

    public static final MapCodec<DualColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("primary").forGetter(DualColorProvider::primary),
            Color.HEX_CODEC.fieldOf("secondary").forGetter(DualColorProvider::secondary)
    ).apply(instance,DualColorProvider::new));

    public static final DualColorProvider THIRST_WAS_TAKEN = new DualColorProvider(Color.hex2Color("#1C5EE4"),Color.hex2Color("#00A3E2"));

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

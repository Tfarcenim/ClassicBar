package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

public interface ColorProvider {

    Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProviderSerializers.MAP::get, ColorProviderSerializer::name)
            .dispatch(ColorProvider::getSerializer, c -> c.codec().codec());

    Color getColor(Player player, BarLayer priority);
    ColorProviderSerializer<?> getSerializer();
}

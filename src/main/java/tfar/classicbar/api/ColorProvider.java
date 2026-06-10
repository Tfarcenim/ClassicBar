package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

public interface ColorProvider extends StringRepresentable {

    Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProviders.MAP::get, ColorProviderSerializer::name)
            .dispatch(ColorProvider::getSerializer, c -> c.codec().codec());

    Color getColor(Player player,int layer);
    ColorProviderSerializer<?> getSerializer();
}

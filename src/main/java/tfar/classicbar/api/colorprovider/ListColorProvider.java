package tfar.classicbar.api.colorprovider;

import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;

import java.util.List;

public record ListColorProvider(List<Color> colors) implements ColorProvider{
    @Override
    public Color getColor(Player player, BarLayer priority) {
        return null;
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return null;
    }
}

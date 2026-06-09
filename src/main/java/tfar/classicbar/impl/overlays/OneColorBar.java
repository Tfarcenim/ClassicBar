package tfar.classicbar.impl.overlays;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarOverlayImpl;

import java.util.Set;
import java.util.function.Function;

public class OneColorBar extends BarOverlayImpl {
    public OneColorBar(String name, BarSettings barSettings, Function<Player, Float> widthGetter) {
        super(name, barSettings, widthGetter);
    }

    public OneColorBar(String name, BarSettings barSettings, String dependency, Function<Player, Float> widthGetter) {
        super(name, barSettings, dependency, widthGetter);
    }

    public OneColorBar(String name, BarSettings barSettings, Set<String> dependencies, Function<Player, Float> widthGetter) {
        super(name, barSettings, dependencies, widthGetter);
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

    }

    @Override
    public Codec<? extends BarOverlayImpl> getCodec() {
        return null;
    }
}

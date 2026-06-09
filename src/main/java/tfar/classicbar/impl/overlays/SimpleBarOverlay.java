package tfar.classicbar.impl.overlays;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarOverlayImpl;

public class SimpleBarOverlay extends BarOverlayImpl {
    private final int barColor;

    public SimpleBarOverlay(String name, BarSettings barSettings, int bar_color) {
        super(name, barSettings,null);
        barColor = bar_color;
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

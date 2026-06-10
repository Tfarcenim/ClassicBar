package tfar.classicbar.impl.overlays;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;

public abstract class OneColorBar extends BarOverlayImpl {

    protected OneColorBar(BarInfo barInfo, BarSettings barSettings) {
        super(barInfo, barSettings);
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderSimpleBar(getBarSettings().colorProvider().getColor(player,0), graphics, player, screenWidth, screenHeight, vOffset);
    }
}

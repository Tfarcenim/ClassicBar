package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;

// Changed (MC 26.1 upgrade): "Thirst Was Taken" compat replaced with a disabled stub because
// no 26.1 build of the mod is available. The original implementation rendered a thirst bar
// driven by the "thirst" mod's API. All of that is removed; shouldRender() now always returns
// false so nothing is drawn. NAME is kept because ClassicBarsConfig still references it.
// Restore the full implementation once a 26.1-compatible "Thirst Was Taken" build exists.
public class ThirstWasTaken extends BarOverlayImpl {

    public static final String NAME = "thirst_was_taken";

    public ThirstWasTaken() {
        super(NAME);
    }

    @Override
    public boolean shouldRender(Player player) {
        return false;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
    }

    @Override
    public void renderText(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
    }

    @Override
    public void renderIcon(GuiGraphicsExtractor graphics, Player player, int width, int height, int vOffset) {
    }

    @Override
    public double getBarWidth(Player player) {
        return 0;
    }
}

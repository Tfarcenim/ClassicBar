package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;

// Changed (MC 26.1 upgrade): ParCool compat replaced with a disabled stub because no 26.1
// build of ParCool is available. The original implementation rendered a stamina bar driven by
// com.alrex.parcool.* (Stamina / ParCoolConfig / HUDType). All of that is removed;
// shouldRender() now always returns false so nothing is drawn.
// Restore the full implementation once a 26.1-compatible ParCool build exists.
public class StaminaB extends BarOverlayImpl {

    public static final String name = "parcool:stamina";

    public StaminaB() {
        super(name);
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

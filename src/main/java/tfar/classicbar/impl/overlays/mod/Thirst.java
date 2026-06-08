package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;

// Changed (MC 26.1 upgrade): Tough As Nails compat replaced with a disabled stub because no
// 26.1 build of Tough As Nails is available. The original implementation rendered a thirst bar
// driven by toughasnails.* (IThirst / ThirstHelper / ModTags / TANEffects) and contained the
// drink thirst/hydration tag lookups. All of that is removed; shouldRender() now always returns
// false so nothing is drawn, and isEnabled() returns false so the vanilla TAN HUD is not
// suppressed. NAME and OVERLAY_ID are kept because EventHandler still references them.
// Restore the full implementation once a 26.1-compatible Tough As Nails build exists.
public class Thirst extends BarOverlayImpl {

    public static final String NAME = "thirst_level";
    public static final Identifier OVERLAY_ID = Identifier.fromNamespaceAndPath("toughasnails", NAME);

    public Thirst() {
        super(NAME);
    }

    // Whether ClassicBar's thirst bar is enabled. Always false while TAN compat is disabled.
    public static boolean isEnabled() {
        return false;
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

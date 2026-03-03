package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.impl.BarOverlayImpl;

// Changed: Feathers compat replaced with a disabled stub because no 1.21.1 build of the
// Feathers mod is available. The original implementation included:
//   - DODGE_ICONS ResourceLocation constant (from elenaidodge2 mod)
//   - shouldRenderText() override returning ClassicBarsConfig.showHungerNumbers.get()
//   - getIconRL() override returning FeathersHudOverlay.ICONS
//   - Full renderBar/renderText/renderIcon/getBarWidth implementations
// All of these are removed; shouldRender() now always returns false so nothing is drawn.
public class Feathers extends BarOverlayImpl {

public Feathers() {
super("feathers");
}

@Override
public boolean shouldRender(Player player) {
return false;
}

@Override
public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
}

@Override
public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
}

@Override
public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
}

@Override
public double getBarWidth(Player player) {
return 0;
}
}

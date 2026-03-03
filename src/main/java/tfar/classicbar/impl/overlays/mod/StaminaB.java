package tfar.classicbar.impl.overlays.mod;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.client.hud.impl.HUDType;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

// Changed: removed shouldRenderText() override (was ClassicBarsConfig.showAirNumbers.get()) and
// removed getIconRL() override (was returning ICONS directly). Both now handled via barSettings.
// Also updated to use the new Stamina API (IStamina -> Stamina).
public class StaminaB extends BarOverlayImpl {

    public static final String name = "parcool:stamina";

    public StaminaB() {
        super(name);
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!checkConfigs()) return false;
        Stamina stamina = Stamina.get(player);
        return stamina != null && stamina.getMaxValue() > stamina.getValue();
    }

    // Changed: old check was four separate boolean config flags:
    //   useLightHUD.get() && !hideStaminaHUD.get() && !infiniteStamina.get() && !useHungerBarInsteadOfStamina.get()
    // New ParCool API consolidates this into a single HUDType enum; Light type means the
    // external/classic-bar-style HUD should be shown.
    public static boolean checkConfigs() {
        return ParCoolConfig.Client.StaminaHUDType.get() == HUDType.Light;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double barWidth = getBarWidth(player);
        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        Color color = getPrimaryBarColor(0, player);
        color.color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        Stamina stamina = Stamina.get(player);
        if (stamina == null) return 0;
        int cur = stamina.getValue();
        int max = stamina.getMaxValue();
        if (max == 0) return 0;
        return Math.ceil((double) BarOverlayImpl.WIDTH * cur / max);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.YELLOW;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        Stamina stamina = Stamina.get(player);
        if (stamina == null) return;
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        Color color = getPrimaryBarColor(0, player);
        textHelper(graphics, xStart, yStart, stamina.getValue() / 20, color.colorToText());
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        Stamina stamina = Stamina.get(player);
        int textureX = (stamina != null && stamina.isExhausted()) ? 16 : 0;
        graphics.blit(getIconRL(), xStart, yStart, textureX, 119, 8, 9, 128, 128);
    }
}

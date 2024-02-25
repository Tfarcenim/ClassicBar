package tfar.classicbar.impl.overlays.mod;


import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

public class StaminaB extends BarOverlayImpl {

    public static final String name = "parcool:stamina";

    public StaminaB() {
        super(name);
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!checkConfigs()) return false;
        IStamina stamina = IStamina.get(player);
        return stamina.getMaxStamina() > stamina.get();
    }

    public static boolean checkConfigs() {
        return ParCoolConfig.Client.StaminaHUDType.get() == HUDType.Light;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double barWidth = getBarWidth(player);
        Color.reset();
        //Bar background
        renderFullBarBackground(graphics, xStart, yStart);
        //draw portion of bar based on air amount
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        Color color = getPrimaryBarColor(0, player);
        color.color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        IStamina stamina = IStamina.get(player);
        int cStamina = stamina.get();
        int maxStamina = stamina.getMaxStamina();
        return Math.ceil((double) BarOverlayImpl.WIDTH * cStamina / maxStamina);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.YELLOW;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        //draw stamina amount
        int stamina = IStamina.get(player).get();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        Color color = getPrimaryBarColor(0, player);
        textHelper(graphics, xStart, yStart, stamina/20, color.colorToText());
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw stamina icon
        IStamina stamina = IStamina.get(player);
        int textureX = stamina.isExhausted() ? 16 : 0;
        graphics.blit(getIconRL(),xStart, yStart, textureX, 119, 8, 9, 128, 128);
    }
}
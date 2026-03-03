package tfar.classicbar.impl.overlays.mod;

import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

// Changed: 1.21 NeoForge build uses data attachments (ModAttachment.PLAYER_THIRST) instead
// of the Forge capability system (ModCapabilities) present in 1.18-1.20 branches.
public class ThirstWasTaken extends BarOverlayImpl {

    public static final String NAME = "thirst_was_taken";
    public static final double MAX_THIRST = 20.0;

    public ThirstWasTaken() {
        super(NAME);
    }

    @Override
    public boolean shouldRender(Player player) {
        return ModCompat.thirstWasTaken.loaded;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);
        if (thirstData == null) return;

        int thirstLevel = thirstData.getThirst();
        int quenchedLevel = thirstData.getQuenched();

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        drawThirst(graphics, xStart, yStart, thirstLevel);

        if (quenchedLevel > 0) {
            drawQuenched(graphics, xStart, yStart, quenchedLevel);
        }
    }

    private void drawThirst(GuiGraphics graphics, int x, int y, int thirstLevel) {
        getSecondaryBarColor(0, null).color2Gl();
        double barWidth = ModUtils.getWidth(thirstLevel, MAX_THIRST);
        double barXStart = x + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(graphics, barXStart + 2, y + 2, barWidth);
    }

    private void drawQuenched(GuiGraphics graphics, int x, int y, int quenchedLevel) {
        getPrimaryBarColor(0, null).color2Gl();
        double barWidth = ModUtils.getWidth(quenchedLevel, MAX_THIRST);
        double barXStart = x + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(graphics, barXStart + 2, y + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);
        if (thirstData == null) return 0;
        return Math.ceil(BarOverlayImpl.WIDTH * thirstData.getThirst() / MAX_THIRST);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);
        if (thirstData == null) return;
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int c = getSecondaryBarColor(0, player).colorToText();
        textHelper(graphics, xStart, yStart, thirstData.getThirst(), c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // Icon texture is 25x9 pixels with 3 droplet states; filled droplet is at x=16
        // Use direct blit with correct texture dimensions (25x9) instead of ModUtils which assumes 256x256
        graphics.blit(getIconRL(), xStart, yStart, 16, 0, 9, 9, 25, 9);
    }

    /** quenched (saturation equivalent) */
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.thirstWasTakenQuenched;
    }

    /** thirst */
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.thirstWasTaken;
    }
}

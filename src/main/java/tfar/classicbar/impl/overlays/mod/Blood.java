package tfar.classicbar.impl.overlays.mod;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

// Changed: removed VAMPIRISM_ICONS ResourceLocation constant (moved to ModUtils.VAMPIRISM_ICONS),
// removed shouldRenderText() override (was ClassicBarsConfig.showHungerNumbers.get()),
// and removed getIconRL() override (was returning VAMPIRISM_ICONS).
// ClassicBarsConfig import removed. ResourceLocation import remains for blitSprite sprite constants.
// Icon is now defaulted to ModUtils.VAMPIRISM_ICONS via ClassicBarsConfig.makeDefaultBarSettings().
public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }

    @Override
    public boolean shouldRender(Player player) {
        return VampirismAPI.factionPlayerHandler(player).isInFaction(VReference.VAMPIRE_FACTION);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.RED;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        if (!shouldRender(player)) return;
        IVampirePlayer vampirePlayer = VampirismAPI.vampirePlayer(player);
        if (vampirePlayer == null) return;
        IBloodStats stats = vampirePlayer.getBloodStats();
        if (stats == null) return;

        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        getPrimaryBarColor(0, player).color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        IVampirePlayer vampirePlayer = VampirismAPI.vampirePlayer(player);
        if (vampirePlayer == null) return 0;
        IBloodStats stats = vampirePlayer.getBloodStats();
        if (stats == null) return 0;
        int blood = stats.getBloodLevel();
        int maxBlood = stats.getMaxBlood();
        return Math.ceil((double) WIDTH * blood / maxBlood);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        IVampirePlayer vampirePlayer = VampirismAPI.vampirePlayer(player);
        if (vampirePlayer == null) return;
        IBloodStats stats = vampirePlayer.getBloodStats();
        if (stats == null) return;
        int blood = stats.getBloodLevel();
        int c = getPrimaryBarColor(0, player).colorToText();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics, xStart, yStart, blood, c);
    }

    private static final ResourceLocation BLOOD_BACKGROUND = ResourceLocation.fromNamespaceAndPath("vampirism", "blood_bar/background");
    private static final ResourceLocation BLOOD_FULL = ResourceLocation.fromNamespaceAndPath("vampirism", "blood_bar/full");

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        graphics.blitSprite(BLOOD_BACKGROUND, xStart, yStart, 9, 9);
        graphics.blitSprite(BLOOD_FULL, xStart, yStart, 9, 9);
    }
}

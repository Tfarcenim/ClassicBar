package tfar.classicbar.impl.overlays.mod;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

import static tfar.classicbar.util.ModUtils.drawTexturedModalRect;

public class Blood extends BarOverlayImpl {

    public Blood() {
        super("blood");
    }
    @Override
    public boolean shouldRender(Player player) {
        return VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION;
    }
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.RED;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            double barWidth = getBarWidth(player);
            int xStart = screenWidth / 2 + getHOffset();
            int yStart = screenHeight - vOffset;
            Color.reset();
            //Bar background
            renderFullBarBackground(graphics,xStart,yStart);
            //draw portion of bar based on blood amount
            double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
            getPrimaryBarColor(0, player).color2Gl();
            renderPartialBar(graphics, f + 2, yStart + 2,barWidth);
        });
    }

    @Override
    public double getBarWidth(Player player) {
        IBloodStats stats = VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).orElse(null);
        if (stats != null) {
            int blood = stats.getBloodLevel();
            int maxBlood = stats.getMaxBlood();
            return Math.ceil((double) WIDTH * blood / maxBlood);
        }
        return 0;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        //draw blood amount
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            int blood = stats.getBloodLevel();
            int c = getPrimaryBarColor(0, player).colorToText();
            int xStart = width / 2 + getIconOffset();
            int yStart = height - vOffset;
            textHelper(graphics, xStart, yStart, blood, c);
        });
    }
    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw blood icon
        drawTexturedModalRect(graphics, xStart, yStart, 0, 0, 9, 9);
        drawTexturedModalRect(graphics, xStart, yStart, 9, 0, 9, 9);
    }
}

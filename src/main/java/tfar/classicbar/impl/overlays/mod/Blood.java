package tfar.classicbar.impl.overlays.mod;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

import static tfar.classicbar.util.ModUtils.drawTexturedModalRect;
import static tfar.classicbar.util.ModUtils.getWidth;

public class Blood extends BarOverlayImpl {

    private static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");
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
    public void renderBar(ForgeGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            double barWidth = getBarWidth(player);
            int xStart = screenWidth / 2 + getHOffset();
            int yStart = screenHeight - vOffset;
            Color.reset();
            //Bar background
            renderFullBarBackground(stack,xStart,yStart);
            //draw portion of bar based on blood amount
            double f = xStart + (rightHandSide() ? BarOverlay.WIDTH - barWidth : 0);
            getPrimaryBarColor(0, player).color2Gl();
            renderPartialBar(stack, f + 2, yStart + 2,barWidth);
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
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showHungerNumbers.get();
    }

    @Override
    public void renderText(PoseStack stack, Player player, int width, int height, int vOffset) {
        //draw blood amount
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            int blood = stats.getBloodLevel();
            int c = getPrimaryBarColor(0, player).colorToText();
            int xStart = width / 2 + getIconOffset();
            int yStart = height - vOffset;
            textHelper(stack, xStart, yStart, blood, c);
        });
    }
    @Override
    public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw blood icon
        drawTexturedModalRect(stack, xStart, yStart, 0, 0, 9, 9);
        drawTexturedModalRect(stack, xStart, yStart, 9, 0, 9, 9);
    }
    @Override
    public ResourceLocation getIconRL() {
        return VAMPIRISM_ICONS;
    }

}

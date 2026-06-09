package tfar.classicbar.impl.overlays.mod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Blood extends BarOverlayImpl {

    public static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");

    public Blood(BarSettings settings) {
        super("blood",settings, ModCompat.vampirism.name(),Blood::getBloodRatio);
    }

    public static final Codec<Blood> CODEC = RecordCodecBuilder.create(
            objectInstance -> objectInstance.group(BarSettings.CODEC.fieldOf("bar_settings")
                    .forGetter(Blood::getBarSettings)
            ).apply(objectInstance,Blood::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> getCodec() {
        return CODEC;
    }

    @Override
    public boolean shouldRender(Player player) {
        boolean b = super.shouldRender(player);
        return b && VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION;
    }
    public Color getPrimaryBarColor() {
        return Color.RED;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            double barWidth = getBarWidth(player);
            int xStart = screenWidth / 2 + getHOffset();
            int yStart = screenHeight - vOffset;
            //Bar background
            renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);
            //draw portion of bar based on blood amount
            double f = xStart + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
            renderPartialBar(getPrimaryBarColor(),graphics, f + 2, yStart + 2,barWidth);
        });
    }


    public static float getBloodRatio(Player player) {
        IBloodStats stats = VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).orElse(null);
        if (stats != null) {
            int blood = stats.getBloodLevel();
            int maxBlood = stats.getMaxBlood();
            return (float) blood / maxBlood;
        }
        return 0;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        //draw blood amount
        VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
            int blood = stats.getBloodLevel();
            int c = getPrimaryBarColor().colorToText();
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
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 0, 0, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 9, 0, 9, 9);
    }
}

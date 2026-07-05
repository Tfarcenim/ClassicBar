package tfar.classicbar.impl.overlays.templates;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.Color;
import tfar.classicbar.impl.BarInfo;

public class StackingBarOverlay extends BarOverlayImpl {

    private final Codec<? extends StackingBarOverlay> codec;

    public StackingBarOverlay(BarInfo info,
                              BarSettings barSettings, Codec<? extends StackingBarOverlay> codec) {
        super(info,barSettings);
        this.codec = codec;
    }

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return codec;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int vOffset) {
        //armor toughness stuff
        double value = barInfo.numerator().getValue(player);
        int barWidth = getBarWidth(player);
        int xStart = getXStartBar(graphics.guiWidth(),barWidth);
        int yStart = graphics.guiHeight() - vOffset;
        int index = getStackCount(player);
        Color primary = getBarColor(player,index);
        //draw bar background portion
        renderBarBackground(graphics, player, vOffset);
        if (index == 0) {
            //draw portion of bar based on armor toughness amount
            renderPartialBar(primary,graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //if we are out of colors wrap the bar
            if (value % 20 != 0) {
                Color secondary = getBarColor(player,index - 1);
                //draw complete first bar
                renderFullBar(secondary, graphics, xStart + 2, yStart + 2);
                //draw partial second bar

                int w = BarOverlayImpl.getWidth(value % 20, 20);
                 xStart = getXStartBar(graphics.guiWidth(),w);
                renderPartialBar(primary,graphics, xStart + 2, yStart + 2, w);
            } else { //case 2, bar is a multiple of 20, or it is capped
                //draw complete second bar
                renderFullBar(primary, graphics, xStart + 2, yStart + 2);
            }
        }
    }

    public int getStackCount(Player player) {
        float ratio = barInfo.getUnclampedRatio(player);
        return ratio > 1 ? (int) (Math.ceil(ratio) - 1) : 0;
    }

    public Color getBarColor(Player player,int index) {
        return getBarSettings().colorProvider().getColor(player,barInfo.getUnclampedRatio(player),index);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int vOffset) {
        int xStart = graphics.guiWidth() / 2 + getIconOffset();
        int yStart = graphics.guiHeight() - vOffset;
        double value = barInfo.numerator().getValue(player);
        int index = getStackCount(player);
        int c = getBarColor(player,index).colorToText();
        //draw amount
        textHelper(graphics, xStart, yStart, value, c);
    }
}

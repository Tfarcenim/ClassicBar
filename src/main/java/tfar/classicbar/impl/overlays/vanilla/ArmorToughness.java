package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class ArmorToughness extends BarOverlayImpl {

    public ArmorToughness() {
        super("armor_toughness");
    }

    @Override
    public boolean shouldRender(Player player) {
        return ClassicBarsConfig.displayToughnessBar.get() && player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue() >= 1;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        //armor toughness stuff
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (rightHandSide()) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;
        int index = (int) Math.min(Math.ceil(armorToughness / 20), ConfigCache.armor_toughness.size()) - 1;
        Color primary = getPrimaryBarColor(index, player);
        //draw bar background portion
        Color.reset();
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {
            primary.color2Gl();
            //draw portion of bar based on armor toughness amount
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            int size = ConfigCache.armor_toughness.size();
            //if we are out of colors wrap the bar
            if (index < size && armorToughness % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1, player);
                //draw complete first bar
                secondary.color2Gl();
                renderFullBar(graphics, xStart + 2, yStart + 2);
                //draw partial second bar

                double w = ModUtils.getWidth(armorToughness % 20, 20);

                primary.color2Gl();
                double f = xStart + (rightHandSide() ? WIDTH - w : 0);
                renderPartialBar(graphics, f + 2, yStart + 2, w);
            } else { //case 2, bar is a multiple of 20, or it is capped
                //draw complete second bar
                primary.color2Gl();
                renderFullBar(graphics, xStart + 2, yStart + 2);
            }
        }
    }

    public double getBarWidth(Player player) {
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        return Math.ceil(WIDTH * Math.min(20, armorToughness) / 20);//armor toughness can go above 20 in modded contexts!
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullToughnessBar.get();
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.armor_toughness.get(index);
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.armor_toughness.get(index);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ConfigCache.armor_toughness.size() - 1);
        int c = getPrimaryBarColor(index, player).colorToText();
        //draw armor toughness amount
        textHelper(graphics, xStart, yStart, armorToughness, c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw armor toughness icon
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 83, 0, 9, 9);
    }
}

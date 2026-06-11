package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.joml.Vector2i;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.BarType;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.api.Color;
import tfar.classicbar.impl.IconData;
import tfar.classicbar.util.ModUtils;

import java.util.List;

public class ArmorToughness extends BarOverlayImpl {

    public static final BarInfo INFO = BarInfo.getBuilder("armor_toughness")
            .setShouldRender(player -> player.getAttributeValue(Attributes.ARMOR_TOUGHNESS) >= 1)
            .setNumerator(player -> (float) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS))
            .setDenominator(fixed(20f))
            .setIconData(new IconData(List.of(new Vector2i(83,0))))
            .build();

    public ArmorToughness(BarSettings barSettings) {
        super(INFO,barSettings);
    }

    public static final Codec<ArmorToughness> CODEC = RecordCodecBuilder.create(
            o -> codecStart(o).apply(o,ArmorToughness::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return CODEC;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        //armor toughness stuff
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        double barWidth = getBarWidth(player, 0);
        int xStart = screenWidth / 2 + getHOffset();
        if (getSide() == BarSide.RIGHT) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;
        int index = (int) Math.min(Math.ceil(armorToughness / 20), ConfigCache.armor_toughness.size()) - 1;
        Color primary = getPrimaryBarColor(index);
        //draw bar background portion
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {
            //draw portion of bar based on armor toughness amount
            renderPartialBar(primary,graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            int size = ConfigCache.armor_toughness.size();
            //if we are out of colors wrap the bar
            if (index < size && armorToughness % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1);
                //draw complete first bar
                renderFullBar(secondary, graphics, xStart + 2, yStart + 2);
                //draw partial second bar

                double w = ModUtils.getWidth(armorToughness % 20, 20);

                double f = xStart + (getSide() == BarSide.RIGHT ? WIDTH - w : 0);
                renderPartialBar(primary,graphics, f + 2, yStart + 2, w);
            } else { //case 2, bar is a multiple of 20, or it is capped
                //draw complete second bar
                renderFullBar(primary, graphics, xStart + 2, yStart + 2);
            }
        }
    }

    public Color getPrimaryBarColor(int index) {
        return ConfigCache.armor_toughness.get(index);
    }

    public Color getSecondaryBarColor(int index) {
        return ConfigCache.armor_toughness.get(index);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ConfigCache.armor_toughness.size() - 1);
        int c = getPrimaryBarColor(index).colorToText();
        //draw armor toughness amount
        textHelper(graphics, xStart, yStart, armorToughness, c);
    }
}

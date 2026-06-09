package tfar.classicbar.impl.overlays;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

public abstract class OneColorBar extends BarOverlayImpl {
    private final Color color;

    protected static <T extends OneColorBar> Products.P2<RecordCodecBuilder.Mu<T>, BarSettings,Color> simpleCodecStart(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(BarSettings.CODEC.fieldOf("bar_settings").forGetter(BarOverlayImpl::getBarSettings),
                Color.HEX_CODEC.fieldOf("color").forGetter(OneColorBar::getColor)
        );
    }

    protected static <T extends OneColorBar> Products.P2<RecordCodecBuilder.Mu<T>, BarSettings,Color> altCodecStart(
            RecordCodecBuilder.Instance<T> instance) {
        return codecStart(instance).and(Color.HEX_CODEC.fieldOf("color").forGetter(OneColorBar::getColor));
    }

    protected OneColorBar(BarInfo barInfo, BarSettings barSettings, Color color) {
        super(barInfo, barSettings);
        this.color = color;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderSimpleBar(color, graphics, player, screenWidth, screenHeight, vOffset);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics,xStart,yStart,barInfo.numerator().getValue(player),color.colorToText());
    }

    public Color getColor() {
        return color;
    }
}

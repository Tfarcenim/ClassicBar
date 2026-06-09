package tfar.classicbar.impl.overlays;

import com.elenai.feathers.api.FeathersHelper;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

import java.util.Set;
import java.util.function.Function;

public abstract class OneColorBar extends BarOverlayImpl {
    private final Color color;

    public OneColorBar(String name, BarSettings barSettings, Function<Player, Float> widthGetter,Color color) {
        this(name, barSettings,Set.of(), widthGetter,color);
    }

    public OneColorBar(String name, BarSettings barSettings, String dependency, Function<Player, Float> widthGetter,Color color) {
        this(name, barSettings,Set.of(dependency), widthGetter,color);
    }

    public OneColorBar(String name, BarSettings barSettings, Set<String> dependencies, Function<Player, Float> widthGetter, Color color) {
        super(name, barSettings, dependencies, widthGetter);
        this.color = color;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderSimpleBar(color, graphics, player, screenWidth, screenHeight, vOffset);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        //draw feathers amount
        double feathers = FeathersHelper.getFeathers();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics,xStart,yStart,feathers,color.colorToText());
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

    }
}

package tfar.classicbar.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

import java.util.Set;
import java.util.function.Function;

public abstract class BarOverlayImpl implements BarOverlay {

    //maximum width the bar can be
    public static final int WIDTH = 77;
    public static final int HEIGHT = 5;
    public static final int BAR_U = 2;
    public static final int BAR_V = 11;
    public static final ResourceLocation BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");

    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    protected String name;
    private final BarSettings barSettings;
    protected final Set<String> dependencies;
    protected final boolean dependenciesMet;

    protected boolean errored;

    protected final Function<Player,Float> widthGetter;

    public BarOverlayImpl(String name, BarSettings barSettings, Function<Player, Float> widthGetter) {
        this(name,barSettings,Set.of(), widthGetter);
    }

    public BarOverlayImpl(String name, BarSettings barSettings, String dependency, Function<Player, Float> widthGetter) {
        this(name,barSettings,Set.of(dependency), widthGetter);
    }

    public BarOverlayImpl(String name, BarSettings barSettings, Set<String> dependencies, Function<Player, Float> widthGetter) {
        this.name = name;
        this.barSettings = barSettings;
        this.dependencies = dependencies;
        this.widthGetter = widthGetter;
        dependenciesMet = checkDependencies();
    }


    protected boolean checkDependencies() {
        return dependencies.isEmpty() || dependencies.stream().allMatch(s -> ModList.get().isLoaded(s));
    }

    public BarSettings getBarSettings() {
        return barSettings;
    }

    @MustBeInvokedByOverriders
    public boolean shouldRender(Player player) {
        return canRender();
    }

    public final boolean canRender() {
        return !errored() && barSettings.enabled() && dependenciesMet;
    }

    @Override
    public final BarSide getSide() {
        return barSettings.side();
    }

    @Override
    public boolean render(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        if (shouldRender(player)) {
            gui.setupOverlayRenderState(true, false);
            renderBar(gui, graphics, player, screenWidth, screenHeight, vOffset);
            Color.reset();//don't leak colors
            if (barSettings.show_text()) {
                renderText(graphics, player, screenWidth, screenHeight, vOffset);
            }
            if (barSettings.show_icon()) {
                renderIcon(graphics, player, screenWidth, screenHeight, vOffset);
            }
            return true;
        } return false;
    }

    public abstract void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

    protected boolean shouldFlash(Player player) {
        return false;
    }

    public abstract void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    public abstract void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    public int getHOffset() {

        return switch (getSide()){
            case LEFT -> -91;
            case RIGHT -> 10;
        };

    }

    public int getIconOffset() {
        return switch (getSide()) {
            case LEFT -> -101;
            case RIGHT -> 92;
        };
    }

    protected HealthEffect getHealthEffect(Player player) {
        HealthEffect effects = HealthEffect.NONE;//16
        if (player.hasEffect(MobEffects.POISON)) effects = HealthEffect.POISON;//evaluates to 52
        else if (player.hasEffect(MobEffects.WITHER)) effects = HealthEffect.WITHER;//evaluates to 88
        else if (player.isFullyFrozen()) effects = HealthEffect.FROZEN;
        return effects;
    }

    public void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && getSide() == BarSide.RIGHT) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            drawScaledBarBackground(graphics, barWidth, xStart, yStart + 1);
        } else renderFullBarBackground(graphics, xStart, yStart);
    }
    public void drawScaledBarBackground(GuiGraphics stack, double barWidth, int x, int y) {

        switch (getSide()) {
            case LEFT -> {
                ModUtils.drawTexturedModalRect(BAR,stack,x, y - 1, 0, 0, (int) (barWidth + 2), 9);
                ModUtils.drawTexturedModalRect(BAR,stack, (int) (x + barWidth + 2), y - 1, WIDTH + 2, 0, 2, 9);
            }
            case RIGHT -> {
                ModUtils.drawTexturedModalRect(BAR,stack,x, y - 1, 0, 0, barWidth + 2, 9);
                ModUtils.drawTexturedModalRect(BAR,stack,x + barWidth + 2, y-1, WIDTH + 2, 0, 2, 9);
            }
        }
    }
    public void textHelper(GuiGraphics graphics,int xStart,int yStart,double stat, int color) {
        int i1 = (int) Math.floor(stat);
        int i2 = barSettings.show_icon() ? 1 : 0;

        switch (getSide()) {
            case LEFT -> {
                int i3 = ModUtils.getStringLength(i1 + "");
                ModUtils.drawStringOnHUD(graphics, i1 + "", xStart - 9 * i2 - i3 + 5, yStart - 1, color);
            }
            case RIGHT -> {
                ModUtils.drawStringOnHUD(graphics, i1 + "", xStart + 9 * i2, yStart - 1, color);
            }
        }
    }

    public void renderFullBarBackground(GuiGraphics matrices, int xStart, int yStart) {
        ModUtils.drawTexturedModalRect(BAR,matrices, xStart, yStart, 0, 0, WIDTH + 4, 9);
    }
    public void renderFullBar(GuiGraphics matrices, int xStart, int yStart) {
        renderPartialBar(matrices,xStart,yStart,WIDTH);
    }
    public void renderPartialBar(GuiGraphics matrices, double xStart, int yStart,double barWidth) {
        ModUtils.drawTexturedModalRect(BAR,matrices, xStart, yStart, BAR_U, BAR_V, barWidth, HEIGHT);
    }
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    public ResourceLocation getIconRL() {
        return barSettings.icon();
    }

    public abstract Codec<? extends BarOverlayImpl> getCodec();

    public Set<String> dependencies() {
        return dependencies;
    }

    public final float getBarWidth(Player player) {
        return (float) Math.ceil(WIDTH* Mth.clamp(widthGetter.apply(player),0,1));
    }

    @Override
    public final boolean isFitted() {
        return barSettings.fitted();
    }
    @Override
    public final String name() {
        return name;
    }

    public final boolean errored() {
        return errored;
    }

    public void setErrored() {
        this.errored = true;
    }
}

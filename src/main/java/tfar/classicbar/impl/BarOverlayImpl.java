package tfar.classicbar.impl;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.Color;
import tfar.classicbar.api.colorprovider.BarLayer;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public abstract class BarOverlayImpl implements BarOverlay {

    //maximum width the bar can be
    public static final int WIDTH = 77;
    public static final int HEIGHT = 5;
    public static final int BAR_U = 2;
    public static final int BAR_V = 11;
    public static final ResourceLocation BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");

    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    private final BarSettings barSettings;

    protected final boolean dependenciesMet;
    protected boolean errored;

    protected final BarInfo barInfo;

    /**
     */
    protected static <T extends BarOverlayImpl> Products.P1<RecordCodecBuilder.Mu<T>, BarSettings> codecStart(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(BarSettings.CODEC.fieldOf("bar_settings").forGetter(BarOverlayImpl::getBarSettings));
    }


    protected BarOverlayImpl(BarInfo barInfo, BarSettings barSettings) {
        this.barInfo = barInfo;
        this.barSettings = barSettings;
        dependenciesMet = barInfo.checkDependencies();
    }

    public BarSettings getBarSettings() {
        return barSettings;
    }

    public final boolean shouldRender(Player player) {
        return canRender() && barInfo.shouldRender().test(player);
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

    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int text = (int)barInfo.numerator().getValue(player);
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        textHelper(graphics,xStart,yStart,text,barSettings.colorProvider().getColor(player, BarLayer.PRIMARY).colorToText());
    }

    public abstract void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset);


    public void renderSingleLayerIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw blood icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 0, 0, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 9, 0, 9, 9);
    }

    public void renderDoubleLayerIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw blood icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 0, 0, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 9, 0, 9, 9);
    }

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

    public static HealthEffect getHealthEffect(Player player) {
        HealthEffect effects = HealthEffect.NONE;//16
        if (player.hasEffect(MobEffects.POISON)) effects = HealthEffect.POISON;//evaluates to 52
        else if (player.hasEffect(MobEffects.WITHER)) effects = HealthEffect.WITHER;//evaluates to 88
        else if (player.isFullyFrozen()) effects = HealthEffect.FROZEN;
        return effects;
    }

    protected void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset,false);
    }

    public void renderFlashBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset,true);
    }

    protected void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset,boolean flash) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && getSide() == BarSide.RIGHT) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            drawScaledBarBackground(graphics, barWidth, xStart, yStart + 1,flash);
        } else renderFullBarBackground(graphics, xStart, yStart,flash);
    }

    private void drawScaledBarBackground(GuiGraphics stack, double barWidth, int x, int y, boolean flash) {
        switch (getSide()) {
            case LEFT -> {
                ModUtils.drawTexturedModalRect(BAR,stack,x, y - 1, 0, flash ? 18 : 0, (int) (barWidth + 2), 9);
                ModUtils.drawTexturedModalRect(BAR,stack, (int) (x + barWidth + 2), y - 1, WIDTH + 2, flash ? 18 : 0, 2, 9);
            }
            case RIGHT -> {
                ModUtils.drawTexturedModalRect(BAR,stack,x, y - 1, 0, flash ? 18 : 0, barWidth + 2, 9);
                ModUtils.drawTexturedModalRect(BAR,stack,x + barWidth + 2, y-1, WIDTH + 2, flash ? 18 : 0, 2, 9);
            }
        }
    }
    public void textHelper(GuiGraphics graphics,int xStart,int yStart,double stat, int color) {
        int i1 = (int) Math.floor(stat);
        int i2 = barSettings.show_icon() ? 1 : 0;

        switch (getSide()) {
            case LEFT -> {
                int i3 = Minecraft.getInstance().font.width(i1 + "");
                ModUtils.drawStringOnHUD(graphics, i1 + "", xStart - 9 * i2 - i3 + 5, yStart - 1, color);
            }
            case RIGHT -> {
                ModUtils.drawStringOnHUD(graphics, i1 + "", xStart + 9 * i2, yStart - 1, color);
            }
        }
    }

    protected int getXStartBar(int screenWidth,int barWidth){
        int xStart = screenWidth / 2 + getHOffset();
        if (getSide() == BarSide.RIGHT) xStart += (WIDTH - barWidth);
        return xStart;
    }

    private void renderFullBarBackground(GuiGraphics matrices, int xStart, int yStart, boolean flash) {
        renderFullBarBackground(matrices,xStart,yStart,flash
        ? 18 : 0);
    }

    public void renderFullBarBackground(GuiGraphics matrices, int xStart, int yStart,int vOffset) {
        ModUtils.drawTexturedModalRect(BAR,matrices, xStart, yStart, 0, vOffset, WIDTH + 4, 9);
    }

    public void renderFullBar(Color color,GuiGraphics matrices, int xStart, int yStart) {
        renderPartialBar(color,matrices,xStart,yStart,WIDTH);
    }

    protected void renderSimpleBar(Color color, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        renderSimpleBar(color,graphics,player,screenWidth,screenHeight,vOffset,false);
    }

    protected void renderSimpleBar(Color color, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset,boolean highlight) {
        int barWidth = getBarWidth(player);
        int xStart = getXStartBar(screenWidth,barWidth);
        int yStart = screenHeight - vOffset;

        //Bar background
        renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset,highlight);
        //draw portion of bar based on feathers amount
        renderPartialBar(color,graphics,xStart+2,yStart+2,barWidth);
    }

    public void renderPartialBar(Color color,GuiGraphics matrices, double xStart, int yStart,double barWidth) {
        color.color2Gl();
        ModUtils.drawTexturedModalRect(BAR,matrices, xStart, yStart, BAR_U, BAR_V, barWidth, HEIGHT);
    }

    public int getLayers() {
        return 1;
    }

    public ResourceLocation getIconRL() {
        return barSettings.icon();
    }

    public final int getBarWidth(Player player) {
        return (int) Math.ceil(WIDTH* barInfo.getRatio(player));
    }

    @FunctionalInterface
    protected interface Numerator {
        float getValue(Player player);
    }

    @FunctionalInterface
    protected interface Denominator {
        float getValue(Player player);
    }

    protected static Denominator fixed(float value) {
        return p -> value;
    }

    public final boolean isFitted() {
        return barSettings.fitted();
    }
    @Override
    public final String name() {
        return barInfo.name();
    }

    public final boolean errored() {
        return errored;
    }

    public void setErrored() {
        this.errored = true;
    }
}

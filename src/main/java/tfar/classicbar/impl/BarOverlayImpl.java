package tfar.classicbar.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import tfar.classicbar.util.ModUtils;

public abstract class BarOverlayImpl implements BarOverlay {

    //maximum width the bar can be
    public static final int WIDTH = 77;
    public static final int HEIGHT = 5;
    public static final int BAR_U = 2;
    public static final int BAR_V = 11;
    public static final ResourceLocation ICON_BAR = ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "textures/gui/health.png");

    public static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.parse("textures/gui/icons.png");
    protected String name;
    protected boolean side;
    protected BarSettings barSettings; // Changed: added to hold per-bar JSON config (show_text, icon)

    public BarOverlayImpl(String name) {
        this.name = name;
    }

    public boolean shouldRender(Player player) {
        return true;
    }

    // Changed: new method implementing BarOverlay.setBarSettings(); called by ClassicBarsConfig
    // when reading per-bar JSON files so each overlay receives its own settings instance.
    @Override
    public void setBarSettings(BarSettings barSettings) {
        this.barSettings = barSettings;
    }

    @Override
    public final boolean rightHandSide() {
        return side;
    }

    @Override
    public final BarOverlay setSide(boolean right) {
        side = right;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        if (barSettings == null || !shouldRender(player)) return;
        Gui gui = Minecraft.getInstance().gui;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bindBarTexture();
        renderBar(gui, graphics, player, screenWidth, screenHeight, vOffset);
        Color.reset(); // reset shader color after renderBar so text is drawn unaffected
        if (shouldRenderText()) {
            // Changed: removed Color.reset() that was here before renderText; the reset above is sufficient.
            // Having it here was redundant and called reset twice when text was enabled.
            renderText(graphics, player, screenWidth, screenHeight, vOffset);
        }
        if (ConfigCache.icons) {
            bindIconTexture();
            renderIcon(graphics, player, screenWidth, screenHeight, vOffset);
        }
        Color.reset();
        RenderSystem.disableBlend();
        EventHandler.increment(gui, rightHandSide(), 10);
    }

    public abstract void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset);

    protected boolean shouldFlash(Player player) {
        return false;
    }

    // Changed: was "public boolean shouldRenderText() { return true; }" - always true, non-final.
    // Individual overlays (Health, Hunger, Air, Armor, etc.) each overrode this to read their
    // own ClassicBarsConfig.showXNumbers flag. Now final and reads from barSettings.show_text,
    // which is set from the per-bar JSON config, removing the override in each overlay class.
    public final boolean shouldRenderText() {
        return barSettings.show_text;
    }

    public abstract void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    public abstract void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset);

    public int getHOffset() {
        return rightHandSide() ? 10 : -91;
    }

    public int getIconOffset() {
        return rightHandSide() ? 92 : -101;
    }

    protected HealthEffect getHealthEffect(Player player) {
        HealthEffect effects = HealthEffect.NONE;//16 - the effect.i value for texture offset reference
        if (player.hasEffect(MobEffects.POISON)) effects = HealthEffect.POISON;//evaluates to 52
        else if (player.hasEffect(MobEffects.WITHER)) effects = HealthEffect.WITHER;//evaluates to 88
        else if (player.isFullyFrozen()) effects = HealthEffect.FROZEN; // added: frozen heart support
        return effects;
    }

    public void renderBarBackground(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && rightHandSide()) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            drawScaledBarBackground(graphics, barWidth, xStart, yStart + 1);
        } else renderFullBarBackground(graphics, xStart, yStart);
    }
    public void drawScaledBarBackground(GuiGraphics stack, double barWidth, int x, int y) {
        if (rightHandSide()) {
            ModUtils.drawTexturedModalRect(stack,x, y - 1, 0, 0, barWidth + 2, 9);
            ModUtils.drawTexturedModalRect(stack,x + barWidth + 2, y-1, WIDTH + 2, 0, 2, 9);
        } else {
            ModUtils.drawTexturedModalRect(stack,x, y - 1, 0, 0, (int) (barWidth + 2), 9);
            ModUtils.drawTexturedModalRect(stack, (int) (x + barWidth + 2), y - 1, WIDTH + 2, 0, 2, 9);
        }
    }
    public void textHelper(GuiGraphics graphics,int xStart,int yStart,double stat, int color) {
        int i1 = (int) Math.floor(stat);
        int i2 = ConfigCache.icons ? 1 : 0;

        if (rightHandSide()) {
            ModUtils.drawStringOnHUD(graphics, i1 + "", xStart + 9 * i2, yStart - 1, color);
        } else {
            int i3 = ModUtils.getStringLength(i1 + "");
            ModUtils.drawStringOnHUD(graphics, i1 + "", xStart - 9 * i2 - i3 + 5, yStart - 1, color);
        }
    }
    public void renderFullBarBackground(GuiGraphics matrices, int xStart, int yStart) {
        ModUtils.drawTexturedModalRect(matrices, xStart, yStart, 0, 0, WIDTH + 4, 9);
    }
    public void renderFullBar(GuiGraphics matrices, int xStart, int yStart) {
        renderPartialBar(matrices,xStart,yStart,WIDTH);
    }
    public void renderPartialBar(GuiGraphics matrices, double xStart, int yStart,double barWidth) {
        ModUtils.drawTexturedModalRect(matrices, xStart, yStart, BAR_U, BAR_V, barWidth, HEIGHT);
    }
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return Color.BLACK;
    }
    // Changed: was "public ResourceLocation getIconRL() { return GUI_ICONS_LOCATION; }" - non-final.
    // Individual overlays overrode this to return their mod-specific texture (e.g. vampirism icons,
    // parcool stamina bar). Now final and reads from barSettings.icon set via JSON default in
    // ClassicBarsConfig.makeDefaultBarSettings(), removing the override in each overlay class.
    @Override
    public final ResourceLocation getIconRL() {
        return barSettings.icon;
    }
    @Override
    public boolean isFitted() {
        return false;
    }
    @Override
    public final String name() {
        return name;
    }
}

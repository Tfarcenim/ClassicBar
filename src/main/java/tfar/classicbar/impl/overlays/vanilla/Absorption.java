package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

// Changed: removed shouldRenderText() override (was ClassicBarsConfig.showAbsorptionNumbers.get())
// and removed getIconRL() override (was returning GUI_ICONS_LOCATION directly).
// Both are now handled via barSettings: show_text and icon from per-bar JSON.
// renderBar parameter changed from ForgeGui to Gui; renderIcon rewritten to use blitSprite.
public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getMaxHealth();

        if (rightHandSide()) {
            xStart += BarOverlayImpl.WIDTH - barWidth;
        }

        //draw absorption bar
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color primary = getPrimaryBarColor(index, player);
        Color.reset();
        //draw background bar
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {//no wrapping
            //background
            primary.color2Gl();
            //bar
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //draw first full bar
            Color secondary = getSecondaryBarColor(index - 1, player);
            secondary.color2Gl();
            renderFullBar(graphics, xStart + 2, yStart + 2);
            //is it on the edge or capped already?
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                //draw second partial bar
                primary.color2Gl();
                renderPartialBar(graphics, xStart + 2, yStart + 2, ModUtils.getWidth(absorb % maxHealth, maxHealth));
            }
        }
    }

    @Override
    public double getBarWidth(Player player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        return Math.ceil(BarOverlayImpl.WIDTH * Math.min(maxHealth, absorb) / maxHealth); // Aligned: removed unnecessary (int) cast; return type is double, matches all other overlays
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        HealthEffect effect = getHealthEffect(player);
        // §1: switch expression over switch statement; FROZEN uses same colors as NONE
        return switch (effect) {
            case NONE, FROZEN -> ConfigCache.absorption.get(index);
            case POISON -> ConfigCache.absorptionPoison.get(index);
            case WITHER -> ConfigCache.absorptionWither.get(index);
        };
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar.get();
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // handle the text
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color c = getPrimaryBarColor(index, player);
        textHelper(graphics, xStart, yStart, absorb, c.colorToText());
    }

    private static final ResourceLocation HEART_CONTAINER = ResourceLocation.withDefaultNamespace("hud/heart/container");
    private static final ResourceLocation HEART_CONTAINER_HARDCORE = ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore");
    private static final ResourceLocation HEART_ABSORBING_FULL = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full");
    private static final ResourceLocation HEART_ABSORBING_HARDCORE_FULL = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full");

    // Changed: rewritten to use blitSprite with named sprite ResourceLocations instead of
    // drawTexturedModalRect with raw atlas offsets (was (16,0) for container, (160,0) for fill).
    // Added hardcore variant support — was missing in the old atlas-offset approach.
    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        boolean hardcore = player.level().getLevelData().isHardcore();
        //draw absorption icon
        graphics.blitSprite(hardcore ? HEART_CONTAINER_HARDCORE : HEART_CONTAINER, xStart, yStart, 9, 9);
        graphics.blitSprite(hardcore ? HEART_ABSORBING_HARDCORE_FULL : HEART_ABSORBING_FULL, xStart, yStart, 9, 9);
    }
}
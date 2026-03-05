package tfar.classicbar.impl.overlays.mod;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

/**
 * ClassicBar-style mana bar for Iron's Spells 'n Spellbooks.
 * Replaces the mod's built-in {@code ManaBarOverlay} with a horizontal bar
 * that matches the look and feel of every other ClassicBar overlay.
 * <p>
 * Current mana is read from {@link ClientMagicData#getPlayerMana()} (client-side cache).
 * Max mana comes from the {@link AttributeRegistry#MAX_MANA} player attribute (default 100).
 */
public class IronsMana extends BarOverlayImpl {

    public static final String NAME = "irons_mana";

    public IronsMana() {
        super(NAME);
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!ModCompat.ironsSpellbooks.loaded) return false;
        // Show the bar whenever the player's mana is below max (matches the mod's "Contextual" default)
        int mana = ClientMagicData.getPlayerMana();
        double maxMana = player.getAttributeValue(AttributeRegistry.MAX_MANA);
        return maxMana > 0 && mana < maxMana;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double barWidth = getBarWidth(player);

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        // Draw mana fill
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        getPrimaryBarColor(0, player).color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        int mana = ClientMagicData.getPlayerMana();
        double maxMana = player.getAttributeValue(AttributeRegistry.MAX_MANA);
        if (maxMana <= 0) return 0;
        return Math.ceil(BarOverlayImpl.WIDTH * Math.min(mana, maxMana) / maxMana);
    }

    /** Mana bar color — configurable via ClassicBarsConfig */
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.ironsMana;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int mana = ClientMagicData.getPlayerMana();
        int c = getPrimaryBarColor(0, player).colorToText();
        textHelper(graphics, xStart, yStart, mana, c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // Iron's Spells icons.png: mana icon at (78, 18), 9×9 pixels, sheet is 256×256
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 78, 18, 9, 9);
    }
}

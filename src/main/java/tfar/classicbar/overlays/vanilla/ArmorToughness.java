package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarOverlayImpl;
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
    public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
        //armor toughness stuff
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (rightHandSide()) {
            xStart += ModUtils.WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;
        int index = (int) Math.min(Math.ceil(armorToughness / 20), ConfigCache.armor_toughness.size()) - 1;
        Color primary = getPrimaryBarColor(index, player);
        //draw bar background portion
        Color.reset();
        renderBarBackground(stack, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {
            primary.color2Gl();
            //draw portion of bar based on armor toughness amount
            renderMainBar(stack,xStart , yStart , barWidth);
        } else {
            //we have wrapped, draw 2 bars
            int size = ConfigCache.armor_toughness.size();
            //if we are out of colors wrap the bar
            if (index < size && armorToughness % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1, player);
                //draw complete first bar
                secondary.color2Gl();
                ModUtils.drawTexturedModalRect(stack, xStart, yStart + 1, 0, 10, 79, 7);
                //draw partial second bar
                primary.color2Gl();
                double f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(armorToughness % 20, 20) : 0);
                renderMainBar(stack, f, yStart, ModUtils.getWidth(armorToughness % 20, 20));
                ModUtils.drawTexturedModalRect(stack, f, yStart + 1, 0, 10, ModUtils.getWidth(armorToughness % 20, 20), 7);
            } else { //case 2, bar is a multiple of 20, or it is capped
                //draw complete second bar
                primary.color2Gl();
                ModUtils.drawTexturedModalRect(stack, xStart + 1, yStart + 1, 2, 10, 79, 7);
            }
        }
    }
    public double getBarWidth(Player player) {
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        return Math.ceil(ModUtils.WIDTH *armorToughness / 20);
    }
    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullToughnessBar.get();
    }
    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showArmorToughnessNumbers.get();
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
    public void renderText(PoseStack stack, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        double armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        int index = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ConfigCache.armor_toughness.size() - 1);
        int c = getPrimaryBarColor(index, player).colorToText();
        //draw armor toughness amount
        textHelper(stack, xStart, yStart, armorToughness, c);
    }
    @Override
    public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw armor toughness icon
        ModUtils.drawTexturedModalRect(stack, xStart, yStart, 83, 0, 9, 9);
    }
    @Override
    public ResourceLocation getIconRL() {
        return ModUtils.ICON_BAR;
    }
}

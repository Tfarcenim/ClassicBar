package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

import static tfar.classicbar.util.ModUtils.*;

public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        int barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();

        if (rightHandSide()) {
            xStart += ModUtils.WIDTH - barWidth;
        }

        //draw absorption bar
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color.reset();
        //no wrapping
        Color primary = getPrimaryBarColor(index, player);
        if (index == 0) {
            //background
            renderBarBackground(stack,player,screenWidth,screenHeight,vOffset);
            primary.color2Gl();
            //bar
            renderMainBar(stack, xStart, yStart, ModUtils.getWidth(absorb, maxHealth));
        } else {
            //draw background bar
            drawTexturedModalRect(stack, xStart, yStart, 0, 0, 81, 9);
            //we have wrapped, draw 2 bars
            //draw first full bar
            Color secondary = getSecondaryBarColor(index - 1, player);
            secondary.color2Gl();
            drawTexturedModalRect(stack, xStart + 1, yStart + 1, 1, 10, 79, 7);
            //is it on the edge or capped already?
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                //draw second partial bar
                primary.color2Gl();
                drawTexturedModalRect(stack, xStart + 1, yStart + 1, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
            }
        }
    }

    public int getBarWidth(Player player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();
        return (int) Math.ceil(ModUtils.WIDTH * Math.min(maxHealth, absorb) / maxHealth);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        HealthEffect effect = getHealthEffect(player);
        switch (effect) {
            case NONE -> {
                return ConfigCache.absorption.get(index);
            }
            case POISON -> {
                return ConfigCache.absorptionPoison.get(index);
            }
            case WITHER -> {
                return ConfigCache.absorptionWither.get(index);
            }
        }
        return super.getPrimaryBarColor(index, player);
    }
    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar.get();
    }
    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showHealthNumbers.get();
    }
    @Override
    public void renderText(PoseStack stack, Player player, int width, int height, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // handle the text
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color c = getPrimaryBarColor(index, player);
        textHelper(stack, xStart, yStart, absorb, c.colorToText());
    }

    @Override
    public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        int i5 = (player.level.getLevelData().isHardcore()) ? 5 : 0;
        //draw absorption icon
        drawTexturedModalRect(stack, xStart, yStart, 16, 9 * i5, 9, 9);
        drawTexturedModalRect(stack, xStart, yStart, 160, 0, 9, 9);
    }
}
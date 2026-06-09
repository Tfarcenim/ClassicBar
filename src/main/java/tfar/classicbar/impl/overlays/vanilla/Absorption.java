package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends BarOverlayImpl {

    public Absorption(BarSettings barSettings) {
        super("absorption",barSettings);
    }

    public static final Codec<Absorption> CODEC = RecordCodecBuilder.create(
            objectInstance -> objectInstance.group(BarSettings.CODEC.fieldOf("bar_settings")
                    .forGetter(Absorption::getBarSettings)
            ).apply(objectInstance,Absorption::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> getCodec() {
        return CODEC;
    }

    @Override
    public boolean shouldRender(Player player) {
        return super.shouldRender(player)&&player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getMaxHealth();

        if (getSide() == BarSide.RIGHT) {
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

    public double getBarWidth(Player player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        return (int) Math.ceil(BarOverlayImpl.WIDTH * Math.min(maxHealth, absorb) / maxHealth);
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

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        int i5 = (player.level().getLevelData().isHardcore()) ? 5 : 0;
        //draw absorption icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 16, 9 * i5, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 160, 0, 9, 9);
    }
}
package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.BarType;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.api.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends BarOverlayImpl {

    public static final BarInfo INFO = new BarInfo("absorption",
            player -> player.getAbsorptionAmount() > 0,Player::getAbsorptionAmount,LivingEntity::getMaxHealth, BarType.DUAL);

    public Absorption(BarSettings barSettings) {
        super(INFO,barSettings);
    }

    public static final Codec<Absorption> CODEC = RecordCodecBuilder.create(
            o -> codecStart(o).apply(o,Absorption::new));

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return CODEC;
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
        //draw background bar
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {//no wrapping
            //bar
            renderPartialBar(primary,graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //draw first full bar
            Color secondary = Color.BLACK;
            renderFullBar(secondary, graphics, xStart + 2, yStart + 2);
            //is it on the edge or capped already?
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                //draw second partial bar
                renderPartialBar(primary,graphics, xStart + 2, yStart + 2, ModUtils.getWidth(absorb % maxHealth, maxHealth));
            }
        }
    }

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
        return Color.BLACK;
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
package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.overlays.templates.StackingBarOverlay;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends StackingBarOverlay {

    public static final BarInfo INFO = BarInfo.getBuilder("absorption")
            .setShouldRender(player -> player.getAbsorptionAmount() > 0)
            .setNumerator(Player::getAbsorptionAmount).setDenominator(LivingEntity::getMaxHealth).build();

    public Absorption(BarSettings barSettings) {
        super(INFO,barSettings,CODEC);
    }

    public static final Codec<Absorption> CODEC = RecordCodecBuilder.create(
            o -> codecStart(o).apply(o,Absorption::new));

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        HealthEffect effect = getHealthEffect(player);
        int i5 = (player.level().getLevelData().isHardcore()) ? 5 : 0;
        //draw absorption icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 16, 9 * i5, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 160, 0, 9, 9);
    }
}
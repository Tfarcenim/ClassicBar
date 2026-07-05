package tfar.classicbar.impl.overlays.mod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.IconData;
import tfar.classicbar.impl.overlays.templates.FoodLikeBarOverlay;

import java.util.List;

/**
 * @see dev.ghen.thirst.foundation.gui.ThirstBarRenderer
 */
public class ThirstWasTakenThirst extends FoodLikeBarOverlay {

    public static final ResourceLocation OVERLAY_ID = ModCompat.thirst.id("thirst_level");
    public static final ResourceLocation THIRST_ICONS = ModCompat.thirst.id("textures/gui/thirst_icons.png");

    public static final BarInfo INFO = BarInfo.getBuilder("thirst_thirst")
            .requireDependency(ModCompat.thirst.name())
            .setShouldRender(player -> (!ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player)))
            .setNumerator(player -> player.getCapability(ModCapabilities.PLAYER_THIRST).map(IThirst::getThirst).orElse(0))
            .setIconData(new IconData(List.of(new Vector2i(16,0))))
            .build();

    public static final Codec<ThirstWasTakenThirst> CODEC = RecordCodecBuilder.create(
            o -> codecStartFoodLike(o).apply(o, ThirstWasTakenThirst::new));

    public ThirstWasTakenThirst(BarSettings barSettings, boolean showSaturation, boolean showExhaustion, boolean showPredictedHealing) {
        super(INFO, CODEC, () -> 4, p -> p.getCapability(ModCapabilities.PLAYER_THIRST)
                        .map(IThirst::getExhaustion).orElse(0f)
                , barSettings, showSaturation, showExhaustion, showPredictedHealing);
    }

    @Override
    public boolean isHealingItem(ItemStack stack, Player player) {
        boolean b = !stack.isEmpty() &&ThirstHelper.itemRestoresThirst(stack);
        return b;
    }

    @Override
    public float getSaturationValue(Player player) {
        return player.getCapability(ModCapabilities.PLAYER_THIRST).map(IThirst::getQuenched).orElse(0);
    }

    @Override
    public int getPotentialHealing(ItemStack stack, Player player) {
        return ThirstHelper.getThirst(stack);
    }

    @Override
    public float getPotentialSaturation(ItemStack stack, Player player) {
        boolean b = CommonConfig.EXTRA_HYDRATION_CONVERT_TO_QUENCHED.get();
        float quenched = ThirstHelper.getQuenched(stack);
        if (!b)return quenched;

        int itemThirst =  getPotentialHealing(stack,player);
        int playerThirst = (int) barInfo.numerator().getValue(player);

        int extra_quenched = Math.max(playerThirst + itemThirst - 20, 0);
        return quenched + extra_quenched;
    }

    @Override
    public float getPotentialSaturationMultiplier(ItemStack stack, Player player) {
        return 1;
    }

    @Override//uses custom sized texture
    public void renderIcon(GuiGraphics graphics, Player player, int vOffset) {
        int x = graphics.guiWidth() / 2 + getIconOffset();
        int y = graphics.guiHeight() - vOffset;
        graphics.blit(getIconRL(), x, y, 16.0F, 0.0F, 9, 9, 25, 9);

    }
}

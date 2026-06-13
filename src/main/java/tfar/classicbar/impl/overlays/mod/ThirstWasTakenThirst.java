package tfar.classicbar.impl.overlays.mod;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.overlays.templates.FoodLikeBarOverlay;

import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

public class ThirstWasTakenThirst extends FoodLikeBarOverlay {
    public static final ResourceLocation OVERLAY_ID = new ResourceLocation("thirst", "thirst_level");


    protected ThirstWasTakenThirst(BarInfo barInfo, Codec<? extends FoodLikeBarOverlay> codec, DoubleSupplier maxExhaustionGetter, ToDoubleFunction<Player> exhaustionGetter, BarSettings barSettings, boolean showSaturation, boolean showExhaustion, boolean showPredictedHealing) {
        super(barInfo, codec, maxExhaustionGetter, exhaustionGetter, barSettings, showSaturation, showExhaustion, showPredictedHealing);
    }

    @Override
    public boolean isHealingItem(ItemStack stack, Player player) {
        return false;
    }

    @Override
    public float getSaturationValue(Player player) {
        return 0;
    }

    @Override
    public int getPotentialHealing(ItemStack stack, Player player) {
        return 0;
    }

    @Override
    public float getPotentialSaturationMultiplier(ItemStack stack, Player player) {
        return 0;
    }
}

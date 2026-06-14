package tfar.classicbar.impl.overlays.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import sfiomn.legendarysurvivaloverhaul.api.data.json.JsonThirstConsumable;
import sfiomn.legendarysurvivaloverhaul.api.data.manager.ThirstDataManager;
import sfiomn.legendarysurvivaloverhaul.api.thirst.ThirstUtil;
import sfiomn.legendarysurvivaloverhaul.client.render.RenderThirstGui;
import sfiomn.legendarysurvivaloverhaul.registry.MobEffectRegistry;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.compat.VampirismHelper;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.IconData;
import tfar.classicbar.impl.overlays.templates.FoodLikeBarOverlay;

import java.util.List;

/**
 * @see sfiomn.legendarysurvivaloverhaul.client.render.RenderThirstGui
 */
public class LegendarySurvivalOverhaulThirst extends FoodLikeBarOverlay {

    public static final ResourceLocation OVERLAY_ID = ModCompat.legendarysurvivaloverhaul.id("thirst");
    public static final ResourceLocation ICONS = ModCompat.legendarysurvivaloverhaul.id("textures/gui/overlay.png");

    public static final BarInfo INFO = BarInfo.getBuilder("legendarysurvivaloverhaul_thirst")
            .requireDependency(ModCompat.legendarysurvivaloverhaul.name())
            .setShouldRender(player -> ThirstUtil.isThirstActive(player) && (!ModCompat.vampirism.loaded || !VampirismHelper.isVampire(player)))
            .setNumerator(player -> CapabilityUtil.getThirstCapability(player).getHydrationLevel())
            .build();

    public static final Codec<LegendarySurvivalOverhaulThirst> CODEC = RecordCodecBuilder.create(
            o -> codecStartFoodLike(o).apply(o, LegendarySurvivalOverhaulThirst::new));

    public LegendarySurvivalOverhaulThirst(BarSettings barSettings, boolean showSaturation, boolean showExhaustion, boolean showPredictedHealing) {
        super(INFO, CODEC,() -> 4,p ->CapabilityUtil.getThirstCapability(p).getThirstExhaustion(),barSettings,
                showSaturation, showExhaustion, showPredictedHealing);
    }

    @Override
    public boolean isHealingItem(ItemStack stack, Player player) {
        return ThirstDataManager.getConsumable(stack) != null;
    }

    @Override
    public float getSaturationValue(Player player) {
        return CapabilityUtil.getThirstCapability(player).getSaturationLevel();
    }

    @Override
    public int getPotentialHealing(ItemStack stack, Player player) {
        return ThirstDataManager.getConsumable(stack).hydration;
    }

    @Override
    public float getPotentialSaturation(ItemStack stack, Player player) {
        return ThirstDataManager.getConsumable(stack).saturation;
    }

    @Override
    public float getPotentialSaturationMultiplier(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        boolean hasThirstEffect = player.hasEffect(MobEffectRegistry.THIRST.get());
        boolean hasHeatThirstEffect = player.hasEffect(MobEffectRegistry.HEAT_THIRST.get());
       // JsonThirstConsumable jsonThirstConsumable =  ThirstDataManager.getConsumable(player.getMainHandItem());
       // boolean heldItemThirst = jsonThirstConsumable != null &&
        //        jsonThirstConsumable.effects.stream().anyMatch((jsonEffectParameter) -> jsonEffectParameter.name.equals("legendarysurvivaloverhaul:thirst"));
        RenderThirstGui.ThirstEffect thirstEffect = RenderThirstGui.ThirstEffect.getEffect(hasThirstEffect, hasHeatThirstEffect);
        //RenderThirstGui.ThirstEffect targetThirstEffect = RenderThirstGui.ThirstEffect.getEffect(hasThirstEffect || heldItemThirst, hasHeatThirstEffect);

        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        graphics.blit(ICONS, xStart, yStart, thirstEffect.getXTextureOffset(false, false), thirstEffect.getYTextureOffset(), 9, 9);

    }
}

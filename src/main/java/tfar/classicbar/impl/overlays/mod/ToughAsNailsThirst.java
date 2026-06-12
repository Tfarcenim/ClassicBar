package tfar.classicbar.impl.overlays.mod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.overlays.templates.FoodLikeBarOverlay;
import tfar.classicbar.util.ModUtils;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModConfig;
import toughasnails.init.ModTags;

public class ToughAsNailsThirst extends FoodLikeBarOverlay {

    public static final ResourceLocation OVERLAY_ID = new ResourceLocation("toughasnails", "thirst_level");

    public static final ResourceLocation OVERLAY = new ResourceLocation("toughasnails:textures/gui/icons.png");

    /**
     * Whether {@code drink} is tagged as {@link ModTags.Items#DRINKS} should be ensured via the context.
     */
    public static float getPotentialHydrationLevel(ItemStack drink) {
        if (drink.is(ModTags.Items.TEN_HYDRATION_DRINKS)) {
            return 0.1F;
        }

        if (drink.is(ModTags.Items.TWENTY_HYDRATION_DRINKS)) {
            return 0.2F;
        }

        if (drink.is(ModTags.Items.THIRTY_HYDRATION_DRINKS)) {
            return 0.3F;
        }

        if (drink.is(ModTags.Items.FOURTY_HYDRATION_DRINKS)) {
            return 0.4F;
        }

        if (drink.is(ModTags.Items.FIFTY_HYDRATION_DRINKS)) {
            return 0.5F;
        }

        if (drink.is(ModTags.Items.SIXTY_HYDRATION_DRINKS)) {
            return 0.6F;
        }

        if (drink.is(ModTags.Items.SEVENTY_HYDRATION_DRINKS)) {
            return 0.7F;
        }

        if (drink.is(ModTags.Items.EIGHTY_HYDRATION_DRINKS)) {
            return 0.8F;
        }

        if (drink.is(ModTags.Items.NINETY_HYDRATION_DRINKS)) {
            return 0.9F;
        }

        if (drink.is(ModTags.Items.ONE_HUNDRED_HYDRATION_DRINKS)) {
            return 1.0F;
        }
        return 0.0F;
    }

    /**
     * Whether {@code drink} is tagged as {@link ModTags.Items#DRINKS} should be ensured via the context.
     */
    public static float getPotentialPoisonChance(ItemStack drink) {
        if (drink.is(ModTags.Items.TWENTY_FIVE_POISON_CHANCE_DRINKS)) {
            return 0.25F;
        }

        if (drink.is(ModTags.Items.FIFTY_POISON_CHANCE_DRINKS)) {
            return 0.5F;
        }

        if (drink.is(ModTags.Items.SEVENTY_FIVE_POISON_CHANCE_DRINKS)) {
            return 0.75F;
        }

        if (drink.is(ModTags.Items.ONE_HUNDRED_POISON_CHANCE_DRINKS)) {
            return 1.0F;
        }
        
        return 0.0F;
    }


    public static final BarInfo INFO = BarInfo.getBuilder("toughasnails_thirst")
            .requireDependency(ModCompat.toughasnails.name())
            .setShouldRender(player -> isEnabled())
            .setNumerator(player -> (float)ThirstHelper.getThirst(player).getThirst()).build();

    public ToughAsNailsThirst(BarSettings settings, boolean showHydration, boolean showExhaustion,boolean showHeldDrink) {
        super(INFO, CODEC, () -> ModConfig.thirst.thirstExhaustionThreshold, p ->
                ThirstHelper.getThirst(p).getExhaustion(),settings, showHydration,showExhaustion,showHeldDrink);
    }

    public static final Codec<ToughAsNailsThirst> CODEC = RecordCodecBuilder.create(
            o -> codecStartFoodLike(o).apply(o, ToughAsNailsThirst::new));

    @Override
    public float getSaturationValue(Player player) {
        return ThirstHelper.getThirst(player).getHydration();
    }

    @Override
    public boolean isHealingItem(ItemStack stack, Player player) {
        return stack.is(ModTags.Items.DRINKS) && ThirstHelper.canDrink(player,true);
    }

    @Override
    public int getPotentialHealing(ItemStack stack, Player player) {
        return ModTags.Items.getThirstRestored(stack);
    }

    @Override
    public float getPotentialSaturationMultiplier(ItemStack stack, Player player) {
        return getPotentialHydrationLevel(stack);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        int texX = 36;
        int texBgX = 0;
        if (player.hasEffect(TANEffects.THIRST)) {
            texX += 36;  // i.e texX = 72
            texBgX = texX + 45; // i.e texBg += 117
        }

        // thirst background
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, texBgX, 32, 9, 9);
        // thirst
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, texX, 32, 9, 9);
    }

    public static boolean isEnabled() {
        return ThirstHelper.isThirstEnabled();
    }
}

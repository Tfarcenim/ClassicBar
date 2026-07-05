package tfar.classicbar.impl.overlays.templates;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.Color;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.network.PacketHandler;
import tfar.classicbar.util.ModUtils;
import toughasnails.thirst.ThirstData;

import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

public abstract class FoodLikeBarOverlay extends BarOverlayImpl {
    private final Codec<? extends FoodLikeBarOverlay> codec;

    private final DoubleSupplier maxExhaustionGetter;
    private final ToDoubleFunction<Player> exhaustionGetter;
    protected final boolean showSaturation;
    protected final boolean showExhaustion;
    protected final boolean showPredictedHealing;

    protected static <T extends FoodLikeBarOverlay> Products.P4<RecordCodecBuilder.Mu<T>, BarSettings, Boolean, Boolean, Boolean>
    codecStartFoodLike(
            RecordCodecBuilder.Instance<T> instance) {
        Products.P4<RecordCodecBuilder.Mu<T>, BarSettings, Boolean, Boolean, Boolean> product = codecStart(instance)
                .and(Codec.BOOL.fieldOf("show_saturation").forGetter(t -> t.showSaturation))
                .and(Codec.BOOL.fieldOf("show_exhaustion").forGetter(t -> t.showExhaustion))
                .and(Codec.BOOL.fieldOf("show_predicted_healing").forGetter(t -> t.showPredictedHealing));
        return product;
    }


    protected FoodLikeBarOverlay(BarInfo barInfo, Codec<? extends FoodLikeBarOverlay> codec, DoubleSupplier maxExhaustionGetter,
                                 ToDoubleFunction<Player> exhaustionGetter, BarSettings barSettings, boolean showSaturation, boolean showExhaustion, boolean showPredictedHealing) {
        super(barInfo, barSettings);
        this.codec = codec;
        this.maxExhaustionGetter = maxExhaustionGetter;
        this.exhaustionGetter = exhaustionGetter;
        this.showSaturation = showSaturation;
        this.showExhaustion = showExhaustion;
        this.showPredictedHealing = showPredictedHealing;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int vOffset) {

        float thirstLevel = barInfo.numerator().getValue(player);
        double hydrationLevel = getSaturationValue(player);

        int xStart = graphics.guiWidth() / 2 + getHOffset();
        int yStart = graphics.guiHeight() - vOffset;

        renderBarBackground(graphics, player,vOffset);

        drawThirst(graphics, player, xStart, yStart, thirstLevel, barInfo.denominator().getValue(player));

        if (hydrationLevel > 0 && showSaturation) {
            drawHydration(graphics, player, xStart, yStart, hydrationLevel, barInfo.denominator().getValue(player));
        }
    }

    @Override
    public void renderBarDecorations(ForgeGui gui, GuiGraphics graphics, Player player, int vOffset) {
        double maxExhaustionLevel = maxExhaustionGetter.getAsDouble();

        double exhaustionLevel = Math.min(exhaustionGetter.applyAsDouble(player), maxExhaustionLevel);
        int xStart = graphics.guiWidth() / 2 + getHOffset();
        int yStart = graphics.guiHeight() - vOffset;

        if (showPredictedHealing) {
            drawOverlayPrediction(graphics, player, xStart, yStart, ThirstData.DEFAULT_THIRST, 20);
        }

        if (showExhaustion && PacketHandler.presentOnServer) {
            drawExhaustion(graphics, player, xStart, yStart, exhaustionLevel, maxExhaustionLevel);
        }
    }

    protected void drawThirst(GuiGraphics stack, Player player, int x, int y, double thirstLevel, double maxLevel) {
        double barWidth = BarOverlayImpl.getWidth(thirstLevel, maxLevel);
        double barXStart = x + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(getPrimaryBarColor(player),stack, barXStart + 2, y + 2, barWidth);
    }

    protected void drawHydration(GuiGraphics stack, Player player, int x, int y, double hydrationLevel, double maxLevel) {
        double barWidth = BarOverlayImpl.getWidth(hydrationLevel, maxLevel);
        double barXStart = x + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(getSecondaryBarColor(player),stack, barXStart + 2, y + 2, barWidth);
    }

    protected void drawExhaustion(GuiGraphics stack, Player player, int x, int y, double exhaustionLevel, double maxLevel) {
        RenderSystem.setShaderColor(1, 1, 1, .25f);
        double barWidth = BarOverlayImpl.getWidth(exhaustionLevel, maxLevel);
        double barXStart = x + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
        ModUtils.drawTexturedModalRect(BAR,stack,barXStart + 2, y + 1, 1, 28, barWidth, 9);
    }

    public abstract boolean isHealingItem(ItemStack stack,Player player);

    protected void drawOverlayPrediction(GuiGraphics stack, Player player, int x, int y, double maxThirstLevel, double maxHydrationLevel) {
        ItemStack drink = player.getMainHandItem();
        if (!isHealingItem(drink, player))return;
        double time = System.currentTimeMillis() / 1000D * ClassicBarsConfig.transitionSpeed.get();
        double drinkAlpha = Math.sin(time) / 2 + .5;

        float thirstLevel = barInfo.numerator().getValue(player);
        float potentialThirstLevel = getPotentialHealing(drink,player);
        double restoredThirstLevel = Math.min(maxThirstLevel - thirstLevel, potentialThirstLevel);
        if (thirstLevel < maxThirstLevel) {
            double barWidth = BarOverlayImpl.getWidth(thirstLevel + restoredThirstLevel, maxThirstLevel);
            double barXStart = x + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
            renderPartialBar(getPrimaryBarColor(player).withAlpha((float) drinkAlpha),stack,barXStart + 2, y + 2, barWidth);
        }

        if (showSaturation) {
            float hydrationLevel = getSaturationValue(player);
            float potentialHydrationLevel = getPotentialSaturation(drink, player);
            double restoredHydrationLevel = Math.min(maxHydrationLevel - hydrationLevel, potentialHydrationLevel);

            // Potential hydration level cannot go over (current thirst level + potential thirst level)
            restoredHydrationLevel = Math.min(restoredHydrationLevel, thirstLevel + potentialThirstLevel);
            restoredHydrationLevel = Math.min(restoredHydrationLevel, thirstLevel + restoredThirstLevel);

            if ((hydrationLevel + potentialHydrationLevel) > (thirstLevel + restoredThirstLevel)) {
                double diff = (hydrationLevel + potentialHydrationLevel) - (thirstLevel + restoredThirstLevel);
                restoredHydrationLevel = potentialHydrationLevel - diff;
            }
            double barWidth = BarOverlayImpl.getWidth(hydrationLevel + restoredHydrationLevel, maxHydrationLevel);
            double barXStart = x + (getSide() == BarSide.RIGHT ? BarOverlayImpl.WIDTH - barWidth : 0);
            renderPartialBar(getSecondaryBarColor(player).withAlpha((float)drinkAlpha),stack,barXStart + 2, y + 2, barWidth);
        }
    }

    public abstract float getSaturationValue(Player player);

    public abstract int getPotentialHealing(ItemStack stack,Player player);

    public abstract float getPotentialSaturationMultiplier(ItemStack stack,Player player);

    public float getPotentialSaturation(ItemStack stack,Player player) {
        return getPotentialHealing(stack,player) * getPotentialSaturationMultiplier(stack, player) * 2;
    }

    //thirst/food
    public Color getPrimaryBarColor(Player player) {
        return getBarSettings().colorProvider().getColor(player,barInfo.getRatio(player),0);
    }

    //hyration/saturation
    public Color getSecondaryBarColor(Player player) {
        return getBarSettings().colorProvider().getColor(player,barInfo.getRatio(player),1);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int vOffset) {
        int xStart = graphics.guiWidth() / 2 + getIconOffset();
        int yStart = graphics.guiHeight() - vOffset;
        //draw hunger amount
        double hunger = barInfo.numerator().getValue(player);
        int c = getPrimaryBarColor(player).colorToText();
        textHelper(graphics,xStart,yStart,hunger,c);
    }

    @Override
    public Codec<? extends BarOverlay> codec() {
        return codec;
    }
}

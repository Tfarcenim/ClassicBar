package tfar.classicbar.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

import static tfar.classicbar.util.ModUtils.*;

public abstract class BarOverlayImpl implements BarOverlay {

    protected String name;
    protected boolean side;

    public BarOverlayImpl(String name) {
        this.name = name;
    }

    public boolean shouldRender(Player player) {
        return true;
    }

    @Override
    public final boolean rightHandSide() {
        return side;
    }

    @Override
    public final BarOverlay setSide(boolean right) {
        side = right;
        return this;
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
        if (shouldRender(player)) {
            gui.setupOverlayRenderState(true, false, ModUtils.ICON_BAR);
            renderBar(gui, stack, player, screenWidth, screenHeight, vOffset);
            if (shouldRenderText()) {
                renderText(stack, player, screenWidth, screenHeight, vOffset);
            }
            if (ConfigCache.icons) {
                bindIconTexture();
                renderIcon(stack, player, screenWidth, screenHeight, vOffset);
            }
            EventHandler.increment(gui, rightHandSide(), 10);
        }
    }

    public abstract void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset);

    public boolean shouldRenderText() {
        return true;
    }

    public abstract void renderText(PoseStack stack, Player player, int width, int height, int vOffset);

    public abstract void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset);

    public int getHOffset() {
        return rightHandSide() ? 10 : -91;
    }

    public int getIconOffset() {
        return rightHandSide() ? 92 : -101;
    }

    protected HealthEffect getHealthEffect(Player player) {
        HealthEffect effects = HealthEffect.NONE;//16
        if (player.hasEffect(MobEffects.POISON)) effects = HealthEffect.POISON;//evaluates to 52
        else if (player.hasEffect(MobEffects.WITHER)) effects = HealthEffect.WITHER;//evaluates to 88
        return effects;
    }
    @Override
    public double getBarWidth(Player player) {
        return ModUtils.WIDTH;
    }

    public void renderBarBackground(PoseStack matrices, Player player, int screenWidth, int screenHeight, int vOffset) {

        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && rightHandSide()) {
            xStart += ModUtils.WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            ModUtils.drawScaledBarBackground1(matrices, barWidth, xStart, yStart + 1, rightHandSide());
        } else drawTexturedModalRect(matrices, xStart, yStart, 0, 0, ModUtils.WIDTH + 4, 9);
    }

    public void textHelper(PoseStack stack,int xStart,int yStart,double stat, int color) {
        int i1 = (int) Math.floor(stat);
        int i2 = ConfigCache.icons ? 1 : 0;

        if (rightHandSide()) {
            ModUtils.drawStringOnHUD(stack, i1 + "", xStart + 9 * i2, yStart - 1, color);
        } else {
            int i3 = getStringLength(i1 + "");
            ModUtils.drawStringOnHUD(stack, i1 + "", xStart - 9 * i2 - i3 - leftTextOffset, yStart - 1, color);
        }
    }

    public void renderFullBarBackground(PoseStack matrices, int xStart, int yStart) {
        ModUtils.drawTexturedModalRect(matrices, xStart, yStart, 0, 0, ModUtils.WIDTH + 4, 9);
    }

    public void renderMainBar(PoseStack matrices, double xStart, int yStart, double barWidth) {
        drawTexturedModalRect(matrices, xStart + 2, yStart + 2, 2, 11, barWidth, 7);
    }

    public void renderSecondaryBar(PoseStack matrices, double xStart, int yStart, double barWidth) {
        drawTexturedModalRect(matrices, xStart + 2, yStart + 2, 2, 11, barWidth, 7);
    }

    public void renderCompleteSecondaryBar(PoseStack matrices, double xStart, int yStart) {
        renderSecondaryBar(matrices,xStart,yStart,ModUtils.WIDTH);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.BLACK;
    }

    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return Color.BLACK;
    }

    @Override
    public ResourceLocation getIconRL() {
        return GuiComponent.GUI_ICONS_LOCATION;
    }

    @Override
    public boolean isFitted() {
        return false;
    }

    @Override
    public final String name() {
        return name;
    }
}

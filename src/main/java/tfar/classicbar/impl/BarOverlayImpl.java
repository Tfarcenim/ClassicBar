package tfar.classicbar.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.EventHandler;
import tfar.classicbar.util.ModUtils;
import tfar.classicbar.api.BarOverlay;

import static tfar.classicbar.util.ModUtils.drawTexturedModalRect;

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
            gui.setupOverlayRenderState(true,false, ModUtils.ICON_BAR);
            renderBar(gui, stack, player, screenWidth, screenHeight, vOffset);
            if (shouldRenderText()) {
                renderText(stack,player,screenWidth,screenHeight,vOffset);
            }
            if (EventHandler.icons) {
                bindIconTexture();
                renderIcon(stack, player, screenWidth, screenHeight, vOffset);
            }
            EventHandler.increment(gui,rightHandSide(),10);
        }
    }

    public abstract void renderBar(ForgeIngameGui gui,PoseStack stack,Player player, int screenWidth, int screenHeight,int vOffset);

    public boolean shouldRenderText() {
        return true;
    }
    public abstract void renderText(PoseStack stack,Player player, int width, int height,int vOffset);
    public abstract void renderIcon(PoseStack stack,Player player, int width, int height,int vOffset);

    public int getHOffset() {
        return rightHandSide() ? 10 : -91;
    }

    @Override
    public int getBarWidth(Player player) {
        return ModUtils.WIDTH;
    }

    public void renderBarBackground(PoseStack matrices, Player player, int screenWidth, int screenHeight, int vOffset) {

        int barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && rightHandSide()) {
            xStart += ModUtils.WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            ModUtils.drawScaledBarBackground1(matrices,barWidth,  xStart, yStart + 1, rightHandSide());
        } else drawTexturedModalRect(matrices,xStart, yStart, 0, 0, 81, 9);
    }

    public void renderMainBar(PoseStack matrices, Player player, int screenWidth, int screenHeight, int vOffset) {

    }

    @Override
    public int getPrimaryBarColor(int index) {
        return 0;
    }

    @Override
    public int getSecondaryBarColor(int index) {
        return 0;
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

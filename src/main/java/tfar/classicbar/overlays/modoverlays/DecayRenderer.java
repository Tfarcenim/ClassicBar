package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.CapabilityRegistry;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

/*
    Class handles the drawing of the Betweenlands Decay bar
 */

public class DecayRenderer implements IBarOverlay {

    private static final ResourceLocation DECAY_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/decay_bar.png");

    public boolean side;

    @Override
    public IBarOverlay setSide(boolean side) {
        this.side = side;
        return this;
    }

    @Override
    public boolean rightHandSide() {
        return side;
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId == player.dimension;
    }

    @Override
    public void renderBar(EntityPlayer player, int width, int height) {
        IDecayCapability decayCap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
        double decay = 20 - decayCap.getDecayStats().getDecayLevel();
        //Push to avoid lasting changes

        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        mc.profiler.startSection("decay");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        Color.reset();
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
        //draw portion of bar based on decay amount
        float f = xStart+79-getWidth(decay,20);
        hex2Color("#81552D"/*mods.thirstBarColor*/).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(decay,20), 7);

        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }

    @Override
    public boolean shouldRenderText() {
        return numbers.showThirstNumbers;
    }

    @Override
    public void renderText(EntityPlayer player, int width, int height) {
        //draw decay amount

        IDecayCapability decayCap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
        double decay = 20 - decayCap.getDecayStats().getDecayLevel();

        int h1 = (int) Math.floor(decay);
        int c = Integer.decode("#81552D"/*mods.thirstBarColor*/);
        if (numbers.showPercent)h1 = (int)decay*5;
        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        drawStringOnHUD(h1 + "", xStart + 9 * ((general.displayIcons) ? 1 : 0) + rightTextOffset, yStart - 1, c);
    }

    @Override
    public void renderIcon(EntityPlayer player, int width, int height) {

        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        //Draw thirst icon
        mc.getTextureManager().bindTexture(DECAY_BAR_TEXTURE);

        drawTexturedModalRect(xStart + 82, yStart, 18, 0, 9,9);
        drawTexturedModalRect(xStart + 82, yStart, 0, 0, 9, 9);
    }

    @Override
    public String name() {
        return "decay";
    }
}
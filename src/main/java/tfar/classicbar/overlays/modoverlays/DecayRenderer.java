package tfar.classicbar.overlays.modoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import tfar.classicbar.Color;
import tfar.classicbar.compat.Decay;
import tfar.classicbar.overlays.IBarOverlay;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.config.BetweenlandsConfig;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

/*
    Class handles the drawing of the Betweenlands Decay bar
 */

public class DecayRenderer implements IBarOverlay {

    private static final ResourceLocation DECAY_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/decay_bar.png");

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId == player.dimension;
    }

    @Override
    public void render(EntityPlayer player, int width, int height) {
        IDecayCapability decayCap = Decay.getDecayHandler(player);
        double decay = 20 - decayCap.getDecayStats().getDecayLevel();
        //Push to avoid lasting changes

        int xStart = width / 2 + 10;
        int yStart = height - GuiIngameForge.right_height;
        mc.profiler.startSection("decay");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on decay amount

        float f = xStart+79-getWidth(decay,20);
        hex2Color("#81552D"/*mods.thirstBarColor*/).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(decay,20), 7);

        //draw decay amount
        int h1 = (int) Math.floor(decay);
        int c = Integer.decode("#81552D"/*mods.thirstBarColor*/);
        if (numbers.showPercent)h1 = (int)decay*5;
        if (numbers.showThirstNumbers)drawStringOnHUD(h1 + "", xStart + 9 * ((general.displayIcons) ? 1 : 0) + rightTextOffset, yStart - 1, c);
        //Reset back to normal settings
        Color.reset();


        if (general.displayIcons) {
            //Draw thirst icon
            mc.getTextureManager().bindTexture(DECAY_BAR_TEXTURE);

            drawTexturedModalRect(xStart + 82, yStart, 18, 0, 9,9);
            drawTexturedModalRect(xStart + 82, yStart, 0, 0, 9, 9);
        }
        //Revert our state back
        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }

    @Override
    public String name() {
        return "decay";
    }
}
package tfar.classicbar.overlays.modoverlays;

import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

/*
    Class handles the drawing of the Betweenlands Decay bar
 */

public class VampireRenderer implements IBarOverlay {

    private static final ResourceLocation VAMPIRISM_ICONS =  new ResourceLocation("vampirism:textures/gui/icons.png");


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
        return Helper.isVampire(player);
    }

    @Override
    public void renderBar(EntityPlayer player, int width, int height) {
        IBloodStats stats = VampirePlayer.get(player).getBloodStats();
        double blood = stats.getBloodLevel();
        //Push to avoid lasting changes
        int maxBlood = stats.getMaxBlood();

        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        mc.profiler.startSection("blood");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        Color.reset();
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
        //draw portion of bar based on blood amount
        float f = xStart+79-getWidth(blood,20);
        hex2Color("#FF0000"/*mods.thirstBarColor*/).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(blood,20), 7);

        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }

    @Override
    public boolean shouldRenderText() {
        return numbers.showThirstNumbers;
    }

    @Override
    public void renderText(EntityPlayer player, int width, int height) {
        //draw blood amount

        IBloodStats stats = VampirePlayer.get(player).getBloodStats();
        int blood = stats.getBloodLevel();

        int h1 = blood;
        int c = Integer.decode("#FF0000"/*mods.thirstBarColor*/);
        if (numbers.showPercent)h1 = (int)blood*5;
        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        drawStringOnHUD(h1 + "", xStart + 9 * ((general.displayIcons) ? 1 : 0) + rightTextOffset, yStart - 1, c);
    }

    @Override
    public void renderIcon(EntityPlayer player, int width, int height) {

        int xStart = width / 2 + 10;
        int yStart = height - getSidedOffset();
        //Draw thirst icon
        mc.getTextureManager().bindTexture(VAMPIRISM_ICONS);
        GlStateManager.enableBlend();

        drawTexturedModalRect(xStart + 82, yStart, 0, 0, 9,9);
        drawTexturedModalRect(xStart + 82, yStart, 9, 0, 9, 9);
    }

    @Override
    public String name() {
        return "blood";
    }
}
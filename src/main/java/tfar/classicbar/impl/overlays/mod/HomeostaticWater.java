package tfar.classicbar.impl.overlays.mod;

import homeostatic.common.attachments.WaterData;
import homeostatic.network.IWater;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

import java.util.Optional;

// Changed: IWater (homeostatic.network.IWater) confirmed from jar.
// Water level: getWaterLevel() -> int 0-20. Saturation: getWaterSaturationLevel() -> float (not getHydration()).
public class HomeostaticWater extends BarOverlayImpl {

    public static final String NAME = "homeostatic_water";
    public static final double MAX_WATER = 20.0;
    public static final double MAX_HYDRATION = 1.0;

    public HomeostaticWater() {
        super(NAME);
    }

    @Override
    public boolean shouldRender(Player player) {
        return ModCompat.homeostatic.loaded;
    }

    @Override
    public void renderBar(Gui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        Optional<? extends IWater> waterOpt = WaterData.getData(player);
        if (waterOpt.isEmpty()) return;
        IWater waterData = waterOpt.get();

        int waterLevel = waterData.getWaterLevel();
        double hydration = Math.min(waterData.getWaterSaturationLevel(), MAX_HYDRATION);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        drawWater(graphics, xStart, yStart, waterLevel);

        if (hydration > 0 && ClassicBarsConfig.showHydrationBar.get()) {
            drawHydration(graphics, xStart, yStart, hydration);
        }
    }

    private void drawWater(GuiGraphics graphics, int x, int y, int waterLevel) {
        getSecondaryBarColor(0, null).color2Gl();
        double barWidth = ModUtils.getWidth(waterLevel, MAX_WATER);
        double barXStart = x + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(graphics, barXStart + 2, y + 2, barWidth);
    }

    private void drawHydration(GuiGraphics graphics, int x, int y, double hydration) {
        getPrimaryBarColor(0, null).color2Gl();
        double barWidth = ModUtils.getWidth(hydration, MAX_HYDRATION);
        double barXStart = x + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        renderPartialBar(graphics, barXStart + 2, y + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        Optional<? extends IWater> waterOpt = WaterData.getData(player);
        if (waterOpt.isEmpty()) return 0;
        return Math.ceil(BarOverlayImpl.WIDTH * waterOpt.get().getWaterLevel() / MAX_WATER);
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        Optional<? extends IWater> waterOpt = WaterData.getData(player);
        if (waterOpt.isEmpty()) return;
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int c = getSecondaryBarColor(0, player).colorToText();
        textHelper(graphics, xStart, yStart, waterOpt.get().getWaterLevel(), c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // Homeostatic icons.png is 256x256; fully filled blue water droplet at (0, 0) Has background but best icon available 
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 0, 0, 9, 9);
    }

    /** hydration */
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return ConfigCache.homeostaticHydration;
    }

    /** water level */
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        return ConfigCache.homeostaticWater;
    }
}
